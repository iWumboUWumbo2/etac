package aar226_akc55_ayc62_ahl88.asm.boolop;

import aar226_akc55_ayc62_ahl88.asm.ASMArg2;
import aar226_akc55_ayc62_ahl88.asm.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public class And extends ASMArg2 {
    public And(ASMExpr arg1, ASMExpr arg2) {
        super(ASMOpCodes.ADD, arg1, arg2);
    }
}
