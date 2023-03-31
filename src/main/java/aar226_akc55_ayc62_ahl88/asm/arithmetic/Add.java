package aar226_akc55_ayc62_ahl88.asm.arithmetic;

import aar226_akc55_ayc62_ahl88.asm.ASMArg2;
import aar226_akc55_ayc62_ahl88.asm.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public class Add extends ASMArg2 {
    public Add(ASMExpr dest, ASMExpr src){
        super(ASMOpCodes.ADD, dest, src);
    }
}
