package aar226_akc55_ayc62_ahl88.asm.stackops;

import aar226_akc55_ayc62_ahl88.asm.ASMArg1;
import aar226_akc55_ayc62_ahl88.asm.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public class ASMPush extends ASMArg1 {
    public ASMPush(ASMExpr arg) {
        super(ASMOpCodes.PUSH, arg);
    }
}
