package aar226_akc55_ayc62_ahl88.newast.definitions;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.AstNode;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public abstract class Definition extends AstNode{

    public Definition(int l, int c) {
        super(l, c);
    }

    public abstract Type typeCheck(SymbolTable<Type> table);
}
