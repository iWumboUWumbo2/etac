package aar226_akc55_ayc62_ahl88.newast.stmt;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.AstNode;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/**
 * Abstract class for all statements
 */
public abstract class Stmt extends AstNode {


    /**
     * @param l line number
     * @param c column number
     */
    public Stmt(int l, int c) {
        super(l, c);
    }

    public abstract void prettyPrint(CodeWriterSExpPrinter p);
    public abstract Type typeCheck(SymbolTable<Type> table);

    public boolean isRType(Type t){
        return t.getType() ==Type.TypeCheckingType.UNIT || t.getType() ==Type.TypeCheckingType.VOID;
    }
}
