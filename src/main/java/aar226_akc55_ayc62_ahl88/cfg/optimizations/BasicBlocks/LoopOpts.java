package aar226_akc55_ayc62_ahl88.cfg.optimizations.BasicBlocks;

import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.ir.LiveVariableAnalysis;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.FlattenIrVisitor;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.InternalCompilerError;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.Pair;

import java.util.*;

/**
 * Loop Optimization Class Does LICM at the Moment
 */
public class LoopOpts {

    private static class LoopWrapper{

        public boolean hasStores;
        public  ArrayList<IRNode> stores;
        public BasicBlockCFG preheader;
        public BasicBlockCFG header;

        public ArrayList<BasicBlockCFG> loopBody;

        public HashSet<BasicBlockCFG> exitBlocks;
        public HashMap<IRTemp,Set<CFGNode<IRStmt>>> definedTempsInLoop;

        public HashSet<IRTemp> loopInvariantDefs;
        public HashMap<IRTemp,CFGNode<IRStmt>> defToNode;
        public ArrayList<CFGNode<IRStmt>> allNodes;
        public ArrayList<CFGNode<IRStmt>> potentialLoopInvariantInstrs;
        public LoopWrapper(BasicBlockCFG head, ArrayList<BasicBlockCFG> body){
            preheader = new BasicBlockCFG();
            header = head;
            loopBody = body;
            exitBlocks = new HashSet<>();
            loopInvariantDefs = new HashSet<>();
            defToNode = new HashMap<>();
            hasStores = false;
            allNodes = getAllNodesInLoop();
            stores = new ArrayList<>();
        }

        public ArrayList<BasicBlockCFG> getAllBlocksInLoop(){
            ArrayList<BasicBlockCFG> res = new ArrayList<>();
            res.add(header);
            res.addAll(loopBody);
            return res;
        }

        public ArrayList<CFGNode<IRStmt>> getAllNodesInLoop(){
            ArrayList<CFGNode<IRStmt>> res = new ArrayList<>();
            for (BasicBlockCFG block : getAllBlocksInLoop()){
                res.addAll(block.getBody());
            }
            return res;
        }
        @Override
        public String toString() {
            return "LoopWrapper{" +
                    "header=" + header +
                    ", loopBody=" + loopBody +
                    '}';
        }
    }
    public CFGGraphBasicBlock graph;

    public DominatorBlockDataflow dom;


    public ReachingDefinitionsBlock reach;
    public ArrayList<Pair<BasicBlockCFG,BasicBlockCFG>> backEdges;

    public ArrayList<LoopWrapper> all_loops;
    public HashMap<BasicBlockCFG,LoopWrapper> headerToLoop;
    public LiveVariableAnalysisBlocks lva;

    private long labelCnt;
    private long tempCnt;
    private String func;

    private String nxtLabel() {
        return String.format("_loop_" +func +  "%d", (labelCnt++));
    }

    private String nxtTemp() {
        return String.format("_loopt" + func + "%d", (tempCnt++));
    }
    public LoopOpts(CFGGraphBasicBlock g,String funcName){
        func = funcName;
        labelCnt = 0;
        tempCnt = 0;
        graph = g;
        dom = new DominatorBlockDataflow(g);
        dom.createAndExecuteDF();
        backEdges = new ArrayList<>();
        all_loops = new ArrayList<>();
        headerToLoop = new HashMap<>();
        findBackEdges();
        findLoops();
        mergeLoops();
        insertPreHeaderNoSSA();
        reorderAllLoops();
        findExitNodes();
        findStores();
        allTempsDefined();
    }
    private void findBackEdges(){
        for (BasicBlockCFG node : graph.getNodes()){
            for (BasicBlockCFG dominator: dom.outMapping.get(node)){
                if (node.getChildren().contains(dominator)){
                    backEdges.add(new Pair<>(node,dominator));
                }
            }
        }
    }

