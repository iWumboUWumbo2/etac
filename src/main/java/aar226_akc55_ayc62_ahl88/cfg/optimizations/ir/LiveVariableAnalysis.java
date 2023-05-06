package aar226_akc55_ayc62_ahl88.cfg.optimizations.ir;

import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.FlattenIrVisitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class LiveVariableAnalysis extends BackwardIRDataflow<Set<IRTemp>,IRStmt> {
    public LiveVariableAnalysis(CFGGraph<IRStmt> graph) {
        super(
                graph,
                (n, outN) -> {
                    Set<IRTemp> useSet = use(n.getStmt());
                    Set<IRTemp> defSet = def(n.getStmt());

                    Set<IRTemp> l_temp = new HashSet<>(outN);
                    l_temp.removeAll(defSet);
                    l_temp.addAll(useSet);
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


    public static Set<IRTemp> use(IRStmt stmt) {

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
        }else if (stmt instanceof IRPhi phi){
            flattened = new ArrayList<>();
            for (IRExpr e : phi.getArgs()){
                flattened.addAll(new FlattenIrVisitor().visit(e));
            }
        }
        else{
            flattened = new ArrayList<>();
        }
        return usedTempsLVA(flattened);
    }


    public static Set<IRTemp> def(IRStmt stmt) {
        HashSet<IRTemp> defSet = new HashSet<>();
        if (stmt instanceof IRMove move && move.target() instanceof IRTemp temp){
            defSet.add(temp);
        }else if (stmt instanceof IRCallStmt call){
            for (int i = 1; i<= call.n_returns();i++){
                defSet.add(new IRTemp("_RV" + i));
            }
        }else if (stmt instanceof IRPhi phi){
            defSet.add((IRTemp) phi.getTarget());
        }
        return defSet;
    }

    public static Set<IRTemp> usedTempsLVA(ArrayList<IRNode> nodes){
        HashSet<IRTemp> res = new HashSet<>();
        for (IRNode node : nodes){
            if (node instanceof IRTemp temp){
                res.add(temp);
            }
        }
        return res;
    }
}