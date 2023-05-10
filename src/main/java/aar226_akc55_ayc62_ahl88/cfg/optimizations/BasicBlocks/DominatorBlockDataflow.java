package aar226_akc55_ayc62_ahl88.cfg.optimizations.BasicBlocks;

import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.cfg.HashSetInf;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.ir.LiveVariableAnalysis;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.ReplaceTempsWithTemps;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.InternalCompilerError;

import java.util.*;

public class DominatorBlockDataflow extends ForwardBlockDataflow<HashSetInf<BasicBlockCFG>> {

    HashMap<BasicBlockCFG,HashSet<BasicBlockCFG>> dominatorTree;

    HashMap<BasicBlockCFG,BasicBlockCFG> immediateDominator;

    HashMap<BasicBlockCFG,HashSet<BasicBlockCFG>> dominanceFrontier;

    HashSet<IRTemp> variables;
    HashMap<BasicBlockCFG, HashMap<IRTemp,IRPhi>> phiPlacedNodes;

    public HashMap<String,String> retArgsReverseMapping;
    public DominatorBlockDataflow(CFGGraphBasicBlock graph) {
        super(graph,
                (n,inN)->{
                    if (inN.isInfSize()){
                        return inN;
                    }
                    HashSetInf<BasicBlockCFG> outN = new HashSetInf<>(false);
                    outN.addAll(inN);
                    outN.add(n);
                    return outN;
                },
                (l1,l2) -> {
                    if (l1.isInfSize()){
                        return l2;
                    }
                    if (l2.isInfSize()){
                        return l1;
                    }
                    HashSetInf<BasicBlockCFG> res = new HashSetInf<>(l1);
                    res.retainAll(l2);
                    return res;
                },
                () -> new HashSetInf<>(true),
                new HashSetInf<>(true));
        HashSetInf<BasicBlockCFG> startOutN = new HashSetInf<>(false);
        BasicBlockCFG startNode = graph.getNodes().get(0);
        startOutN.add(startNode);
        getOutMapping().put(startNode,startOutN);
        dominatorTree = new HashMap<>();
        immediateDominator = new HashMap<>();
        dominanceFrontier = new HashMap<>();
        retArgsReverseMapping = new HashMap<>();
    }

    public void convertToSSA(){
        worklist();
        createDominatorTreeAndImmediate();
        constructDF();
        placePhiFunctions();
        renamingVariables();
    }

    public void createAndExecuteDF(){
        worklist();
        createDominatorTreeAndImmediate();
        constructDF();
    }
    @Override
    public void worklist() {
        HashSet<BasicBlockCFG> set = new HashSet<>(graph.getNodes());
        Queue<BasicBlockCFG> queue = new ArrayDeque<>(graph.reversePostorder());
        // Don't execute first start element Brittle
        queue.poll();
        while (!queue.isEmpty()) {
            BasicBlockCFG node = queue.poll();
            set.remove(node);

            HashSetInf<BasicBlockCFG> oldOut = outMapping.get(node);
            HashSetInf<BasicBlockCFG> newOut = applyTransfer(node);

            if (!oldOut.equals(newOut)) {
                for (BasicBlockCFG succ : node.getChildren()) {
                    if (succ != null && !set.contains(succ)) {
                        set.add(succ);
                        queue.add(succ);
                    }
                }
            }
        }
    }

    public HashMap<BasicBlockCFG, HashSet<BasicBlockCFG>> getDominatorTree() {
        return dominatorTree;
    }

    public HashMap<BasicBlockCFG, BasicBlockCFG> getImmediateDominator() {
        return immediateDominator;
    }

