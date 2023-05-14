package aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg2;

/**
 * Class for enter instruction
 */
public class ASMEnter extends ASMArg2 {

    /**
     * @param size
     * @param offset
     * Set up new stack frame
     */
    public ASMEnter(ASMExpr size, ASMExpr offset){
        super(ASMOpCodes.ENTER, size,offset);
    }
}
