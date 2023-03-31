package aar226_akc55_ayc62_ahl88.asm.stackops;

import aar226_akc55_ayc62_ahl88.asm.ASMArg1;
import aar226_akc55_ayc62_ahl88.asm.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public class Pop extends ASMArg1 {
    public Pop (ASMExpr arg) {
        super(ASMOpCodes.POP, arg);
    }
}
