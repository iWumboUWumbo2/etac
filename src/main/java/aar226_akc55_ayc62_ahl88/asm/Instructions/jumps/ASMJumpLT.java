package aar226_akc55_ayc62_ahl88.asm.Instructions.jumps;

import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public class ASMJumpLT extends ASMAbstractJump {

    public ASMJumpLT(ASMExpr arg1){
        super(ASMOpCodes.JL,arg1);
    }
}