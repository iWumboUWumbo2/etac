package aar226_akc55_ayc62_ahl88.asm.Instructions.stackops;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg1;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

/**
 * Class for push instruction
 */
public class ASMPush extends ASMArg1 {

    /**
     * @param arg register
     * Push register onto stack
     */
    public ASMPush(ASMExpr arg) {
        super(ASMOpCodes.PUSH, arg);
    }
}
