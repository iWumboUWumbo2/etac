package aar226_akc55_ayc62_ahl88.asm.Expressions;

public class ASMBinOpAddExpr extends ASMBinOpExpr{

    public ASMBinOpAddExpr(ASMExpr arg1, ASMExpr arg2){
        super(arg1,arg2);
    }
    @Override
    public String toString() {
        if (getRight() instanceof ASMConstExpr cons && cons.getValue() <0){
            return getLeft().toString() + getRight().toString();
        }
        return getLeft() + " + " + getRight();
    }
}
