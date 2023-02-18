package aar226_akc55_ayc62_ahl88.newast.expr.binop.intbop;

import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopEnum;

public class MinusBinop extends IntOutBinop {
    /**
     * Class for Minus Binary Operator
     *
     * @param in1 left Expression
     * @param in2 right Expression
     * @param l line number
     * @param c column number
     */
    public MinusBinop(Expr in1, Expr in2, int l, int c) {
        super(BinopEnum.MINUS, in1, in2, l, c);
    }
}
