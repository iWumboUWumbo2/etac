package aar226_akc55_ayc62_ahl88.asm.Instructions.jumps;

import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

/**
 * Class for jump equal instruction
 */
public class ASMJumpEqual extends ASMAbstractJump {

    /**
     * @param arg1
     */
    public ASMJumpEqual(ASMExpr arg1){
        super(ASMOpCodes.JE,arg1);
    }
}
