package aar226_akc55_ayc62_ahl88.asm.Expressions;

public class ASMBinOpSubExpr extends ASMBinOpExpr{
    public ASMBinOpSubExpr(ASMExpr arg1, ASMExpr arg2){
        super(arg1,arg2);
    }

    @Override
    public String toString() {
        if (getRight() instanceof ASMConstExpr cons && cons.getValue() < 0){
            long abs = Math.abs(cons.getValue());
            return getLeft().toString() + " + " + Long.toString(abs);
        }
        return getLeft() + " - " + getRight();
    }

}