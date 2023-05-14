package aar226_akc55_ayc62_ahl88.asm.Instructions.jumps;

import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

/**
 * Class for jump less than or equal to instruction
 */
public class ASMJumpLE extends ASMAbstractJump {

    /**
     * @param arg1
     */
    public ASMJumpLE(ASMExpr arg1){
        super(ASMOpCodes.JLE,arg1);
    }
}