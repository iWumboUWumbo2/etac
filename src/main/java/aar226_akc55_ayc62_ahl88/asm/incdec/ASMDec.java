package aar226_akc55_ayc62_ahl88.asm.incdec;

import aar226_akc55_ayc62_ahl88.asm.ASMArg1;
import aar226_akc55_ayc62_ahl88.asm.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public class ASMDec extends ASMArg1 {
    public ASMDec(ASMExpr arg1){
        super(ASMOpCodes.DEC, arg1);
    }
}
