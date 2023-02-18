package aar226_akc55_ayc62_ahl88.newast.expr.unop;

import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopEnum;
import aar226_akc55_ayc62_ahl88.newast.expr.unop.UnopEnum;

public abstract class UnopExpr extends Expr {
    Expr e;
    UnopEnum unopType;

    /**
     * Abstract class for all Unary Expressions
     * @param b  Unary Enum
     * @param in Expression
     * @param l line number
     * @param c column number
     */
    public UnopExpr(UnopEnum u, Expr in, int l, int c) {
        super(l,c);
        e = in;
        unopType = u;
    }
//    public abstract void prettyPrint(CodeWriterSExpPrinter p);
}
