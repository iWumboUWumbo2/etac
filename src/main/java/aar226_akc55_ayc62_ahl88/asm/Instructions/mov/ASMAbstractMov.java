package aar226_akc55_ayc62_ahl88.asm.Instructions.mov;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg2;

public abstract class ASMAbstractMov extends ASMArg2 {
    public ASMAbstractMov(ASMOpCodes op, ASMExpr arg1, ASMExpr arg2) {
        super(op, arg1, arg2);
    }
}
