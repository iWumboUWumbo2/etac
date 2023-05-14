package aar226_akc55_ayc62_ahl88.asm.Instructions.tstcmp;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg1;

/**
 * Class for setl instruction
 */
public class ASMSetl extends ASMArg1 {

    /**
     * @param arg
     */
    public ASMSetl(ASMExpr arg) {super (ASMOpCodes.SETL, arg);}
}