    private void find_pred(BasicBlockCFG node,
                           ArrayList<BasicBlockCFG> loop,
                           HashSet<BasicBlockCFG> visit,
                           BasicBlockCFG head){
        if (node == head){
            return;
        }
        loop.add(node);
        for (BasicBlockCFG pred : node.getPredecessors()){
            if (!visit.contains(pred)){
                visit.add(pred);
                find_pred(pred,loop,visit,head);
            }
        }
    }
    private void findLoops(){
        for (Pair<BasicBlockCFG,BasicBlockCFG> pair : backEdges){
            BasicBlockCFG a = pair.part1();
            BasicBlockCFG b = pair.part2();
            ArrayList<BasicBlockCFG> nat_loop = new ArrayList<>();
            HashSet<BasicBlockCFG> visit = new HashSet<>();
            visit.add(b);
            find_pred(a,nat_loop,visit,b);
            all_loops.add(new LoopWrapper(b, nat_loop));
        }
    }
    private void mergeLoops(){
        for (LoopWrapper loop : all_loops){
            if (!headerToLoop.containsKey(loop.header)){
                headerToLoop.put(loop.header,loop);
            }else{
                LoopWrapper head = headerToLoop.get(loop.header);
                // merge
                head.loopBody.addAll(loop.loopBody);
            }
        }
        all_loops.clear();
        all_loops.addAll(headerToLoop.values());
    }


    private boolean isSubset(LoopWrapper loop1, LoopWrapper loop2){
        ArrayList<BasicBlockCFG> loop1Blocks = loop1.getAllBlocksInLoop();

        ArrayList<BasicBlockCFG> loop2Blocks = loop2.getAllBlocksInLoop();

        for (BasicBlockCFG block : loop1Blocks){
            if (!loop2Blocks.contains(block)){
                return false;
            }
        }
        return true;
    }
    private void reorderAllLoops(){
        HashMap<LoopWrapper,HashSet<LoopWrapper>> parentToChildLoops = new HashMap<>();
        for (int i =0; i< all_loops.size();i++){
            parentToChildLoops.put(all_loops.get(i),new HashSet<>());
            LoopWrapper parent = all_loops.get(i);
            for (int j = 0; j< all_loops.size();j++){
                LoopWrapper child = all_loops.get(j);
                if (i != j){
                    if (isSubset(child,parent)){ // j is subset of i
                        parentToChildLoops.get(parent).add(child);
                    }
                }
            }
        }
        for (LoopWrapper parent : parentToChildLoops.keySet()){
            for (LoopWrapper child : parentToChildLoops.get(parent)){
                int indexOfChild = parent.loopBody.indexOf(child.header);
                parent.loopBody.add(indexOfChild,child.preheader);
            }
        }
        ArrayList<LoopWrapper> reordered = new ArrayList<>();
        ArrayDeque<LoopWrapper> queue = new ArrayDeque<>();
        HashSet<LoopWrapper> visited = new HashSet<>();
        for (LoopWrapper parent : parentToChildLoops.keySet()){
            if (parentToChildLoops.get(parent).size() == 0){
                queue.add(parent);
                visited.add(parent);
            }
        }

        while (!queue.isEmpty()){
            LoopWrapper top = queue.poll();
            reordered.add(top);
            for (LoopWrapper parent : parentToChildLoops.keySet()){
                parentToChildLoops.get(parent).remove(top);
                if (!visited.contains(parent) &&  parentToChildLoops.get(parent).size() == 0){
                    visited.add(parent);
                    queue.add(parent);
                }
            }
        }
        if (reordered.size() != all_loops.size()){
            throw new InternalCompilerError("loops missing");
        }
        all_loops = reordered;



    }
    private void findExitNodes(){
        for (LoopWrapper loop : all_loops){
            HashSet<BasicBlockCFG> nodesInGraph = new HashSet<>(loop.getAllBlocksInLoop());
            for (BasicBlockCFG block : loop.getAllBlocksInLoop()){

                for (BasicBlockCFG child : block.getChildren()){
                    if (child != null && !nodesInGraph.contains(child)){
                        loop.exitBlocks.add(block);
                    }
                }

            }
        }
    }

