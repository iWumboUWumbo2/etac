package aar226_akc55_ayc62_ahl88.asm.Instructions;

import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public abstract class ASMArg1 extends ASMInstruction {

    ASMExpr left;
    public ASMArg1(ASMOpCodes op, ASMExpr arg1){
        super(op);
        left = arg1;
    }

    public static class ASMName extends ASMExpr {
    }
}
