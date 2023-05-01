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


    CFGGraph<IRStmt> graph;

    // ASSUME SSA
    public ConstantPropSSA(CFGGraph<IRStmt> graph){
        this.graph = graph;
    }

    public ArrayList<IRStmt> workList(){
        HashMap<IRTemp, HashSet<CFGNode<IRStmt>>> uses = new HashMap<>();

        for (CFGNode<IRStmt> node : graph.getNodes()){
            Set<IRTemp> nodeUse = LiveVariableAnalysis.use(node.getStmt());
            for (IRTemp t : nodeUse){
                if (!(t.name().startsWith("_RV") || t.name().startsWith("_ARG"))){
                    if (!uses.containsKey(t)){
                        uses.put(t,new HashSet<>());
                    }
                    uses.get(t).add(node);
                }
            }
        }

        HashSet<CFGNode<IRStmt>> set = new HashSet<>(graph.getNodes());
        Queue<CFGNode<IRStmt>> queue = new ArrayDeque<>(graph.reversePostorder());

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
                IRConst valueToProp =new IRConst(cons.value());
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
        ArrayList<IRStmt> res = new ArrayList<>();
        for (CFGNode<IRStmt> node : graph.getNodes()){
            if (!node.isDeleted){
                res.add(node.getStmt());
            }
        }
        return res;
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
                new Pair<>(false,0L);
            }
        }

        return new Pair<>(true,val);
    }
}
