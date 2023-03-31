package aar226_akc55_ayc62_ahl88.asm.arithmetic;

import aar226_akc55_ayc62_ahl88.asm.ASMArg1;
import aar226_akc55_ayc62_ahl88.asm.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public class ASMIDiv extends ASMArg1 {
    public ASMIDiv(ASMExpr divisor){
        super(ASMOpCodes.IDIV, divisor);
    }
}