    private void allTempsDefined(){
        for (LoopWrapper loop :all_loops) {
            HashMap<IRTemp, Set<CFGNode<IRStmt>>> tempToDefs = new HashMap<>();
            for (BasicBlockCFG block : loop.getAllBlocksInLoop()) {
                for (CFGNode<IRStmt> stmt : block.getBody()) {
                    Set<IRTemp> defs = LiveVariableAnalysis.def(stmt.getStmt());
                    for (IRTemp def : defs) {
                        if (!tempToDefs.containsKey(def)) {
                            tempToDefs.put(def, new HashSet<>());
                        }
                        tempToDefs.get(def).add(stmt);
                    }
                }
            }
            loop.definedTempsInLoop = tempToDefs;
        }
    }
    private void findStores(){
        for (LoopWrapper loop : all_loops){
            for (BasicBlockCFG block : loop.getAllBlocksInLoop()){
                for (CFGNode<IRStmt> stmt : block.getBody()){
                    if (stmt.getStmt() instanceof IRMove mov &&
                    mov.target() instanceof IRMem mem){
                        loop.hasStores = true;
                        loop.stores.add(mem);
                    }else if (stmt.getStmt() instanceof IRCallStmt call){
                        loop.hasStores = true;
                        loop.stores.add(call);
                    }
                }
            }
        }
    }


    private boolean nodeContainedInLoop(LoopWrapper loop, CFGNode<IRStmt> inst){
        return loop.allNodes.contains(inst);
    }

    /**
     * Very Naive Check if alias. Just checking for Length
     * @param inst
     * @return
     */

    private boolean isLengthInVar(IRMem inst, LoopWrapper loop){

        HashMap<IRTemp,Set<CFGNode<IRStmt>>> definedTemps =  loop.definedTempsInLoop;
        // This Mem is length
        if (inst.expr() instanceof IRBinOp binop && binop.opType() == IRBinOp.OpType.SUB
                && binop.left() instanceof IRTemp base && binop.right() instanceof IRConst cons && cons.value() == 8L){
            if (!definedTemps.containsKey(base)){
                return true;
            }
            else return definedTemps.get(base).size() == 0;
        }

        return false;
    }
    private boolean isNaiveAlias(CFGNode<IRStmt> inst, LoopWrapper loop){
        // Length
//        IRMem mem = new IRMem(new IRBinOp(IRBinOp.OpType.SUB, node.getArg().accept(this), new IRConst(WORD_BYTES)));
        IRStmt node = inst.getStmt();
        ArrayList<IRNode> exprsInInst = new FlattenIrVisitor().visit(node);
        ArrayList<IRMem> mems = new ArrayList<>();
        for (IRNode n: exprsInInst){
            if (n instanceof IRMem m){
                mems.add(m);
            }
        }
        for (IRMem memInst: mems){
            if (!isLengthInVar(memInst,loop)){
                return true;
            }
        }


        return false;
    }
    /**
     * Checks if Instruction is Invariant
     * @param inst
     * @param loop
     * @param instrsAlreadyInvar
     * @return
     */
    private boolean isInvariant(CFGNode<IRStmt> inst, LoopWrapper loop, HashSet<CFGNode<IRStmt>> instrsAlreadyInvar){
        if (!(inst.getStmt() instanceof IRMove)){
            return false;
        }
        ArrayList<IRNode> flat = new FlattenIrVisitor().visit(inst.getStmt());
        boolean usedMem = false;
        for (IRNode node : flat){
            if (node instanceof IRMem){
                usedMem = true;
                break;
            }
        }
        boolean isLength = false;
        if (loop.hasStores && usedMem){
            for (IRNode node : loop.stores){ // I'm here
                if (node instanceof IRCallStmt ){
                    return false;
                }
            }
            if(isNaiveAlias(inst,loop)){
                return false;
            }else{
                isLength = true;
            }
        }else if (usedMem){
            if(isNaiveAlias(inst,loop)){

            }else{
                isLength = true;
            }
        }
//        loop.definedTempsInLoop
        Set<IRTemp> uses = LiveVariableAnalysis.use(inst.getStmt());

        //All reaching definitions of var are outside the loop.
        boolean allReachDefOutside = true;
        for (IRTemp use : uses){
            Set<CFGNode<IRStmt>> nodesToCheck = new HashSet<>(reach.singleNodeOutMapping.get(inst));
            // only check nodes that define this use
            nodesToCheck.removeIf(n -> !LiveVariableAnalysis.def(n.getStmt()).contains(use));

            for (CFGNode<IRStmt> reachDef: nodesToCheck) {
                if (nodeContainedInLoop(loop, reachDef)) {
                    allReachDefOutside = false;
                    break;
                }
            }
        }
        if (!allReachDefOutside){
        // now we know at least one def inside of loop
            for (IRTemp use : uses){
                Set<CFGNode<IRStmt>> nodesToCheck = new HashSet<>(reach.singleNodeOutMapping.get(inst));
                // only check nodes that define this use
                nodesToCheck.removeIf(n -> !LiveVariableAnalysis.def(n.getStmt()).contains(use));
                // more than one reaching def
                if (nodesToCheck.size() == 0){
                    continue;
                }
                if(nodesToCheck.size() > 1){
                    return false;
                }
                CFGNode<IRStmt> reachedDef = nodesToCheck.iterator().next();
                if (nodeContainedInLoop(loop,reachedDef) && // loop contains reaching def
                        (!instrsAlreadyInvar.contains(reachedDef))){ // loop has more than one def of this var
                    return false;
                }
            }
        }
        HashMap<IRTemp,Set<CFGNode<IRStmt>>> definedTemps =  loop.definedTempsInLoop;
        Set<IRTemp> defs = LiveVariableAnalysis.def(inst.getStmt());
        for (IRTemp def : defs){
            if (definedTemps.get(def).size() > 1){
                return false;
            }
            if (lva.getOutMapping().get(loop.preheader).contains(def)){
                return false;
            }
        }
        BasicBlockCFG blockThatHoldsLI = null;
        for (BasicBlockCFG block : loop.getAllBlocksInLoop()){
            if (block.getBody().contains(inst)){
                blockThatHoldsLI = block;
                break;
            }
        }
        if (blockThatHoldsLI == null){
            throw new InternalCompilerError("loop invar without block in loop");
        }
        for (BasicBlockCFG exitBlocks : loop.exitBlocks){
            if (!dom.getOutMapping().get(exitBlocks).contains(blockThatHoldsLI) && !isLength){
//                System.out.println("exit block not dominated: " + inst);
//                System.out.println("my block: " + blockThatHoldsLI);
//                System.out.println("bad block: " + exitBlocks);
                return false;
            }
        }
        return true;

    }

