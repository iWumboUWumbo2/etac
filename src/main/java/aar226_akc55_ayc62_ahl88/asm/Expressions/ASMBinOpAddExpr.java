package aar226_akc55_ayc62_ahl88.asm.Expressions;

/**
 * Class for add operator
 */
public class ASMBinOpAddExpr extends ASMBinOpExpr{

    /**
     * @param arg1 Left expr
     * @param arg2 Right expr
     */
    public ASMBinOpAddExpr(ASMExpr arg1, ASMExpr arg2){
        super(arg1,arg2);
    }
    @Override
    public String toString() {
        if (getRight() instanceof ASMConstExpr cons && cons.getValue() <0){
            long abs = Math.abs(cons.getValue());
            return getLeft().toString() + " - " + Long.toString(abs);
        }
        return getLeft() + " + " + getRight();
    }
}
