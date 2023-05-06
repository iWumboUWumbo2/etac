package aar226_akc55_ayc62_ahl88.asm.Opts;

import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMTempExpr;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg1;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg2;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg3;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;
import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.ir.BackwardIRDataflow;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

import static aar226_akc55_ayc62_ahl88.asm.visit.RegisterAllocationTrivialVisitor.checkExprForTemp;

public class LiveVariableAnalysisASM extends BackwardIRDataflow<Set<ASMTempExpr>> {
    public LiveVariableAnalysisASM(CFGGraph<IRStmt> g, BiFunction<CFGNode<IRStmt>, Set<ASMTempExpr>, Set<ASMTempExpr>> transfer, BinaryOperator<Set<ASMTempExpr>> meet, Supplier<Set<ASMTempExpr>> acc, Set<ASMTempExpr> topElement) {
        super(g, transfer, meet, acc, topElement);
    }

    public static Set<ASMTempExpr> usesInASM(ASMInstruction instr){
        HashSet<ASMTempExpr> useSet = new HashSet<>();
        HashSet<String> temps = new HashSet<>();
        if (instr instanceof ASMArg1 arg1){
            checkExprForTemp(arg1.getLeft(),temps);
        }else if (instr instanceof  ASMArg2 arg2){
            checkExprForTemp(arg2.getLeft(),temps);
            checkExprForTemp(arg2.getRight(),temps);
        }else if (instr instanceof ASMArg3 arg3){
            checkExprForTemp(arg3.getA1(),temps);
            checkExprForTemp(arg3.getA2(),temps);
            checkExprForTemp(arg3.getA3(),temps);
        }

        return useSet;
    }
    public static Set<ASMTempExpr> defsInASM(ASMInstruction instr) {
        HashSet<ASMTempExpr> defSet = new HashSet<>();


        return defSet;
    }

}
