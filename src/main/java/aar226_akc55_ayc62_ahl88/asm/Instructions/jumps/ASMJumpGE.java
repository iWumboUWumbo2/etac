package aar226_akc55_ayc62_ahl88.asm.Instructions.jumps;

import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

/**
 * Class for jump greater than or equal to instruction
 */
public class ASMJumpGE extends ASMAbstractJump {

    /**
     * @param arg1
     */
    public ASMJumpGE(ASMExpr arg1){
        super(ASMOpCodes.JGE, arg1);
    }
}