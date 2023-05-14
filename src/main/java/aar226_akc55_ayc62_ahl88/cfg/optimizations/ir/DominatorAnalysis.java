package aar226_akc55_ayc62_ahl88.cfg.optimizations.ir;

import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.cfg.HashSetInf;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.ReplaceTempsWithTemps;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.InternalCompilerError;

import java.util.*;

public class DominatorAnalysis extends ForwardIRDataflow<HashSetInf<CFGNode<IRStmt>>>{

    HashMap<CFGNode<IRStmt>,HashSet<CFGNode<IRStmt>>> dominatorTree;

    HashMap<CFGNode<IRStmt>,CFGNode<IRStmt>> immediateDominator;

    HashMap<CFGNode<IRStmt>,HashSet<CFGNode<IRStmt>>> dominanceFrontier;

    HashSet<IRTemp> variables;
//    HashMap<CFGNode<IRStmt>, HashSet<IRTemp>> phiPlaced;

    HashMap<CFGNode<IRStmt>, HashMap<IRTemp,CFGNode<IRStmt>>> phiPlacedNodes;
    public DominatorAnalysis(CFGGraph<IRStmt> graph) {
        super(graph,
                (n,inN)->{
                    if (inN.isInfSize()){
                        return inN;
                    }
                    HashSetInf<CFGNode<IRStmt>> outN = new HashSetInf<>(false);
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
                    HashSetInf<CFGNode<IRStmt>> res = new HashSetInf<>(l1);
                    res.retainAll(l2);
                    return res;
                },
                () -> new HashSetInf<>(true),
                new HashSetInf<>(true));
        HashSetInf<CFGNode<IRStmt>> startOutN = new HashSetInf<>(false);
        CFGNode<IRStmt> startNode = graph.getNodes().get(0);
        startOutN.add(startNode);
        getOutMapping().put(startNode,startOutN);
        dominatorTree = new HashMap<>();
        immediateDominator = new HashMap<>();
        dominanceFrontier = new HashMap<>();
    }
    @Override
    public void worklist() {
        HashSet<CFGNode<IRStmt>> set = new HashSet<>(graph.getNodes());
        Queue<CFGNode<IRStmt>> queue = new ArrayDeque<>(graph.reversePostorder());
        // Don't execute first start element Brittle
        queue.poll();
        while (!queue.isEmpty()) {
            CFGNode<IRStmt> node = queue.poll();
            set.remove(node);

            HashSetInf<CFGNode<IRStmt>> oldOut = outMapping.get(node);
            HashSetInf<CFGNode<IRStmt>> newOut = applyTransfer(node);

            if (!oldOut.equals(newOut)) {
                for (CFGNode<IRStmt> succ : node.getChildren()) {
                    if (succ != null && !set.contains(succ)) {
                        set.add(succ);
                        queue.add(succ);
                    }
                }
            }
        }
    }

    public HashMap<CFGNode<IRStmt>, HashSet<CFGNode<IRStmt>>> getDominatorTree() {
        return dominatorTree;
    }

    public HashMap<CFGNode<IRStmt>, CFGNode<IRStmt>> getImmediateDominator() {
        return immediateDominator;
    }

