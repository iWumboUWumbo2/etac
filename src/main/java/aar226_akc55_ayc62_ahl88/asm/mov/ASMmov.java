package aar226_akc55_ayc62_ahl88.asm.mov;

import aar226_akc55_ayc62_ahl88.asm.ASMArg2;
import aar226_akc55_ayc62_ahl88.asm.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public class ASMmov extends ASMArg2 {
    public ASMmov(ASMExpr left, ASMExpr right) {
        super(ASMOpCodes.MOV, left, right);
    }
}
