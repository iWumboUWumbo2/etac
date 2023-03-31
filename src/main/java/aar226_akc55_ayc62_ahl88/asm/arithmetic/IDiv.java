package aar226_akc55_ayc62_ahl88.asm.arithmetic;

import aar226_akc55_ayc62_ahl88.asm.ASMArg1;
import aar226_akc55_ayc62_ahl88.asm.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public class IDiv extends ASMArg1 {
    public IDiv(ASMExpr divisor){
        super(ASMOpCodes.IDIV, divisor);
    }
}
