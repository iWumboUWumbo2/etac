package aar226_akc55_ayc62_ahl88.asm.Instructions.jumps;

import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

/**
 * Class for jump not equal to instruction
 */
public class ASMJumpNotEqual extends ASMAbstractJump {

    /**
     * @param arg1
     */
    public ASMJumpNotEqual(ASMExpr arg1){
        super(ASMOpCodes.JNE,arg1);
    }
}
