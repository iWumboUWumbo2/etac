package aar226_akc55_ayc62_ahl88.asm.Expressions;

/**
 * Class for mult operator
 */
public class ASMBinOpMultExpr extends ASMBinOpExpr{

    /**
     * @param arg1 Left expr
     * @param arg2 Right expr
     */
    public ASMBinOpMultExpr(ASMExpr arg1, ASMExpr arg2){
        super(arg1,arg2);
    }

    @Override
    public String toString() {
        return getLeft() + " * " + getRight();
    }
}