package aar226_akc55_ayc62_ahl88.newast.expr.unop.intuop;

import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.unop.UnopEnum;
import aar226_akc55_ayc62_ahl88.newast.expr.unop.UnopExpr;

/**
 * Abstract class for all Integer unary expressions
 */
public abstract class IntUnop extends UnopExpr {
    /**
     * @param b   Integer Enum for unary Expressions
     * @param in  input Expressions 1
     * @param l   Line Number
     * @param c   Column Number
     */
    public IntUnop(UnopEnum b, Expr in, int l, int c) {
        super(b, in, l, c);
    }
}
