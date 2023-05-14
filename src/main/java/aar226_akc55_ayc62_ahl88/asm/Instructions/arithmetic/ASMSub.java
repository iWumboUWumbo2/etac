package aar226_akc55_ayc62_ahl88.asm.Instructions.arithmetic;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg2;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

/**
 * Class for subtraction instruction
 */
public class ASMSub extends ASMArg2 {

    /**
     * @param dest destination
     * @param src source
     */
    public ASMSub(ASMExpr dest, ASMExpr src){
        super(ASMOpCodes.SUB, dest, src);
    }
}
