package aar226_akc55_ayc62_ahl88.asm.Instructions.tstcmp;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg1;

/**
 * Class for setle instruction
 */
public class ASMSetle extends ASMArg1 {

    /**
     * @param arg
     */
    public ASMSetle(ASMExpr arg) {super (ASMOpCodes.SETLE, arg);}
}
