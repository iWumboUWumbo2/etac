package aar226_akc55_ayc62_ahl88.cfg.optimizations.BasicBlocks;

import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.ir.LiveVariableAnalysis;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.FlattenIrVisitor;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.Pair;

import java.util.*;

public class LoopOpts {

    private static class LoopWrapper{

        public boolean hasStores;
        public BasicBlockCFG preheader;
        public BasicBlockCFG header;

        public ArrayList<BasicBlockCFG> loopBody;

        public HashSet<BasicBlockCFG> exitBlocks;
        public HashMap<IRTemp,Set<CFGNode<IRStmt>>> definedTempsInLoop;

        public HashSet<IRTemp> loopInvariantDefs;
        public HashMap<IRTemp,CFGNode<IRStmt>> defToNode;

        public ArrayList<CFGNode<IRStmt>> allNodes;
        public LoopWrapper(BasicBlockCFG head, ArrayList<BasicBlockCFG> body){
            preheader = new BasicBlockCFG();
            header = head;
            loopBody = body;
            exitBlocks = new HashSet<>();
            loopInvariantDefs = new HashSet<>();
            defToNode = new HashMap<>();
            hasStores = false;
            allNodes = getAllNodesInLoop();
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
        lva = new LiveVariableAnalysisBlocks(g);
        lva.workList();
        findBackEdges();
        findLoops();
        mergeLoops();
        findExitNodes();
        findStores();
        allTempsDefined();
//        all_loops.forEach(e -> System.out.println("exits: " + e.exitBlocks));
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
                    mov.target() instanceof IRMem){
                        loop.hasStores = true;
                    }else if (stmt.getStmt() instanceof IRCallStmt){
                        loop.hasStores = true;
                    }
                }
            }
        }
    }


    private boolean nodeContainedInLoop(LoopWrapper loop, CFGNode<IRStmt> inst){
        return loop.allNodes.contains(inst);
    }
    private boolean isInvariant(CFGNode<IRStmt> inst, LoopWrapper loop, HashSet<IRTemp> defsAlreadyInvar){
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
        if (loop.hasStores && usedMem){
            return false;
        }
//        loop.definedTempsInLoop
        Set<IRTemp> uses = LiveVariableAnalysis.use(inst.getStmt());

        //All reaching definitions of var are outside the loop.
        boolean allReachDefOutside = true;
        for (IRTemp use : uses){
            Set<CFGNode<IRStmt>> nodesToCheck = reach.singleNodeOutMapping.get(inst);
            // only check nodes that define this use
            nodesToCheck.removeIf(n -> !LiveVariableAnalysis.def(n.getStmt()).contains(use));

            for (CFGNode<IRStmt> reachDef: nodesToCheck) {
                if (nodeContainedInLoop(loop, reachDef)) {
                    allReachDefOutside = false;
                    break;
                }
            }
        }
        if (allReachDefOutside){
            return true;
        }

        // now we know at least one def inside of loop
        for (IRTemp use : uses){
            Set<CFGNode<IRStmt>> nodesToCheck = reach.singleNodeOutMapping.get(inst);
            // only check nodes that define this use
            nodesToCheck.removeIf(n -> !LiveVariableAnalysis.def(n.getStmt()).contains(use));
            // more than one reaching def
            if(nodesToCheck.size() != 1){
                return false;
            }
            CFGNode<IRStmt> reachedDef = nodesToCheck.iterator().next();
            if (nodeContainedInLoop(loop,reachedDef) && // loop contains reaching def
                    (!defsAlreadyInvar.contains(use))){ // loop has more than one def of this var
                return false;
            }
        }
        return true;

    }
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
        HashSet<IRTemp> invariantTemps = new HashSet<>();
        while (!queue.isEmpty()){
            CFGNode<IRStmt> node = queue.poll();
            if (isInvariant(node,loop,invariantTemps)){
                Set<IRTemp> defs = LiveVariableAnalysis.def(node.getStmt());
                invariantTemps.addAll(defs);
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

        return res;
    }

    public ArrayList<CFGNode<IRStmt>> loopToPotentialInvar(){
        ArrayList<CFGNode<IRStmt>> res =new ArrayList<>();
        for (LoopWrapper loop : all_loops){
            res.addAll(potentialInvariantNodes(loop));
        }
        return res;
    }

    public void insertPreHeaderNoSSA(){
//        System.out.println("inserting preheaders");
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
                    pred.getChildren().set(indexOfHeaderInPred, preheader);
                    preheaderPreds.add(pred);

                    if (pred.getBody().size() != 0){
                        CFGNode<IRStmt> lastStatementPred = pred.getBody().get(pred.getBody().size() - 1);
                        if (lastStatementPred.getStmt() instanceof IRJump jmp) {
                            IRJump newJump = new IRJump(new IRName(preheadString));
                            lastStatementPred.setStmt(newJump);
                        }else if (lastStatementPred.getStmt() instanceof IRCJump cjmp){
                            IRCJump newCjmp = new IRCJump(cjmp.cond(),preheadString,null);
                            lastStatementPred.setStmt(newCjmp);
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

        final HashMap<IRTemp,HashSet<CFGNode<IRStmt>>> temps =  ReachingDefinitionsBlock.locateDefs(graph);
        reach = new ReachingDefinitionsBlock(graph, temps);
        reach.worklist();
        reach.getSingleMapping(temps);
    }

}
