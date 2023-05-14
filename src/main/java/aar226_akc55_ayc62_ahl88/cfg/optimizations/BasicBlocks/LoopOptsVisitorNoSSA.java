package aar226_akc55_ayc62_ahl88.cfg.optimizations.BasicBlocks;

import aar226_akc55_ayc62_ahl88.cfg.optimizations.OptimizationType;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRSeq;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;

import java.util.ArrayList;
import java.util.HashMap;

import static aar226_akc55_ayc62_ahl88.Main.opts;
import static aar226_akc55_ayc62_ahl88.cfg.optimizations.BasicBlocks.ReachingDefinitionsBlock.canWeRunReaching;
import static aar226_akc55_ayc62_ahl88.cfg.optimizations.OptimizationType.LICM;

public class LoopOptsVisitorNoSSA {

    public LoopOptsVisitorNoSSA(){

    }



    public IRCompUnit optimizeLoops(IRCompUnit ir){
        HashMap<String, IRFuncDecl> replaceFuncs = new HashMap<>();
        for (String funcName : ir.functions().keySet()){
            IRFuncDecl func = ir.functions().get(funcName);
            IRFuncDecl optCopy = optimizeFunc(func);

            replaceFuncs.put(funcName,optCopy);
        }

        return new IRCompUnit(ir.name(),replaceFuncs,new ArrayList<>(),ir.dataMap());
    }

    public IRFuncDecl optimizeFunc(IRFuncDecl func){
        String funcName = func.name();
        CFGGraphBasicBlock graph = new CFGGraphBasicBlock((ArrayList<IRStmt>) ((IRSeq) func.body()).stmts());
        IRFuncDecl newFunc;
        if (canWeRunReaching(graph) && opts.isSet(OptimizationType.LICM)) {
//            System.out.println("doing licm");
            LoopOpts loopOpts = new LoopOpts(graph, funcName);
            loopOpts.hoistPotentialNodes();
            newFunc = new IRFuncDecl(func.name(), new IRSeq(graph.optimizeJumpsAndLabels()));
            newFunc.functionSig = func.functionSig;
        }else{
            newFunc = func;
        }
        return newFunc;
    }
}
