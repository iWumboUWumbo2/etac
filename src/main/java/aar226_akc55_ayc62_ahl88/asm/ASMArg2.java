package aar226_akc55_ayc62_ahl88.asm;

public abstract class ASMArg2 extends ASMInstruction{

    ASMExpr left;
    ASMExpr right;
    public ASMArg2(ASMOpCodes op, ASMExpr arg1, ASMExpr arg2){
        super(op);
        left = arg1;
        right = arg2;
    }
}
