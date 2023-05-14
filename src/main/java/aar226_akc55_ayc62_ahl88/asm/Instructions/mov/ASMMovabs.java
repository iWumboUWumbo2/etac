package aar226_akc55_ayc62_ahl88.asm.Instructions.mov;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;

/**
 * Class for movabs instruction
 */
public class ASMMovabs extends ASMAbstractMov {

    /**
     * @param left destination
     * @param right source
     */
    public ASMMovabs(ASMExpr left, ASMExpr right) {
        super(ASMOpCodes.MOVABS, left, right);
    }
}
