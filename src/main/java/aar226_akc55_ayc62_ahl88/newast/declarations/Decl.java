package aar226_akc55_ayc62_ahl88.newast.declarations;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.AstNode;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

/**
 * Abstract Class for Declaration Statements
 * works for a:int
 * works for a
 * works for _
 * works for a[2][4]
 */

public abstract class Decl extends AstNode {

    /**
     * Constructor for Abstract Declarations
     * @param l line number
     * @param c column number
     */

    public Id identifier;
    public Decl(Id i , int l, int c) {
        super(l, c);
        identifier = i;
    }
    public abstract void prettyPrint(CodeWriterSExpPrinter p);

    public abstract Type typeCheck(SymbolTable<Type> table);

    public abstract IRNode accept(IRVisitor visitor);

    public Id getIdentifier() {
        return identifier;
    }
}

