package aar226_akc55_ayc62_ahl88.asm.Instructions.bitwise;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg2;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

/**
 * Class for xor instruction
 */
public class ASMXor extends ASMArg2 {

    /**
     * @param arg1 left
     * @param arg2 right
     */
    public ASMXor(ASMExpr arg1, ASMExpr arg2) {
        super(ASMOpCodes.XOR, arg1, arg2);
    }
}
