package aar226_akc55_ayc62_ahl88.asm.Instructions.stackops;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg1;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

/**
 * Class for pop instruction
 */
public class ASMPop extends ASMArg1 {

    /**
     * @param arg register
     * Pops stack into register
     */
    public ASMPop(ASMExpr arg) {
        super(ASMOpCodes.POP, arg);
    }
}
