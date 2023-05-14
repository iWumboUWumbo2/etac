package aar226_akc55_ayc62_ahl88.asm.Instructions.bitwise;

import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMConstExpr;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg2;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

/**
 * Class for shift arithmetic right instruction
 */
public class ASMSar extends ASMArg2 {

    /**
     * @param arg1
     * @param arg2
     */
    public ASMSar(ASMExpr arg1, ASMExpr arg2) {
        super(ASMOpCodes.SAR, arg1, arg2);
    }

    /**
     * @param arg
     * Shfit arg by 1
     */
    public ASMSar(ASMExpr arg) {
        super(ASMOpCodes.SAR, arg, new ASMConstExpr(1));
    }
}