    /**
     * Finds the potential Invariant Nodes
     * @param loop
     * @return
     */
    private ArrayList<CFGNode<IRStmt>> potentialInvariantNodes(LoopWrapper loop){
        ArrayList<CFGNode<IRStmt>> res = new ArrayList<>();
        HashMap<IRTemp, Set<CFGNode<IRStmt>>> useMapping = new HashMap<>();

        for (BasicBlockCFG block : loop.getAllBlocksInLoop()){
            for (CFGNode<IRStmt> node : block.getBody()){
                Set<IRTemp> uses = LiveVariableAnalysis.use(node.getStmt());
                Set<IRTemp> defs = LiveVariableAnalysis.def(node.getStmt());
                for (IRTemp use : uses){
                    if (!useMapping.containsKey(use)){
                        useMapping.put(use,new HashSet<>());
                    }
                    useMapping.get(use).add(node);
                }
                for (IRTemp def : defs){
                    if (!useMapping.containsKey(def)){
                        useMapping.put(def,new HashSet<>());
                    }
                }
            }
        }
        HashSet<CFGNode<IRStmt>> set = new HashSet<>(loop.allNodes);
        ArrayDeque<CFGNode<IRStmt>> queue = new ArrayDeque<>(loop.allNodes);
        HashSet<CFGNode<IRStmt>> invariantInstrs = new HashSet<>();
        while (!queue.isEmpty()){
            CFGNode<IRStmt> node = queue.poll();
            if (isInvariant(node,loop,invariantInstrs)){ // passes initial check
                Set<IRTemp> defs = LiveVariableAnalysis.def(node.getStmt());
                invariantInstrs.add(node);
                res.add(node);
                for (IRTemp def : defs){
                    for (CFGNode<IRStmt> child : useMapping.get(def)){
                        if (!set.contains(child)){
                            set.add(child);
                            queue.add(child);
                        }
                    }
                }
            }
        }
//        System.out.println("this is res:" + res);
        return res;
    }

