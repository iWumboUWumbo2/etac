package aar226_akc55_ayc62_ahl88.asm.Instructions.jumps;

import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public class ASMJumpGE extends ASMAbstractJump {

    public ASMJumpGE(ASMExpr arg1){
        super(ASMOpCodes.JGE, arg1);
    }
}