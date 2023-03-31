package aar226_akc55_ayc62_ahl88.asm.subroutine;

import aar226_akc55_ayc62_ahl88.asm.ASMArg1;
import aar226_akc55_ayc62_ahl88.asm.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public class Call extends ASMArg1 {
    public Call(ASMExpr label) {
        super(ASMOpCodes.CALL, label);
    }
}