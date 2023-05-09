package aar226_akc55_ayc62_ahl88.cfg.optimizations.BasicBlocks;

import aar226_akc55_ayc62_ahl88.src.polyglot.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class LoopOpts {

    private static class LoopWrapper{

        public BasicBlockCFG preheader;
        public BasicBlockCFG header;

        public ArrayList<BasicBlockCFG> loopBody;

        public HashSet<BasicBlockCFG> exitBlocks;
        public LoopWrapper(BasicBlockCFG head, ArrayList<BasicBlockCFG> body){
            preheader = new BasicBlockCFG();
            header = head;
            loopBody = body;
            exitBlocks = new HashSet<>();
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
    CFGGraphBasicBlock graph;

    DominatorBlockDataflow dom;

    ArrayList<Pair<BasicBlockCFG,BasicBlockCFG>> backEdges;

    ArrayList<LoopWrapper> all_loops;

    public LiveVariableAnalysisBlocks lva;



    public LoopOpts(CFGGraphBasicBlock g,DominatorBlockDataflow dominator){
        graph = g;
        dom = dominator;
        backEdges = new ArrayList<>();
        all_loops = new ArrayList<>();
        lva = new LiveVariableAnalysisBlocks(g);
        lva.workList();
        findBackEdges();
        findLoops();
        mergeLoops();
        findExitNodes();
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

}
