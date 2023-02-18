package aar226_akc55_ayc62_ahl88.newast.expr.unop.intuop;

import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.unop.UnopEnum;
import aar226_akc55_ayc62_ahl88.newast.expr.unop.UnopExpr;

public abstract class IntUnop extends UnopExpr {
    /**
     * Abstract class for all integer unary expressions
     *
     * @param b   B
     * @param in
     * @param l
     * @param c
     */
    public IntUnop(UnopEnum b, Expr in, int l, int c) {
        super(b, in, l, c);
    }
}
