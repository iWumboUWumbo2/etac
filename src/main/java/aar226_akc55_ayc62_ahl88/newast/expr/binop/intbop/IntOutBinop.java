package aar226_akc55_ayc62_ahl88.newast.expr.binop.intbop;

import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopEnum;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopExpr;

/**
 * Abstract class for all binary expressions
 */
public abstract class IntOutBinop extends BinopExpr {

    /**
     * @param b binary operation type
     * @param in1 first expression input
     * @param in2 second expressioin input
     * @param l line number
     * @param c column number
     */
    public IntOutBinop(BinopEnum b, Expr in1, Expr in2, int l, int c) {
        super(b, in1, in2, l, c);
    }
    // Type Checking is In1, In2 both must be Int and output is Int
}
