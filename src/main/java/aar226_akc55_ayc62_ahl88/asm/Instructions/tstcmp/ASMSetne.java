package aar226_akc55_ayc62_ahl88.asm.Instructions.tstcmp;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg1;

/**
 * Class for setne instruction
 */
public class ASMSetne extends ASMArg1 {

    /**
     * @param arg
     */
    public ASMSetne(ASMExpr arg) {super (ASMOpCodes.SETNE, arg);}
}
