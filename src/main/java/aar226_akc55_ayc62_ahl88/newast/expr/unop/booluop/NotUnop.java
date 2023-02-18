package aar226_akc55_ayc62_ahl88.newast.expr.unop.booluop;

import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopEnum;
import aar226_akc55_ayc62_ahl88.newast.expr.unop.UnopEnum;

public class NotUnop extends BooleanUnop {
    /**
     * Class for Boolean Negation Unary Operator
     *
     * @param in input Expression
     * @param l line number
     * @param c column number
     */
    public NotUnop(Expr in, int l, int c) {
        super(UnopEnum.NOT, in, l, c);
    }
}
