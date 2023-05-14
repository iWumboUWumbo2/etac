package aar226_akc55_ayc62_ahl88.asm.Instructions.arithmetic;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg2;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

/**
 * Class for add instruction
 */
public class ASMAdd extends ASMArg2 {

    /**
     * @param dest destination
     * @param src source
     */
    public ASMAdd(ASMExpr dest, ASMExpr src){
        super(ASMOpCodes.ADD, dest, src);
    }
}
