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
                (n, outN) -> {
                    Set<IRTemp> useSet = use(n);
                    Set<IRTemp> defSet = def(n);

                    Set<IRTemp> l_temp = new HashSet<>(outN);
                    System.out.println("this is l_temp before: " + l_temp);
                    System.out.println("this is def: " + defSet);
                    for (IRTemp t : defSet){
                        System.out.println(t + "in out: " + l_temp.contains(t));
                    }
                    l_temp.removeAll(defSet);
                    System.out.println("thi is l_temp after remove " + l_temp);

                    l_temp.addAll(useSet);
                    System.out.println("this is node: " + n);
                    System.out.println("this is useSet: " + useSet);
                    System.out.println("this is def set: " + defSet);
                    System.out.println("this is outN: " + outN);
                    System.out.println("nextIn " + l_temp);
                    return l_temp;
                },
                (l1, l2) -> {
                    Set<IRTemp> l1_temp = new HashSet<>(l1);
                    l1_temp.addAll(l2);
                    return l1_temp;
                },
                HashSet::new,
                new HashSet<>()
        );
    }


    public static Set<IRTemp> use(CFGNode<IRStmt> node) {
        IRStmt stmt = node.getStmt();

        ArrayList<IRNode> flattened;
        // if [mov temp, expr] then don't add temp
        if (stmt instanceof IRMove irmove && irmove.target() instanceof IRTemp) {
            flattened = new FlattenIrVisitor().visit(irmove.source());
        }

        // MEM
        else if (stmt instanceof IRMove irmove && irmove.target() instanceof IRMem) {
            flattened = new FlattenIrVisitor().visit(stmt);
        }

        // JUMP
        else if (stmt instanceof IRCJump cjmp) {
            flattened = new FlattenIrVisitor().visit(cjmp.cond());
        }
        // Return
        else if (stmt instanceof IRReturn ret){
            flattened = new ArrayList<>();
            for (IRExpr e: ret.rets()){
                flattened.addAll(new FlattenIrVisitor().visit(e));
            }
        }else if (stmt instanceof IRCallStmt call){
            flattened = new ArrayList<>();
            for (IRExpr e : call.args()){
                flattened.addAll(new FlattenIrVisitor().visit(e));
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