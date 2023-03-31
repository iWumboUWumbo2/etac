package aar226_akc55_ayc62_ahl88.asm.bitwise;

import aar226_akc55_ayc62_ahl88.asm.ASMArg2;
import aar226_akc55_ayc62_ahl88.asm.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public class Or extends ASMArg2 {
    public Or(ASMExpr arg1, ASMExpr arg2) {
        super(ASMOpCodes.OR, arg1, arg2);
    }
}