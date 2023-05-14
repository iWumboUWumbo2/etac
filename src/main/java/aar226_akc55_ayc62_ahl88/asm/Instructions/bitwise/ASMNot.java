package aar226_akc55_ayc62_ahl88.asm.Instructions.bitwise;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg1;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

/**
 * Class for not instruction
 */
public class ASMNot extends ASMArg1 {
    /**
     * @param arg
     */
    public ASMNot(ASMExpr arg){
        super(ASMOpCodes.NOT, arg);
    }
}
