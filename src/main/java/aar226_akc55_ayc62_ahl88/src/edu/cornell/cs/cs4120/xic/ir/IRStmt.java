package aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.ASMVisitor;

import java.util.ArrayList;
import java.util.HashSet;

/** An intermediate representation for statements */
public abstract class IRStmt extends IRNode_c {
    public abstract ArrayList<ASMInstruction> accept(ASMVisitor v, HashSet<String> hs);
}
