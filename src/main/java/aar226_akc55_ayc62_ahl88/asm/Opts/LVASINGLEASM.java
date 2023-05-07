package aar226_akc55_ayc62_ahl88.asm.Opts;


import aar226_akc55_ayc62_ahl88.asm.Expressions.*;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg1;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg2;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg3;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;
import aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine.ASMCall;
import aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine.ASMRet;
import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.ir.BackwardIRDataflow;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.InternalCompilerError;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

import static aar226_akc55_ayc62_ahl88.asm.Opts.LiveVariableAnalysisASM.defsInASM;
import static aar226_akc55_ayc62_ahl88.asm.Opts.LiveVariableAnalysisASM.usesInASM;
import static aar226_akc55_ayc62_ahl88.asm.visit.RegisterAllocationTrivialVisitor.checkExprForTemp;
import static aar226_akc55_ayc62_ahl88.asm.visit.RegisterAllocationTrivialVisitor.flattenMem;

public class LVASINGLEASM extends BackwardIRDataflow<Set<ASMAbstractReg>,ASMInstruction> {
    public LVASINGLEASM(CFGGraph<ASMInstruction> g) {
        super(g,
                (n, outN) -> {
                    Set<ASMAbstractReg> useSet = usesInASM(n.getStmt());
                    Set<ASMAbstractReg> defSet = defsInASM(n.getStmt());

                    Set<ASMAbstractReg> l_temp = new HashSet<>(outN);
                    l_temp.removeAll(defSet);
                    l_temp.addAll(useSet);
                    return l_temp;
                },
                (l1, l2) -> {
                    Set<ASMAbstractReg> l1_temp = new HashSet<>(l1);
                    l1_temp.addAll(l2);
                    return l1_temp;
                },
                HashSet::new,
                new HashSet<>()
        );
    }

}