    public void createDominatorTreeAndImmediate(){
        HashMap<BasicBlockCFG,HashSetInf<BasicBlockCFG>> copyOfIndegree = new HashMap<>();
        for (BasicBlockCFG node : outMapping.keySet()){
            copyOfIndegree.put(node,new HashSetInf<>(outMapping.get(node)));
        }
        // Remove yourself
        for (BasicBlockCFG node: copyOfIndegree.keySet()){
            copyOfIndegree.get(node).remove(node);
            dominatorTree.put(node,new HashSet<>());
        }
        HashSet<BasicBlockCFG> visited = new HashSet<>();
        Queue<BasicBlockCFG> queue = new ArrayDeque<>();
        queue.add(graph.getNodes().get(0));
        immediateDominator.put(graph.getNodes().get(0),null);
        visited.add(graph.getNodes().get(0));
        while (!queue.isEmpty()){
            BasicBlockCFG top = queue.remove();
            for (BasicBlockCFG node: copyOfIndegree.keySet()){
                copyOfIndegree.get(node).remove(top);
                if (!visited.contains(node) && copyOfIndegree.get(node).size() == 0){
                    visited.add(node);
                    queue.add(node);
                    immediateDominator.put(node,top);
                    dominatorTree.get(top).add(node);
                }
            }
        }
    }
//
    public HashMap<BasicBlockCFG, HashSet<BasicBlockCFG>> getDominanceFrontier() {
        return dominanceFrontier;
    }
//
    public void constructDF(){
        computeDF(graph.getNodes().get(0));
    }
    private void computeDF(BasicBlockCFG node){
        HashSet<BasicBlockCFG> S = new HashSet<>();
        for (BasicBlockCFG succ : node.getChildren()){
            if (succ != null && immediateDominator.get(succ) != node){
                S.add(succ);
            }
        }
        for (BasicBlockCFG domChild: dominatorTree.get(node)){
            computeDF(domChild);
            for (BasicBlockCFG w : dominanceFrontier.get(domChild)){
                if (!(dominatorTree.get(node).contains(w) || node == w)){
                    S.add(w);
                }
            }
        }
        dominanceFrontier.put(node,S);
    }

    public void placePhiFunctions(){
        phiPlacedNodes = new HashMap<>();
        HashMap<IRTemp,HashSet<BasicBlockCFG>> defsites = new HashMap<>();
        HashMap<BasicBlockCFG,HashSet<IRTemp>> Aorg = new HashMap<>();
        for (BasicBlockCFG node : graph.getNodes()){
            Set<IRTemp> tempsDefined =  node.dataflowDef();
            for (IRTemp t: tempsDefined){
                if (!defsites.containsKey(t)){
                    defsites.put(t, new HashSet<>());
                }
                defsites.get(t).add(node);
            }
            Aorg.put(node,new HashSet<>(tempsDefined));
            phiPlacedNodes.put(node,new HashMap<>());
        }
//        Set<IRTemp> noReplace = new HashSet<>();
//        for (IRTemp use: defsites.keySet()){
//            if (use.name().startsWith("_RV") || use.name().startsWith("_ARG")){
//                noReplace.add(use);
//            }
//        }
//        for (IRTemp retArg :  noReplace){
//            defsites.remove(retArg);
//        }
        for (IRTemp a: defsites.keySet()){
            Queue<BasicBlockCFG> queue = new ArrayDeque<>(defsites.get(a));
//            System.out.println("starting: " + a);
            while (!queue.isEmpty()){
                BasicBlockCFG node = queue.poll();
//                System.out.println("curBlock: " + node);
                for (BasicBlockCFG y : dominanceFrontier.get(node)){
                    if (!phiPlacedNodes.get(y).containsKey(a)){
                        ArrayList<IRExpr> nums = new ArrayList<>();
                        for (int i = 0; i < y.getPredecessors().size();i++){
                            nums.add(new IRTemp(a.name()));
                        }
                        IRPhi phi = new IRPhi(new IRTemp(a.name()),nums);

                        phiPlacedNodes.get(y).put(a,phi); // Aphi[y] <- Aphi[y] U a
                        if (y.body.get(0).getStmt() instanceof IRLabel){
                            y.body.add(1,new CFGNode<>(phi));
                        }else{
                            y.body.add(0,new CFGNode<>(phi));
                        }
//                        System.out.println("inserted phi"+ a +"at: " + y);
                        if (!Aorg.get(y).contains(a)){
                            queue.add(y);
//                            System.out.println("added to queue: " + y);
                        }
                    }
                }
            }
        }
        variables = new HashSet<>(defsites.keySet());

    }
    public void renamingVariables(){
        HashMap<IRTemp, Integer> count = new HashMap<>();
        HashMap<IRTemp,Stack<Integer>> stacks = new HashMap<>();
        for (IRTemp a : variables){
            count.put(a,0);
            stacks.put(a,new Stack<>());
            stacks.get(a).push(0);
        }
        rename(graph.getNodes().get(0),count,stacks);
    }

