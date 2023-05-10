package aar226_akc55_ayc62_ahl88.cfg.optimizations.ir;

import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.cfg.HashSetInf;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.CopyPropReplaceVisitor;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

public class CopyPropNoSSA {

    public CopyPropNoSSA(){


    }


    public IRCompUnit eliminateCode(IRCompUnit ir){
        HashMap<String, IRFuncDecl> replaceFuncs = new HashMap<>();
        for (String funcName : ir.functions().keySet()){
            IRFuncDecl func = ir.functions().get(funcName);
            IRFuncDecl optCopy = replaceCode(func);

            replaceFuncs.put(funcName,optCopy);
        }

        return new IRCompUnit(ir.name(),replaceFuncs,new ArrayList<>(),ir.dataMap());
    }

    private IRFuncDecl replaceCode(IRFuncDecl func){
        CFGGraph<IRStmt> stmtGraph = new CFGGraph<>((ArrayList<IRStmt>) ((IRSeq) func.body()).stmts());
        CopyProp copyProp = new CopyProp(stmtGraph);
        copyProp.worklist();
        HashMap<CFGNode<IRStmt>, HashSetInf<Pair<IRTemp, IRTemp>>> inMapping =
                copyProp.getInMapping();
        for (int i = 0; i < stmtGraph.getNodes().size(); i++) {
            CFGNode<IRStmt> node = stmtGraph.getNodes().get(i);
            HashSetInf<Pair<IRTemp, IRTemp>> pairSet =  inMapping.get(node);
            HashMap<String, String> tempHashMap = new HashMap<>();
            for (Pair<IRTemp, IRTemp> pair : pairSet) {
                tempHashMap.put(pair.part1().name(), pair.part2().name());
            }
            IRStmt visited = (IRStmt) new CopyPropReplaceVisitor(new IRNodeFactory_c(), tempHashMap).visit(node.getStmt());
            node.setStmt(visited);
        }
        IRFuncDecl newFunc = new IRFuncDecl(func.name(), new IRSeq(stmtGraph.getBackIR()));
        newFunc.functionSig = func.functionSig;
        return newFunc;
    }
}
