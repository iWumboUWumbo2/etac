package aar226_akc55_ayc62_ahl88.newast.expr.binop.boolbop;

import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopEnum;

public abstract class LogicalBinop extends BoolOutBinop {
    /**
     * Abstract class for all logical binary expressions (and, or, etc.)
     *
     * @param b
     * @param in1 left Expression
     * @param in2 right Expression
     * @param l line number
     * @param c column number
     */
    public LogicalBinop(BinopEnum b, Expr in1, Expr in2, int l, int c) {
        super(b, in1, in2, l, c);
    }
}
