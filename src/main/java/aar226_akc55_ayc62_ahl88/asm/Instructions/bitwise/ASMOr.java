package aar226_akc55_ayc62_ahl88.asm.Instructions.bitwise;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg2;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

/**
 * Class for or instruction
 */
public class ASMOr extends ASMArg2 {

    /**
     * @param arg1
     * @param arg2
     */
    public ASMOr(ASMExpr arg1, ASMExpr arg2) {
        super(ASMOpCodes.OR, arg1, arg2);
    }
}
