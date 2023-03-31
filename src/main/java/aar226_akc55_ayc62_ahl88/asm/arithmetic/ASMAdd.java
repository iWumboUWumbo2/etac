package aar226_akc55_ayc62_ahl88.asm.arithmetic;

import aar226_akc55_ayc62_ahl88.asm.ASMArg2;
import aar226_akc55_ayc62_ahl88.asm.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public class ASMAdd extends ASMArg2 {
    public ASMAdd(ASMExpr dest, ASMExpr src){
        super(ASMOpCodes.ADD, dest, src);
    }
}
