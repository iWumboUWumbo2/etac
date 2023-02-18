package aar226_akc55_ayc62_ahl88.newast.expr.binop.boolbop;

import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopEnum;

public class EqualsBinop extends EquivalenceBinop {
    /**
     * Class for Equals Binary Operator
     *
     * @param in1 left Expression
     * @param in2 right Expression
     * @param l line number
     * @param c column number
     */
    public EqualsBinop (Expr in1, Expr in2, int l, int c) {
        super(BinopEnum.EQUALS, in1, in2, l, c);
    }
}
