package aar226_akc55_ayc62_ahl88.cfg.optimizations.ir;

import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.BasicBlocks.BasicBlockCFG;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.BasicBlocks.CFGGraphBasicBlock;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.FlattenIrVisitor;

import java.util.*;

public class DeadCodeEliminationSSA {
    CFGGraphBasicBlock graph;

    HashSet<IRTemp> vars;
    HashMap<IRTemp, HashSet<CFGNode<IRStmt>>> tempUseStmts;
    HashMap<IRTemp, CFGNode<IRStmt>> tempDefStmts;


    public DeadCodeEliminationSSA(CFGGraphBasicBlock graph) {
        this.graph = graph;
        vars = new HashSet<>();
        tempUseStmts = new HashMap<>();
        tempDefStmts = new HashMap<>();

        for (BasicBlockCFG bb : graph.getNodes()) {
            for (CFGNode<IRStmt> node : bb.getBody()) {
                Set<IRTemp> used = LiveVariableAnalysis.use(node.getStmt());
                Set<IRTemp> def = LiveVariableAnalysis.def(node.getStmt());

                vars.addAll(used);
                vars.addAll(def);

                for (IRTemp temp : used) {
                    if (!tempUseStmts.containsKey(temp)) {
                        tempUseStmts.put(temp, new HashSet<>());
                    }

                    tempUseStmts.get(temp).add(node);
                }

                for (IRTemp temp : def) {
                    if (!tempUseStmts.containsKey(temp)) {
                        tempUseStmts.put(temp, new HashSet<>());
                    }
                    tempDefStmts.put(temp, node);
                }
            }
        }
    }

    private boolean stmtHasSideEffects(IRStmt stmt) {
        if (stmt instanceof IRCallStmt){
            return true;
        }
        ArrayList<IRNode> flat = new FlattenIrVisitor().visit(stmt);
        for (IRNode node : flat){
            if (node instanceof IRBinOp binop && binop.opType() == IRBinOp.OpType.DIV &&
                    (!(binop.right() instanceof IRConst))){
                return true;
            }
            if (node instanceof IRBinOp binop && binop.opType() == IRBinOp.OpType.DIV && binop.right() instanceof IRConst cons
                    && cons.value() == 0){
                return true;
            }
        }
        return false;
    }

    public void workList() {
        Queue<IRTemp> W = new ArrayDeque<>(vars);
        while (!W.isEmpty()) {
            IRTemp v = W.poll();

            if (tempUseStmts.get(v).isEmpty()) {
                CFGNode<IRStmt> S = tempDefStmts.get(v);
                if (S != null && !stmtHasSideEffects(S.getStmt())) {
                    S.isDeleted = true;

                    for (IRTemp xi : LiveVariableAnalysis.use(S.getStmt())) {
                        tempUseStmts.get(xi).remove(S);
                        W.add(xi);
                    }
                }
            }
        }
        graph.removeDeletedNodes();
    }
}