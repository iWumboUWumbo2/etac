package aar226_akc55_ayc62_ahl88.cfg.optimizations.ir;

import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.ir.LiveVariableAnalysis;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRTemp;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.FlattenIrVisitor;

import java.util.*;

public class DeadCodeElimination {
    CFGGraph<IRStmt> graph;

    public DeadCodeElimination(CFGGraph<IRStmt> graph) {
        this.graph = graph;
    }

    public Set<IRTemp> getAllVariables() {
        ArrayList<IRStmt> stmts = graph.getBackIR();
        ArrayList<IRNode> flattened = new ArrayList<>();

        for (IRStmt stmt : stmts) {
            flattened.addAll(new FlattenIrVisitor().visit(stmt));
        }

        return LiveVariableAnalysis.usedTempsLVA(flattened);
    }

    private void runDCE() {
//        Queue<IRStmt> W = new ArrayDeque<>();
//
//        for (IRStmt stmt : graph.getBackIR()) {
//
//        }
//
//        while (!W.isEmpty()) {
//            IRStmt
//        }
    }

}