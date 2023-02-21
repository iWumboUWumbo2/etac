package aar226_akc55_ayc62_ahl88.newast.expr.binop.boolbop;

import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopEnum;


/**
 * Abstract class for integer all equivalence binary expressions (>, <, >=, <=)
 * Integer inputs only
 */
public abstract class IntegerComparisonBinop extends BoolOutBinop{
    /**
     * @param b   binary operation type
     * @param in1 left Expression
     * @param in2 right Expression
     * @param l   line number
     * @param c   column number
     */
    public IntegerComparisonBinop(BinopEnum b, Expr in1, Expr in2, int l, int c) {
        super(b, in1, in2, l, c);
    }

    // typecheck ints only -> bool
}
