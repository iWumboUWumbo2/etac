package aar226_akc55_ayc62_ahl88.newast.expr.binop.intbop;

import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopEnum;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopExpr;

public abstract class IntOutBinop extends BinopExpr {

    /**
     * Abstract class for all binary expressions
     *
     * @param b   B
     * @param in1
     * @param in2
     * @param l
     * @param c
     */
    public IntOutBinop(BinopEnum b, Expr in1, Expr in2, int l, int c) {
        super(b, in1, in2, l, c);
    }
}
