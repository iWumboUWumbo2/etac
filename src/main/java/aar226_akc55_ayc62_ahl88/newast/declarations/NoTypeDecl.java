package aar226_akc55_ayc62_ahl88.newast.declarations;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;


/**
 * Class For Declarations that don't have a type
 * Annotation
 * Ex: a , bob, job etc
 * Not: a:int, bob:bool[]
 */
public class NoTypeDecl extends Decl{
    /**
     * @param i Identifier Input
     * @param l Line Number
     * @param c Column Number
     */
    public NoTypeDecl(Id i, int l, int c) {
        super(i,l, c);
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        identifier.prettyPrint(p);
    }

    @Override
    public Type typeCheck(SymbolTable<Type> table) {
        nodeType = table.lookup(identifier);
        return nodeType;
    }
}