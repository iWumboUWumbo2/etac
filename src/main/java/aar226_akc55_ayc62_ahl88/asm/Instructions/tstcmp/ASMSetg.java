package aar226_akc55_ayc62_ahl88.asm.Instructions.tstcmp;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg1;

public class ASMSetg extends ASMArg1 {
    public ASMSetg(ASMExpr arg) {super (ASMOpCodes.SETG, arg);}
}
