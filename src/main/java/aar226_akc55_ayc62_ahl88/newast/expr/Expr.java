package aar226_akc55_ayc62_ahl88.newast.expr;

import aar226_akc55_ayc62_ahl88.newast.AstNode;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public abstract class Expr extends AstNode {
    public Expr(int l, int c) {
        super(l, c);
    }
    public abstract void prettyPrint(CodeWriterSExpPrinter p);
}
