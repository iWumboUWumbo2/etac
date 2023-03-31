package aar226_akc55_ayc62_ahl88.asm.tstcmp;

import aar226_akc55_ayc62_ahl88.asm.ASMArg2;
import aar226_akc55_ayc62_ahl88.asm.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public class ASMTest extends ASMArg2 {

    public ASMTest(ASMExpr arg1, ASMExpr arg2){
        super(ASMOpCodes.TEST,arg1,arg2);
    }
}
