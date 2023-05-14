package aar226_akc55_ayc62_ahl88.asm.Instructions.bitwise;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg2;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

/**
 * Class for shift logical left instruction
 */
public class ASMShl extends ASMArg2 {

    /**
     * @param arg1
     * @param arg2
     */
    public ASMShl(ASMExpr arg1, ASMExpr arg2) {
        super(ASMOpCodes.SHL, arg1, arg2);
    }
}
