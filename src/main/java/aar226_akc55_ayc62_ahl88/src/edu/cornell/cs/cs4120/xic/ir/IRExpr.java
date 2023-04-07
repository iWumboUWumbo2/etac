package aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.AbstractASMVisitor;

import java.util.ArrayList;

public interface IRExpr extends IRNode {
    boolean isConstant();

    long constant();

    ArrayList<ASMInstruction> accept(AbstractASMVisitor v);
}
