package aar226_akc55_ayc62_ahl88.asm.Instructions.jumps;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg1;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public abstract class ASMAbstractJump extends ASMArg1 {


    public ASMAbstractJump(ASMOpCodes op, ASMExpr arg1){
        super(op,arg1);
    }

}
