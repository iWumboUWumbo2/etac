package aar226_akc55_ayc62_ahl88.newast.expr.binop;

import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public abstract class BinopExpr extends Expr {

    Expr e1;
    Expr e2;
    BinopEnum binopType;

    /**
     * Abstract class for all binary expressions
     * @param b B
     * @param in1
     * @param in2
     */
    public BinopExpr(BinopEnum b, Expr in1, Expr in2) {
        e1 = in1;
        e2 = in2;
        binopType = b;
    }
    public abstract void prettyPrint(CodeWriterSExpPrinter p);

}
