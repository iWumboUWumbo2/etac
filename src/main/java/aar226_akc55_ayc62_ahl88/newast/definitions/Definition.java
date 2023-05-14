package aar226_akc55_ayc62_ahl88.newast.definitions;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.AstNode;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;
import java.util.HashSet;

/**
 * Abstract class for definitions.
 */
public abstract class Definition extends AstNode{

    /**
     * @param l
     * @param c
     */
    public Definition(int l, int c) {
        super(l, c);
    }

    public abstract Type typeCheck(SymbolTable<Type> table);
    public abstract IRNode accept(IRVisitor visitor);
    public abstract Type firstPass(SymbolTable<Type> table, HashSet<String> currentFile);
}
