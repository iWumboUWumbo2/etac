package aar226_akc55_ayc62_ahl88.asm.Instructions.tstcmp;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg1;

/**
 * Class for setg instruction
 */
public class ASMSetg extends ASMArg1 {

    /**
     * @param arg
     */
    public ASMSetg(ASMExpr arg) {super (ASMOpCodes.SETG, arg);}
}
