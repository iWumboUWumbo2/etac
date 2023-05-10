package aar226_akc55_ayc62_ahl88.cfg.optimizations.ir;

import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static aar226_akc55_ayc62_ahl88.cfg.optimizations.BasicBlocks.DeadCodeEliminationSSA.stmtHasSideEffects;

public class DeadCodeElimNoSSA {


    public DeadCodeElimNoSSA(){

    }
    public IRCompUnit eliminateCode(IRCompUnit ir){
        HashMap<String, IRFuncDecl> replaceFuncs = new HashMap<>();
        for (String funcName : ir.functions().keySet()){
            IRFuncDecl func = ir.functions().get(funcName);
            IRFuncDecl optNoDead = removeCode(func);

            replaceFuncs.put(funcName,optNoDead);
        }

        return new IRCompUnit(ir.name(),replaceFuncs,new ArrayList<>(),ir.dataMap());
    }

    private IRFuncDecl removeCode(IRFuncDecl func){
        IRSeq body = (IRSeq) func.body();
        CFGGraph<IRStmt> graph = new CFGGraph<>((ArrayList<IRStmt>) body.stmts());

        LiveVariableAnalysis single = new LiveVariableAnalysis(graph);
        single.workList();
        HashMap<CFGNode<IRStmt>, Set<IRTemp>> liveOutMapping = single.getOutMapping();
        ArrayList<IRStmt> res = new ArrayList<>();
        for (int i = 0 ;i< body.stmts().size();i++){
            CFGNode<IRStmt> node = graph.getNodes().get(i);
            Set<IRTemp> curLive = liveOutMapping.get(node);
            IRStmt stmt = node.getStmt();
            if (!stmtHasSideEffects(stmt) && stmt instanceof IRMove mov
                    && mov.target() instanceof IRTemp targ && !curLive.contains(targ)){
//                System.out.println("deleted node");
            }else{
                res.add(stmt);
            }
        }

        IRFuncDecl result = new IRFuncDecl(func.name(),new IRSeq(res));
        result.functionSig = func.functionSig;
        return result;
    }
}
