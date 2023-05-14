package aar226_akc55_ayc62_ahl88.asm.Instructions.arithmetic;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg2;

/**
 * Class for load effective address instruction
 */
public class ASMLEA extends ASMArg2 {
    /**
     * @param arg1 address
     * @param arg2 register
     */
    public ASMLEA(ASMExpr arg1, ASMExpr arg2) {
        super(ASMOpCodes.LEA, arg1, arg2);
    }
}
