package aar226_akc55_ayc62_ahl88.cfg.optimizations.ir;

import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.ir.BackwardIRDataflow;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.FlattenIrVisitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LiveVariableAnalysis extends BackwardIRDataflow<Set<IRTemp>> {
    public LiveVariableAnalysis(CFGGraph<IRStmt> graph) {
        super(
                graph,
                (n, l) -> {
                    var useSet = use(n);
                    var defSet = def(n);

                    var l_temp = new HashSet<>(l);
                    l_temp.removeAll(defSet);

                    useSet.addAll(l_temp);
                    return useSet;
                },
                (l1, l2) -> {
                    var l1_temp = new HashSet<>(l1);
                    l1_temp.addAll(l2);
                    return l1_temp;
                },
                HashSet::new,
                new HashSet<>()
        );
    }

//    private static Set<IRTemp> use(CFGNode<IRStmt> node) {
//        HashSet<IRTemp> useSet = new HashSet<>();
//
//    }

    public static Set<IRTemp> use(CFGNode<IRStmt> node) {
        IRStmt stmt = node.getStmt();

        ArrayList<IRNode> flattened;
        // if [mov temp, expr] then don't add temp
        if (stmt instanceof IRMove irmove && irmove.target() instanceof IRTemp) {
            flattened = irmove.source().aggregateChildren(new FlattenIrVisitor());
        }

        // MEM
        else if (stmt instanceof IRMove irmove && irmove.target() instanceof IRMem) {
            flattened = stmt.aggregateChildren(new FlattenIrVisitor());
        }

        // JUMP
        else if (stmt instanceof IRCJump cjmp) {
            flattened = cjmp.cond().aggregateChildren(new FlattenIrVisitor());
        }
        // Return
        else if (stmt instanceof IRReturn ret){
            flattened = new ArrayList<>();
            for (IRExpr e: ret.rets()){
                flattened.addAll(e.aggregateChildren(new FlattenIrVisitor()));
            }
        }else if (stmt instanceof IRCallStmt call){
            flattened = new ArrayList<>();
            for (IRExpr e : call.args()){
                flattened.addAll(e.aggregateChildren(new FlattenIrVisitor()));
            }
        }
        else{
            flattened = new ArrayList<>();
        }
        return usedTempsLVA(flattened);
    }


    private static Set<IRTemp> def(CFGNode<IRStmt> node) {
        HashSet<IRTemp> defSet = new HashSet<>();
        IRStmt stmt = node.getStmt();
        if (stmt instanceof IRMove move && move.target() instanceof IRTemp temp){
            defSet.add(temp);
        }
        return defSet;
    }

    private static Set<IRTemp> usedTempsLVA(ArrayList<IRNode> nodes){
        HashSet<IRTemp> res = new HashSet<>();
        for (IRNode node : nodes){
            if (node instanceof IRTemp temp){
                res.add(temp);
            }
        }
        return res;
    }
}