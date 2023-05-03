package aar226_akc55_ayc62_ahl88.cfg.optimizations.BasicBlocks;

import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.ir.LiveVariableAnalysis;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.ReplaceTempWithConstAndFold;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.ReplaceTempsWithTemps;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.InternalCompilerError;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.Pair;

import java.util.*;

public class CopyPropSSA {
    CFGGraphBasicBlock graph;

    public CopyPropSSA(CFGGraphBasicBlock graph) {
        this.graph = graph;
    }

    public void workList(){
        HashMap<IRTemp, HashSet<CFGNode<IRStmt>>> uses = new HashMap<>();

        for (BasicBlockCFG bb : graph.getNodes()) {
            for (CFGNode<IRStmt> node : bb.getBody()) {
                Set<IRTemp> nodeUse = LiveVariableAnalysis.use(node.getStmt());
                for (IRTemp t : nodeUse) {
                    if (!uses.containsKey(t)) {
                        uses.put(t, new HashSet<>());
                    }
                    uses.get(t).add(node);
                }
            }
        }

//        HashSet<CFGNode<IRStmt>> set = new HashSet<>(graph.getNodes());
//        Queue<CFGNode<IRStmt>> queue = new ArrayDeque<>(graph.reversePostorder());
        ArrayList<BasicBlockCFG> bbs = graph.reversePostorder();
        Queue<CFGNode<IRStmt>> queue = new ArrayDeque<>();

        for (BasicBlockCFG bb : bbs) {
            queue.addAll(bb.getBody());
        }

        while (!queue.isEmpty()){
            CFGNode<IRStmt> s = queue.poll();
            if (s.getStmt() instanceof IRPhi phi){
                Pair<Boolean,IRTemp> res =  isPhiTemp(phi);
                if (res.part1()){
                    IRMove nxtStatement = new IRMove(phi.getTarget(),new IRTemp(res.part2().name()));
                    s.setStmt(nxtStatement);
                }
            }

            if (s.getStmt() instanceof IRMove mov &&
                    mov.source() instanceof IRTemp tsrc && mov.target() instanceof IRTemp tdest &&
                    uses.containsKey(tdest)) {
//                IRConst valueToProp = new IRConst(cons.value());
                IRTemp valueToProp = new IRTemp(tsrc.name());
                HashMap<String,String> pairMap = new HashMap<>();
                pairMap.put(tdest.name(), valueToProp.name());
                s.isDeleted = true;
//                System.out.println(temp);
//                System.out.println(s.getStmt());
                for (CFGNode<IRStmt> nodeT : uses.get(tdest)) {
                    if (!nodeT.isDeleted) {
                        // replace
                        IRStmt subResult = (IRStmt) new ReplaceTempsWithTemps(
                                new IRNodeFactory_c(), pairMap).visit(nodeT.getStmt());
                        nodeT.setStmt(subResult);
                        queue.add(nodeT);
                    }
                }
            }
        }

        graph.removeDeletedNodes();
    }
    private Pair<Boolean, IRTemp> isPhiTemp(IRPhi phi){
        if (phi.getArgs().size() == 0) {
            throw new InternalCompilerError("phi shouldn't be zero");
        }

        if (phi.getArgs().size() != 1) {
            return new Pair<>(false, null);
        }

        IRExpr expr = phi.getArgs().get(0);

        if (!(expr instanceof IRTemp temp)) {
            return new Pair<>(false, null);
        }

        return new Pair<>(true, temp);
    }



}
