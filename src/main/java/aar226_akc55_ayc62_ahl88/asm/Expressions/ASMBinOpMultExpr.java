package aar226_akc55_ayc62_ahl88.asm.Expressions;

public class ASMBinOpMultExpr extends ASMBinOpExpr{

    public ASMBinOpMultExpr(ASMExpr arg1, ASMExpr arg2){
        super(arg1,arg2);
    }

    @Override
    public String toString() {
        return getLeft() + " * " + getRight();
    }
}