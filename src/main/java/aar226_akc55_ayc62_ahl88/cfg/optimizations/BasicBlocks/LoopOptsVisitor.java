package aar226_akc55_ayc62_ahl88.cfg.optimizations.BasicBlocks;

import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRSeq;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;

import java.util.ArrayList;
import java.util.HashMap;

public class LoopOptsVisitor {

    public LoopOptsVisitor(){

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
        CFGGraphBasicBlock blockGraph = new CFGGraphBasicBlock((ArrayList<IRStmt>) ((IRSeq) func.body()).stmts());
        LoopOpts loopFrameWork = new LoopOpts(blockGraph);
        IRFuncDecl newFunc = new IRFuncDecl(func.name(), new IRSeq(loopFrameWork.createNewGraph()));
        newFunc.functionSig = func.functionSig;
        return newFunc;
    }
}
