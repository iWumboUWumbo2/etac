package aar226_akc55_ayc62_ahl88.asm.Instructions.jumps;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;

public class ASMJumpULT extends ASMAbstractJump{
    public ASMJumpULT(ASMExpr arg1){
        super(ASMOpCodes.JB,arg1);
    }

}
