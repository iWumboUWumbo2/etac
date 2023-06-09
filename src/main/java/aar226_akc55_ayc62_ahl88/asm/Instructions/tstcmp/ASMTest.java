package aar226_akc55_ayc62_ahl88.asm.Instructions.tstcmp;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg2;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

/**
 * Class for test instruction
 */
public class ASMTest extends ASMArg2 {

    /**
     * @param arg1
     * @param arg2
     * Bitwise and
     */
    public ASMTest(ASMExpr arg1, ASMExpr arg2){
        super(ASMOpCodes.TEST,arg1,arg2);
    }
}
