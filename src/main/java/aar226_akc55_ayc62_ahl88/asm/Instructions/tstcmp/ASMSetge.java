package aar226_akc55_ayc62_ahl88.asm.Instructions.tstcmp;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg1;

public class ASMSetge extends ASMArg1 {
    public ASMSetge(ASMExpr arg) {super (ASMOpCodes.SETGE, arg);}
}
