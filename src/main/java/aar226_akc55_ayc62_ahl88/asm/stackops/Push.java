package aar226_akc55_ayc62_ahl88.asm.stackops;

import aar226_akc55_ayc62_ahl88.asm.ASMArg1;
import aar226_akc55_ayc62_ahl88.asm.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public class Push extends ASMArg1 {
    public Push (ASMExpr arg) {
        super(ASMOpCodes.PUSH, arg);
    }
}
