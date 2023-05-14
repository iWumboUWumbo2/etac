package aar226_akc55_ayc62_ahl88.asm.Expressions;

import java.util.Objects;

/**
 * Abstract class for binop operator
 */
public abstract class ASMBinOpExpr extends ASMExpr{
    private ASMExpr left;
    private ASMExpr right;

    /**
     * @param arg1 Left expr
     * @param arg2 Right expr
     */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ASMBinOpExpr that = (ASMBinOpExpr) o;

        if (!Objects.equals(left, that.left)) return false;
        return Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }
}