    /**
     * Hoists the loop invariant Nodes
     */
    public void hoistPotentialNodes(){
        for (LoopWrapper loop : all_loops){
            lva = new LiveVariableAnalysisBlocks(graph);
            lva.workList();
            final HashMap<IRTemp,HashSet<CFGNode<IRStmt>>> temps =  ReachingDefinitionsBlock.locateDefs(graph);
            reach = new ReachingDefinitionsBlock(graph, temps);
            reach.worklist();
            reach.getSingleMapping(temps);
            loop.potentialLoopInvariantInstrs = potentialInvariantNodes(loop);
//            System.out.println(loop.potentialLoopInvariantInstrs);
//            System.out.println(loop.definedTempsInLoop);
            HashMap<IRTemp,Set<CFGNode<IRStmt>>> definedTemps =  new HashMap<>(loop.definedTempsInLoop);
            ArrayList<CFGNode<IRStmt>> validLI = new ArrayList<>();
            validLI = new ArrayList<>(loop.potentialLoopInvariantInstrs);

//            if (validLI.size() != 0){
//                System.out.println("hoisted " + validLI);
//            }
            ArrayList<BasicBlockCFG> loopBlocks = loop.getAllBlocksInLoop();
            for (int i = 0; i< loopBlocks.size();i++){
                BasicBlockCFG block = loopBlocks.get(i);
                ArrayList<CFGNode<IRStmt>> nxtBody = new ArrayList<>();
                for (CFGNode<IRStmt> node : block.getBody()){
                    if (validLI.contains(node)){
                        loop.preheader.getBody().add(node);
                    }else{
                        nxtBody.add(node);
                    }
                }
                block.body = nxtBody;
            }
        }
    }

    /**
     * Inserts the Preheader nodes into the Graph
     */
    public void insertPreHeaderNoSSA(){
        for (LoopWrapper loop : all_loops){
            BasicBlockCFG head = loop.header;
            BasicBlockCFG preheader = loop.preheader;

            String preheadString = nxtLabel();
            IRLabel preheaderLabel = new IRLabel(preheadString);
            preheader.getBody().add(new CFGNode<>(preheaderLabel));


            int indexOfHead = graph.getNodes().indexOf(head);
            ArrayList<BasicBlockCFG> preds = head.getPredecessors();
            // Old predecessors of head now point to preheader
            ArrayList<BasicBlockCFG> preheaderPreds = new ArrayList<>();

            for (BasicBlockCFG pred : preds){
                if (!backEdges.contains(new Pair<>(pred,head))) { // not backedge
                    int indexOfHeaderInPred = pred.getChildren().indexOf(head);
                    BasicBlockCFG originalFallThrough = pred.getFallThroughChild();
                    pred.getChildren().set(indexOfHeaderInPred, preheader);
                    preheaderPreds.add(pred);

                    if (pred.getBody().size() != 0){
                        CFGNode<IRStmt> lastStatementPred = pred.getBody().get(pred.getBody().size() - 1);
                        if (lastStatementPred.getStmt() instanceof IRJump jmp) {
                            IRJump newJump = new IRJump(new IRName(preheadString));
                            lastStatementPred.setStmt(newJump);
                        }else if (lastStatementPred.getStmt() instanceof IRCJump cjmp){
                            if (!(originalFallThrough == head)) {
                                IRCJump newCjmp = new IRCJump(cjmp.cond(), preheadString, null);
                                lastStatementPred.setStmt(newCjmp);
                            }
                        }
                    }
                }
            }
            preheader.predecessors = preheaderPreds; // preheader gets headers preds
            preheader.setFallThroughChild(head);

            ArrayList<BasicBlockCFG> nxtHeadPreds = new ArrayList<>();
            for (BasicBlockCFG pred : preds){
                if (backEdges.contains(new Pair<>(pred,head))){
                    nxtHeadPreds.add(pred);
                }
            }
            nxtHeadPreds.add(preheader);
            head.predecessors = nxtHeadPreds;
            graph.getNodes().add(indexOfHead,preheader);
        }
    }

}
