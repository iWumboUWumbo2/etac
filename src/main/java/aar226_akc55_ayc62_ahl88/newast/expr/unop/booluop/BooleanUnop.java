package aar226_akc55_ayc62_ahl88.newast.expr.unop.booluop;

import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.unop.*;

/**
 * Abstract class for all boolean unary expressions
 */
public abstract class BooleanUnop extends UnopExpr {
    /**
     * @param b   Boolean Unop
     * @param in  Expressions Input 1
     * @param l   Line Number
     * @param c   Column Number
     */
    public BooleanUnop(UnopEnum b, Expr in, int l, int c) {
        super(b, in, l, c);
    }
}
