package aar226_akc55_ayc62_ahl88.asm.Instructions.jumps;

import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

/**
 * Class for jump greater than instruction
 */
public class ASMJumpGT extends ASMAbstractJump {

    /**
     * @param arg1
     */
    public ASMJumpGT(ASMExpr arg1){
        super(ASMOpCodes.JG,arg1);
    }
}