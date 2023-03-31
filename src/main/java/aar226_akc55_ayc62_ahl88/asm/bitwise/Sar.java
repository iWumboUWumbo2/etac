package aar226_akc55_ayc62_ahl88.asm.bitwise;

import aar226_akc55_ayc62_ahl88.asm.ASMArg2;
import aar226_akc55_ayc62_ahl88.asm.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public class Sar extends ASMArg2 {
    public Sar(ASMExpr arg1, ASMExpr arg2) {
        super(ASMOpCodes.SAR, arg1, arg2);
    }
}
