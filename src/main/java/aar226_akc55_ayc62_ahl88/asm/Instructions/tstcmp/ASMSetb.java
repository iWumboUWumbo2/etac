package aar226_akc55_ayc62_ahl88.asm.Instructions.tstcmp;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg1;

/**
 * Class for setb instruction
 */
public class ASMSetb extends ASMArg1 {

    /**
     * @param arg
     * Set register al to 1
     */
    public ASMSetb(ASMExpr arg){
        super(ASMOpCodes.SETB,arg);
    }
}
