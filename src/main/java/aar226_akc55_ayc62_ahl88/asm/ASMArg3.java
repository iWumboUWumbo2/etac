package aar226_akc55_ayc62_ahl88.asm;

public class ASMArg3 extends ASMInstruction{
    ASMExpr a1;
    ASMExpr a2;
    ASMExpr a3;
    public ASMArg3(ASMOpCodes op, ASMExpr arg1, ASMExpr arg2, ASMExpr arg3){
        super(op);
        a1 = arg1;
        a2 = arg2;
        a3 = arg3;
    }
}
