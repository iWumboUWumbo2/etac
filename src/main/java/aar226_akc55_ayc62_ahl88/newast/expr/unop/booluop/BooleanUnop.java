package aar226_akc55_ayc62_ahl88.newast.expr.unop.booluop;

import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.unop.*;

public abstract class BooleanUnop extends UnopExpr {
    /**
     * Abstract class for all boolean unary expressions
     *
     * @param b   B
     * @param in
     * @param l
     * @param c
     */
    public BooleanUnop(UnopEnum b, Expr in, int l, int c) {
        super(b, in, l, c);
    }
}
