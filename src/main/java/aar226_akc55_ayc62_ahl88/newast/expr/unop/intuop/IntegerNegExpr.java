package aar226_akc55_ayc62_ahl88.newast.expr.unop.intuop;

import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.unop.intuop.IntUnop;
import aar226_akc55_ayc62_ahl88.newast.expr.unop.UnopEnum;

public class IntegerNegExpr extends IntUnop {
    /**
     * Class for Integer Negation Unary Operator
     *
     * @param in Expression input
     * @param l line number
     * @param c column number
     */
    public IntegerNegExpr(Expr in, int l, int c) {
        super(UnopEnum.INT_NEG, in, l, c);
    }
}
