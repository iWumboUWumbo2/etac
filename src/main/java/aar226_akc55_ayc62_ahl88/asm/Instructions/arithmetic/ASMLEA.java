package aar226_akc55_ayc62_ahl88.asm.Instructions.arithmetic;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg2;

public class ASMLEA extends ASMArg2 {
    public ASMLEA(ASMExpr arg1, ASMExpr arg2) {
        super(ASMOpCodes.LEA, arg1, arg2);
    }
}
