package aar226_akc55_ayc62_ahl88.asm.Instructions.jumps;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;

/**
 * Class for jump below unsigned instruction
 */
public class ASMJumpULT extends ASMAbstractJump{

    /**
     * @param arg1
     */
    public ASMJumpULT(ASMExpr arg1){
        super(ASMOpCodes.JB,arg1);
    }

}
