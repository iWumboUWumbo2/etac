package aar226_akc55_ayc62_ahl88.asm.bitwise;

import aar226_akc55_ayc62_ahl88.asm.ASMArg1;
import aar226_akc55_ayc62_ahl88.asm.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public class Not extends ASMArg1 {
    public Not (ASMExpr arg){
        super(ASMOpCodes.NOT, arg);
    }
}