    public void createDominatorTreeAndImmediate(){
        HashMap<CFGNode<IRStmt>,HashSetInf<CFGNode<IRStmt>>> copyOfIndegree = new HashMap<>();
        for (CFGNode<IRStmt> node : outMapping.keySet()){
            copyOfIndegree.put(node,new HashSetInf<>(outMapping.get(node)));
        }
        // Remove yourself
        for (CFGNode<IRStmt> node: copyOfIndegree.keySet()){
            copyOfIndegree.get(node).remove(node);
            dominatorTree.put(node,new HashSet<>());
        }
        HashSet<CFGNode<IRStmt>> visited = new HashSet<>();
        Queue<CFGNode<IRStmt>> queue = new ArrayDeque<>();
        queue.add(graph.getNodes().get(0));
        immediateDominator.put(graph.getNodes().get(0),null);
        visited.add(graph.getNodes().get(0));
        while (!queue.isEmpty()){
            CFGNode<IRStmt> top = queue.remove();
            for (CFGNode<IRStmt> node: copyOfIndegree.keySet()){
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

    public HashMap<CFGNode<IRStmt>, HashSet<CFGNode<IRStmt>>> getDominanceFrontier() {
        return dominanceFrontier;
    }

    public void constructDF(){
        computeDF(graph.getNodes().get(0));
    }
    private void computeDF(CFGNode<IRStmt> node){
        HashSet<CFGNode<IRStmt>> S = new HashSet<>();
        for (CFGNode<IRStmt> succ : node.getChildren()){
            if (succ != null && immediateDominator.get(succ) != node){
                S.add(succ);
            }
        }
        for (CFGNode<IRStmt> domChild: dominatorTree.get(node)){
            computeDF(domChild);
            for (CFGNode<IRStmt> w : dominanceFrontier.get(domChild)){
                if (!(dominatorTree.get(node).contains(w) || node == w)){
                    S.add(w);
                }
            }
        }
        dominanceFrontier.put(node,S);
    }

    public void placePhiFunctions(){
        phiPlacedNodes = new HashMap();
        HashMap<IRTemp,HashSet<CFGNode<IRStmt>>> defsites = new HashMap<>();
        for (CFGNode<IRStmt> node : graph.getNodes()){
            Set<IRTemp> tempsDefined =  LiveVariableAnalysis.def(node.getStmt());
            for (IRTemp t: tempsDefined){
                if (!defsites.containsKey(t)){
                    defsites.put(t, new HashSet<>());
                }
                defsites.get(t).add(node);
            }
            phiPlacedNodes.put(node,new HashMap<>());
        }
        for (IRTemp a: defsites.keySet()){
            Queue<CFGNode<IRStmt>> queue = new ArrayDeque<>(defsites.get(a));
            while (!queue.isEmpty()){
                CFGNode<IRStmt> node = queue.poll();
                for (CFGNode<IRStmt> y : dominanceFrontier.get(node)){
                    if (!phiPlacedNodes.get(y).containsKey(a)){
                        ArrayList<IRExpr> nums = new ArrayList<>();
                        for (int i = 0; i < y.getPredecessors().size();i++){
                            nums.add(new IRTemp(a.name()));
                        }
                        IRPhi phi = new IRPhi(new IRTemp(a.name()),nums); // org a-> b dest a-> new -> b
                        CFGNode<IRStmt> phiCFG = new CFGNode<>(phi);
                        phiPlacedNodes.get(y).put(a,phiCFG); // Aphi[y] <- Aphi[y] U a
                        CFGNode<IRStmt> orgLabelSucc = y.getFallThroughChild(); //b
                        if (orgLabelSucc != null) {
                            orgLabelSucc.removePredecessor(y); // remove  a|->| b

                            y.setFallThroughChild(phiCFG); // a -> new
                            phiCFG.addPredecessor(y);

                            phiCFG.setFallThroughChild(orgLabelSucc); // new -> b
                            orgLabelSucc.addPredecessor(phiCFG);
                        }else{ // a-> null
                            y.setFallThroughChild(phiCFG); // a-> b
                            phiCFG.addPredecessor(y);
                        }
                        graph.getNodes().add(graph.getNodes().indexOf(y),phiCFG);// Insert into graph
                        Set<IRTemp> tempsY =  LiveVariableAnalysis.def(y.getStmt());
                        if (!tempsY.contains(a)){
                            queue.add(y);
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
//        HashMap<CFGNode<IRStmt>,Integer> phiToindex = new HashMap<>();
        for (IRTemp a : variables){
            count.put(a,0);
            stacks.put(a,new Stack<>());
            stacks.get(a).push(0);
        }
//        for (CFGNode<IRStmt> node : graph.getNodes()){
//            if (node.getStmt() instanceof IRPhi){
//                phiToindex.put(node,0);
//            }
//        }
        rename(graph.getNodes().get(0),count,stacks);
    }

    public void rename(CFGNode<IRStmt> node,HashMap<IRTemp, Integer> count,HashMap<IRTemp,Stack<Integer>> stacks){
        // block is single node
        Set<IRTemp> used = LiveVariableAnalysis.use(node.getStmt());
        Set<IRTemp> defs = LiveVariableAnalysis.def(node.getStmt());
        used.retainAll(count.keySet());
        defs.retainAll(count.keySet());
        System.out.println("node: " + node);
        System.out.println("defs: "  + defs);
        System.out.println("used: " + used);
        if (!(node.getStmt() instanceof IRPhi)){
            HashMap<String,String> replaceUsedMapping = new HashMap<>();
            for (IRTemp t : used){
                replaceUsedMapping.put(t.name(),t.name() +"_"+ stacks.get(t).peek());
            }

            IRStmt afterUsedSwap =  replaceRHS(node.getStmt(),replaceUsedMapping);
            node.setStmt(afterUsedSwap);
        }
        HashMap<String,String> replaceDefMapping = new HashMap<>();
        for (IRTemp t : defs){
            count.put(t,count.get(t)+1);
            replaceDefMapping.put(t.name(),t.name() +"_"+ count.get(t));
            stacks.get(t).push(count.get(t));
        }
        IRStmt afterDefSwap = replaceLHS(node.getStmt(),replaceDefMapping);

        node.setStmt(afterDefSwap);

        // check flow
        for (CFGNode<IRStmt> childInCFG : node.getChildren()){
            if (childInCFG != null){
                if (!phiPlacedNodes.containsKey(childInCFG)){
                    phiPlacedNodes.put(childInCFG,new HashMap<>());
                }
                for (Map.Entry<IRTemp,CFGNode<IRStmt>> kv:  phiPlacedNodes.get(childInCFG).entrySet()){
                    int indexOfPred = childInCFG.getPredecessors().indexOf(node);
                    String newName = kv.getKey().name() +"_"+  stacks.get(kv.getKey()).peek();
                    replacePHIIndex((IRPhi) kv.getValue().getStmt(),newName,indexOfPred);
                }
            }
        }

        for (CFGNode<IRStmt> childInDom : dominatorTree.get(node)){
            rename(childInDom,count,stacks);
        }

        for (IRTemp t : defs){
            stacks.get(t).pop();
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
            System.out.println(phi);
            System.out.println(mapping);
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
}
