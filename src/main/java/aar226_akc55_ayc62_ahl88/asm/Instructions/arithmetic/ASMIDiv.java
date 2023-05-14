package aar226_akc55_ayc62_ahl88.asm.Instructions.arithmetic;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg1;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

/**
 * Class for division instruction
 */
public class ASMIDiv extends ASMArg1 {

    /**
     * @param divisor
     */
    public ASMIDiv(ASMExpr divisor){
        super(ASMOpCodes.IDIV, divisor);
    }
}
