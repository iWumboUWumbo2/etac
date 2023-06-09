package aar226_akc55_ayc62_ahl88.asm.Instructions.mov;

import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

/**
 * Class for normal move instruction
 */
public class ASMMov extends ASMAbstractMov {

    /**
     * @param left destination
     * @param right source
     */
    public ASMMov(ASMExpr left, ASMExpr right) {
        super(ASMOpCodes.MOV, left, right);
    }
}
