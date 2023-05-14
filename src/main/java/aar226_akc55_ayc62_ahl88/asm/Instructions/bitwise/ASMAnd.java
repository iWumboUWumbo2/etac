package aar226_akc55_ayc62_ahl88.asm.Instructions.bitwise;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg2;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

/**
 * Class for and instruction
 */
public class ASMAnd extends ASMArg2 {

    /**
     * @param arg1 Left
     * @param arg2 Right
     */
    public ASMAnd(ASMExpr arg1, ASMExpr arg2) {
        super(ASMOpCodes.AND, arg1, arg2);
    }
}
