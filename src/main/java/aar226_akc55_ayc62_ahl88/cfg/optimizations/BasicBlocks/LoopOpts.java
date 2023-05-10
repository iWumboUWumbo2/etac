package aar226_akc55_ayc62_ahl88.cfg.optimizations.BasicBlocks;

import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.ir.LiveVariableAnalysis;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.FlattenIrVisitor;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class LoopOpts {

    private static class LoopWrapper{

        public boolean hasStores;
        public BasicBlockCFG preheader;
        public BasicBlockCFG header;

        public ArrayList<BasicBlockCFG> loopBody;

        public HashSet<BasicBlockCFG> exitBlocks;

        public HashSet<IRTemp> definedTempsInLoop;

        public HashSet<IRTemp> loopInvariantDefs;
        public HashMap<IRTemp,CFGNode<IRStmt>> defToNode;
        public LoopWrapper(BasicBlockCFG head, ArrayList<BasicBlockCFG> body){
            preheader = new BasicBlockCFG();
            header = head;
            loopBody = body;
            exitBlocks = new HashSet<>();
            definedTempsInLoop = new HashSet<>();
            loopInvariantDefs = new HashSet<>();
            defToNode = new HashMap<>();
            hasStores = false;
        }

        public ArrayList<BasicBlockCFG> getAllNodesInLoop(){
            ArrayList<BasicBlockCFG> res = new ArrayList<>();
            res.add(header);
            res.addAll(loopBody);
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

    public ArrayList<Pair<BasicBlockCFG,BasicBlockCFG>> backEdges;

    public ArrayList<LoopWrapper> all_loops;

    public LiveVariableAnalysisBlocks lva;



    public LoopOpts(CFGGraphBasicBlock g,DominatorBlockDataflow domBlock){
        graph = g;
        dom = new DominatorBlockDataflow(g);
        dom.createAndExecuteDF();
        dom.retArgsReverseMapping = domBlock.retArgsReverseMapping;
        backEdges = new ArrayList<>();
        all_loops = new ArrayList<>();
        lva = new LiveVariableAnalysisBlocks(g);
        lva.workList();
        findBackEdges();
        findLoops();
        mergeLoops();
//        insertPreHeader();

        findExitNodes();
        findStores();
        allTempsDefined();
//        all_loops.forEach(e -> System.out.println("exits: " + e.exitBlocks));
    }

//    private void insertPreHeader(){
//        for (LoopWrapper loop : all_loops){
//            BasicBlockCFG head = loop.header;
//            BasicBlockCFG preheader = loop.preheader;
//
//            int indexOfHead = graph.getNodes().indexOf(head);
//            ArrayList<BasicBlockCFG> preds = head.getPredecessors();
//            // Old predecessors of head now point to preheader
//            ArrayList<BasicBlockCFG> preheaderPreds = new ArrayList<>();
//            for (BasicBlockCFG pred : preds){
//                if (!backEdges.contains(new Pair<>(pred,head))) { // not backedge
//                    int indexOfHeaderInPred = pred.getChildren().indexOf(head);
//                    pred.getChildren().set(indexOfHeaderInPred, preheader);
//                    preheaderPreds.add(pred);
//                }
//            }
//            preheader.predecessors = preheaderPreds; // preheader gets headers preds
//
//            preheader.setFallThroughChild(head);
//            head.predecessors.clear();
//            head.predecessors.add(preheader);
//
//            graph.getNodes().set(indexOfHead,preheader);
//        }
//    }
    private void findBackEdges(){
        for (BasicBlockCFG node : graph.getNodes()){
            for (BasicBlockCFG dominator: dom.outMapping.get(node)){
                if (node.getChildren().contains(dominator)){
                    backEdges.add(new Pair<>(node,dominator));
                }
            }
        }
    }

    private void find_pred(BasicBlockCFG node, ArrayList<BasicBlockCFG> loop, HashSet<BasicBlockCFG> visit){
        loop.add(node);
        for (BasicBlockCFG pred : node.getPredecessors()){
            if (!visit.contains(pred)){
                visit.add(pred);
                find_pred(pred,loop,visit);
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
            find_pred(a,nat_loop,visit);
            all_loops.add(new LoopWrapper(b, nat_loop));
        }
    }
    private void mergeLoops(){
        HashMap<BasicBlockCFG,LoopWrapper> headerToLoop = new HashMap<>();
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

    private void findExitNodes(){
//        System.out.println(all_loops.size());
        for (LoopWrapper loop : all_loops){
            HashSet<BasicBlockCFG> nodesInGraph = new HashSet<>(loop.getAllNodesInLoop());
//            System.out.println("these are nodes in loop: " + nodesInGraph);
            for (BasicBlockCFG block : loop.getAllNodesInLoop()){

                for (BasicBlockCFG child : block.getChildren()){
                    if (child != null && !nodesInGraph.contains(child)){
                        loop.exitBlocks.add(block);
                    }
                }

            }
        }
    }

    private boolean isInvariant(LoopWrapper loop,
                                CFGNode<IRStmt> inst){
        ArrayList<IRNode> flat = new FlattenIrVisitor().visit(inst.getStmt());
        boolean usedMem = false;
        for (IRNode node : flat){
            if (node instanceof IRMem){
                usedMem = true;
                break;
            }
        }
        if (loop.hasStores && usedMem){
            return false;
        }
        Set<IRTemp> used = LiveVariableAnalysis.use(inst.getStmt());
        for (IRTemp use: used){
            // loop defines this temp and that temp is not invariant
            if (loop.definedTempsInLoop.contains(use) && !loop.loopInvariantDefs.contains(use)){
                return false;
            }
        }
        return true;
    }

    private void allTempsDefined(){ // should only be 1
        for (LoopWrapper loop :all_loops) {
            HashMap<IRTemp, Long> tempToCounts = new HashMap<>();
            for (BasicBlockCFG block : loop.getAllNodesInLoop()) {
                for (CFGNode<IRStmt> stmt : block.getBody()) {
                    Set<IRTemp> defs = LiveVariableAnalysis.def(stmt.getStmt());
                    for (IRTemp def : defs) {
                        if (!tempToCounts.containsKey(def)) {
                            tempToCounts.put(def, 0L);
                        }
                        tempToCounts.put(def, tempToCounts.get(def) + 1);
                    }
                }
            }
//            System.out.println(tempToCounts);
            loop.definedTempsInLoop.addAll(tempToCounts.keySet());
        }
    }
    private void findStores(){
        for (LoopWrapper loop : all_loops){
            for (BasicBlockCFG block : loop.getAllNodesInLoop()){
                for (CFGNode<IRStmt> stmt : block.getBody()){
                    if (stmt.getStmt() instanceof IRMove mov &&
                    mov.target() instanceof IRMem){
                        loop.hasStores = true;
                    }else if (stmt.getStmt() instanceof IRCallStmt){
                        loop.hasStores = true;
                    }
                }
            }
        }
    }
    private void loopInvariantHoisting(){
        for (LoopWrapper loop :all_loops){
            HashMap<IRTemp,Long> tempToCounts = new HashMap<>();
            for (BasicBlockCFG block : loop.getAllNodesInLoop()){
                for (CFGNode<IRStmt> stmt: block.getBody()){
                    Set<IRTemp> defs = LiveVariableAnalysis.def(stmt.getStmt());
                    for (IRTemp def : defs){
                        if (!tempToCounts.containsKey(def)){
                            tempToCounts.put(def,0L);
                        }
                        tempToCounts.put(def,tempToCounts.get(def)+1);
                    }
                }
            }
            HashSet<IRTemp> validTempsSoFar = new HashSet<>();
            for (IRTemp t : tempToCounts.keySet()){ // cond 1
                if (tempToCounts.get(t) == 1){
                    validTempsSoFar.add(t);
                }
            }
            for (IRTemp t : validTempsSoFar){

            }

        }
    }

}