    public void rename(BasicBlockCFG block,HashMap<IRTemp, Integer> count,HashMap<IRTemp,Stack<Integer>> stacks){
        ArrayList<CFGNode<IRStmt>> body = block.getBody();
        HashMap<Integer,Set<IRTemp>> orgDefs = new HashMap<>();
        for (int i = 0; i< block.getBody().size();i++){
            IRStmt stmt = body.get(i).getStmt();
            Set<IRTemp> used = LiveVariableAnalysis.use(stmt);
            Set<IRTemp> defs = LiveVariableAnalysis.def(stmt);
            used.retainAll(count.keySet());
            defs.retainAll(count.keySet());

//            Set<IRTemp> noReplace = new HashSet<>();
//            for (IRTemp use: used){
//                if (use.name().startsWith("_RV") || use.name().startsWith("_ARG")){
//                    noReplace.add(use);
//                }
//            }
//            used.removeAll(noReplace);
//            noReplace.clear();
//            for (IRTemp def: defs){
//                if (def.name().startsWith("_RV") || def.name().startsWith("_ARG")){
//                    noReplace.add(def);
//                }
//            }
//            defs.removeAll(noReplace);

            if (!(body.get(i).getStmt() instanceof IRPhi)){
                HashMap<String,String> replaceUsedMapping = new HashMap<>();
                for (IRTemp x : used){
                    if ( x.name().startsWith("_RV") || x.name().startsWith("_ARG")){
                        retArgsReverseMapping.put(x.name() +"_"+ stacks.get(x).peek(),x.name());
                    }
                    replaceUsedMapping.put(x.name(),x.name() +"_"+ stacks.get(x).peek());
                }
                IRStmt afterUsedSwap =  replaceRHS(stmt,replaceUsedMapping);
                block.getBody().get(i).setStmt(afterUsedSwap);
                stmt = body.get(i).getStmt();
            }
            HashMap<String,String> replaceDefMapping = new HashMap<>();
            for (IRTemp t : defs){
                count.put(t,count.get(t)+1);
                if ( t.name().startsWith("_RV") || t.name().startsWith("_ARG")){
                    retArgsReverseMapping.put(t.name() +"_"+ count.get(t),t.name());
                }
                replaceDefMapping.put(t.name(),t.name() +"_"+ count.get(t));
                stacks.get(t).push(count.get(t));
            }
            IRStmt afterDefSwap = replaceLHS(stmt,replaceDefMapping);
            block.getBody().get(i).setStmt(afterDefSwap);
            orgDefs.put(i,defs);
        }
        for (BasicBlockCFG succY: block.getChildren()){
            if (succY != null) {
                int indexPred = succY.getPredecessors().indexOf(block);
//            HashMap<BasicBlockCFG, HashMap<IRTemp,IRPhi>> phiPlacedNodes;
                for (Map.Entry<IRTemp, IRPhi> kv : phiPlacedNodes.get(succY).entrySet()) {
                    String newName = kv.getKey().name() + "_" + stacks.get(kv.getKey()).peek();
                    replacePHIIndex(kv.getValue(), newName, indexPred);
                }
            }
        }
        for (BasicBlockCFG childX : dominatorTree.get(block)){
            rename(childX,count,stacks);
        }
        for (int i = 0; i< block.getBody().size();i++){
            Set<IRTemp> defs = orgDefs.get(i);
            for (IRTemp a: defs){
                stacks.get(a).pop();
            }
        }
    }
    public static void replacePHIIndex(IRPhi stmt, String newName, int index){
        stmt.getArgs().set(index,new IRTemp(newName));
    }
    public static IRStmt replaceLHS(IRStmt stmt, HashMap<String,String> mapping){
        if (stmt instanceof IRMove move && move.target() instanceof IRTemp temp){
            IRExpr dest = (IRExpr) new ReplaceTempsWithTemps(new IRNodeFactory_c(),mapping).visit(move.target());
            return new IRMove(dest,move.source());
        }else if (stmt instanceof IRPhi phi){
            IRExpr dest = (IRExpr) new ReplaceTempsWithTemps(new IRNodeFactory_c(),mapping).visit(phi.getTarget());
            return new IRPhi(dest,phi.getArgs());
        }
        return stmt;
    }
    public static IRStmt replaceRHS(IRStmt stmt, HashMap<String,String> mapping){
        if (stmt instanceof IRPhi){
            throw new InternalCompilerError("don't do replaceRHS for PHI");
        }
        if (stmt instanceof IRMove irmove && irmove.target() instanceof IRTemp) {
            IRExpr source = (IRExpr) new ReplaceTempsWithTemps(new IRNodeFactory_c(),mapping).visit(irmove.source());
            return new IRMove(irmove.target(),source);
        }

        // MEM
        else if (stmt instanceof IRMove irmove && irmove.target() instanceof IRMem) {
            return (IRStmt) new ReplaceTempsWithTemps(new IRNodeFactory_c(),mapping).visit(irmove);
        }
        // JUMP
        else if (stmt instanceof IRCJump cjmp) {
            return (IRStmt) new ReplaceTempsWithTemps(new IRNodeFactory_c(),mapping).visit(cjmp);
        }
        // Return
        else if (stmt instanceof IRReturn ret){
            ret.rets().replaceAll(node -> (IRExpr) new ReplaceTempsWithTemps(new IRNodeFactory_c(), mapping).visit(node));
            return stmt;
        }else if (stmt instanceof IRCallStmt call){
            call.args().replaceAll(node -> (IRExpr) new ReplaceTempsWithTemps(new IRNodeFactory_c(), mapping).visit(node));
            return stmt;
        }else{
            return stmt;
        }
    }
    public static void unSSA(CFGGraphBasicBlock graph,HashMap<String,String> mapping){

        for (BasicBlockCFG block: graph.getNodes()){
            ArrayList<CFGNode<IRStmt>> nxtBody = new ArrayList<>();
            for (int z = 0; z< block.getBody().size();z++){
                CFGNode<IRStmt> node = block.getBody().get(z);
                IRStmt stmt = node.getStmt();
                if (stmt instanceof IRPhi phi){
//                    System.out.println(phi.getArgs().size());
//                    System.out.println(phi.toString().replaceAll("\n",""));
//                    System.out.println(block.getPredecessors().size());
                    for (int i = 0; i< block.getPredecessors().size();i++){
                        BasicBlockCFG pred = block.getPredecessors().get(i);
                        IRExpr use = phi.getArgs().get(i);
                        IRMove extraMove = new IRMove(phi.getTarget(),use);
                        if (pred.getBody().size() != 0) {
                            IRStmt lastStatementPred = pred.getBody().get(pred.getBody().size() - 1).getStmt();
                            if (lastStatementPred instanceof IRJump || lastStatementPred instanceof IRCJump) {
                                pred.getBody().add(pred.getBody().size() - 1, new CFGNode<>(extraMove));
                            } else {
                                pred.getBody().add(new CFGNode<>(extraMove));
                            }
                        }else{
                            pred.getBody().add(new CFGNode<>(extraMove));
                        }
                    }
                }else{
                    nxtBody.add(node);
                }
            }
            block.body = nxtBody;
        }
//        System.out.println(mapping);
        for (BasicBlockCFG block: graph.getNodes()){
            for (CFGNode<IRStmt> stmt : block.getBody()){
                IRStmt replaced = (IRStmt) new ReplaceTempsWithTemps(new IRNodeFactory_c(),mapping).visit(stmt.getStmt());
                stmt.setStmt(replaced);
            }
        }
    }
}
