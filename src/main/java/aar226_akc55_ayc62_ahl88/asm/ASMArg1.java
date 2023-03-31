package aar226_akc55_ayc62_ahl88.asm;

public class ASMArg1 extends ASMInstruction{

    ASMExpr left;
    public ASMArg1(ASMOpCodes op, ASMExpr arg1){
        super(op);
        left = arg1;
    }

    public static class ASMName extends ASMExpr {
    }
}
