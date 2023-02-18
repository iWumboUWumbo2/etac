package aar226_akc55_ayc62_ahl88.newast.expr.binop.boolbop;

import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopEnum;

/**
 * Class for Greater Than Or Equals To Binary Operator
 */
public class GeqBinop extends EquivalenceBinop {
    /**
     * @param in1 left Expression
     * @param in2 right Expression
     * @param l line number
     * @param c column number
     */
    public GeqBinop (Expr in1, Expr in2, int l, int c) {
        super(BinopEnum.GEQ, in1, in2, l, c);
    }
}
