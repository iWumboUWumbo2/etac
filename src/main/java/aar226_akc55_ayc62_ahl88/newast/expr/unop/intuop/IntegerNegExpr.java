package aar226_akc55_ayc62_ahl88.newast.expr.unop.intuop;

import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.unop.UnopEnum;

/**
 * Class for Integer Negation Unary Operator
 */
public class IntegerNegExpr extends IntUnop {
    /**
     * @param in Expression input
     * @param l line number
     * @param c column number
     */
    public IntegerNegExpr(Expr in, int l, int c) {
        super(UnopEnum.INT_NEG, in, l, c);
    }
}
