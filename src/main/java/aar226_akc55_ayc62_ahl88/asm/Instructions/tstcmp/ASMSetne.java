package aar226_akc55_ayc62_ahl88.asm.Instructions.tstcmp;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg1;

public class ASMSetne extends ASMArg1 {
    public ASMSetne(ASMExpr arg) {super (ASMOpCodes.SETNE, arg);}
}
