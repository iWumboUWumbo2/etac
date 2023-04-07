package aar226_akc55_ayc62_ahl88.asm.Expressions;

public abstract class ASMBinOpExpr extends ASMExpr{
    private ASMExpr left;
    private ASMExpr right;
    ASMBinOpExpr(ASMExpr arg1, ASMExpr arg2){
        left = arg1;
        right = arg2;
    }

    public ASMExpr getRight() {
        return right;
    }

    public ASMExpr getLeft() {
        return left;
    }
}
