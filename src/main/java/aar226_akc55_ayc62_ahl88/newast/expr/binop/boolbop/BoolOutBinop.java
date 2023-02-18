package aar226_akc55_ayc62_ahl88.newast.expr.binop.boolbop;

import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopEnum;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopExpr;

public abstract class BoolOutBinop extends BinopExpr {
    /**
     * Abstract class for all binary expressions with boolean output
     *
     * @param b
     * @param in1 left Expression
     * @param in2 right Expression
     * @param l line number
     * @param c column number
     */
    public BoolOutBinop(BinopEnum b, Expr in1, Expr in2, int l, int c) {
        super(b, in1, in2, l, c);
    }

    // Type checking output is Bool
}
