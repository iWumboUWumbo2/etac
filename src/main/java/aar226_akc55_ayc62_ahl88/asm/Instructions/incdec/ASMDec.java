package aar226_akc55_ayc62_ahl88.asm.Instructions.incdec;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg1;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

/**
 * Class for decrement instruction
 */
public class ASMDec extends ASMArg1 {

    /**
     * @param arg1
     */
    public ASMDec(ASMExpr arg1){
        super(ASMOpCodes.DEC, arg1);
    }
}
