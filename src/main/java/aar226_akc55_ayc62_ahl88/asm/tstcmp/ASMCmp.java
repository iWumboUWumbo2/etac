package aar226_akc55_ayc62_ahl88.asm.tstcmp;

import aar226_akc55_ayc62_ahl88.asm.ASMArg2;
import aar226_akc55_ayc62_ahl88.asm.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public abstract class ASMCmp extends ASMArg2 {
    public ASMCmp(ASMExpr arg1, ASMExpr arg2){
        super(ASMOpCodes.CMP,arg1,arg2);
    }
}