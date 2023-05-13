package aar226_akc55_ayc62_ahl88.cfg.optimizations.BasicBlocks;

import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.ir.LiveVariableAnalysis;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.ReplaceTempWithConstAndFold;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.InternalCompilerError;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.Pair;

import java.util.*;

public class ConstantPropSSA {


    CFGGraphBasicBlock graph;

    // ASSUME SSA
    public ConstantPropSSA(CFGGraphBasicBlock graph){
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
                Pair<Boolean,Long> res =  isPhiConst(phi);
                if (res.part1()){
                    IRMove nxtStatement = new IRMove(phi.getTarget(),new IRConst(res.part2()));
                    s.setStmt(nxtStatement);
                }
            }

            if (s.getStmt() instanceof IRMove mov && mov.source() instanceof
                    IRConst cons && mov.target() instanceof IRTemp temp && uses.containsKey(temp)){
                IRConst valueToProp = new IRConst(cons.value());
                Pair<IRTemp,IRConst> pairMap = new Pair<>(temp,valueToProp);
                s.isDeleted = true;
//                System.out.println(temp);
//                System.out.println(s.getStmt());
                for (CFGNode<IRStmt> nodeT : uses.get(temp)) {
                    if (!nodeT.isDeleted) {
                        // replace
                        IRStmt subResult = (IRStmt) new ReplaceTempWithConstAndFold(
                                new IRNodeFactory_c(), pairMap).visit(nodeT.getStmt());
                        nodeT.setStmt(subResult);
                        queue.add(nodeT);
                    }
                }
            }
        }

        graph.removeDeletedNodes();
    }
    private Pair<Boolean,Long> isPhiConst(IRPhi phi){
        if (phi.getArgs().size() == 0) {
            throw new InternalCompilerError("phi shouldn't be zero");
        }

        Long val = null;
        for (IRExpr arg : phi.getArgs()) {
            if (!(arg instanceof IRConst cons)) {
                return new Pair<>(false,0L);
            } else {
                val = cons.constant();
            }
        }

        for (IRExpr arg : phi.getArgs()) {
            IRConst cons = (IRConst) arg;
            if (cons.value() != val) {
                return new Pair<>(false,0L);
            }
        }
//        System.out.println("phi is constant: " + phi);
        return new Pair<>(true,val);
    }
}
