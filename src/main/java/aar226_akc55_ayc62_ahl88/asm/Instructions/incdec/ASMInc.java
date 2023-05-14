package aar226_akc55_ayc62_ahl88.asm.Instructions.incdec;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg1;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

/**
 * Class for increment instruction
 */
public class ASMInc extends ASMArg1 {

    /**
     * @param arg1 left
     */
    public ASMInc(ASMExpr arg1){
        super(ASMOpCodes.INC, arg1);
    }
}
