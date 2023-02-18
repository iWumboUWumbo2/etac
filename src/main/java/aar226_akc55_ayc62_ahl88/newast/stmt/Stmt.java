package aar226_akc55_ayc62_ahl88.newast.stmt;

import aar226_akc55_ayc62_ahl88.newast.AstNode;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public abstract class Stmt extends AstNode {


    public Stmt(int l, int c) {
        super(l, c);
    }

    public abstract void prettyPrint(CodeWriterSExpPrinter p);
}
