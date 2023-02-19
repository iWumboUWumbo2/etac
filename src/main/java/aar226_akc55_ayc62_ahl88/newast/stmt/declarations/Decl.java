package aar226_akc55_ayc62_ahl88.newast.stmt.declarations;

import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.newast.stmt.Stmt;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/**
 * Abstract Class for Declaration Statements
 * works for a:int
 * works for a
 * works for _
 * works for a[2][4]
 */

public abstract class Decl extends Stmt {

    /**
     * Constructor for Abstract Declarations
     * @param l line number
     * @param c column number
     */
    public Decl(int l, int c) {
        super(l, c);
    }
    public abstract void prettyPrint(CodeWriterSExpPrinter p);
}

