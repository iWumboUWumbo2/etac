package aar226_akc55_ayc62_ahl88.newast.definitions;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.SemanticException;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.declarations.AnnotatedTypeDecl;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

public class MultiGlobalDecl extends Definition{
    private ArrayList<AnnotatedTypeDecl> decls;
    private ArrayList<Expr> expressions;

    public MultiGlobalDecl (ArrayList<AnnotatedTypeDecl > d, ArrayList<Expr> e, int left, int right) {
        super(left,right);
        decls = d;
        for (AnnotatedTypeDecl de: decls){
            if (!de.type.dimensions.allEmpty) {
                throw new Error(left + ":" + right +" error: array with init len no Val");
            }
            if ((de.type.dimensions.getDim() != 0)){
                throw new Error(left + ":" + right + " error: array can't have gets");
            }
        }
        expressions = e;
    }

    public String toString(){
        String build = "";
        build +=  "( " + decls.toString() +  " )";
        return build;
    }

    @Override
    public Type firstPass(SymbolTable<Type> table) {
        if (decls.size() != expressions.size()) {
            throw new SemanticException(getLine(),getColumn(),"size of declarations dont match expressions");
        }
        for (AnnotatedTypeDecl atd: decls){
            Type curDeclType = atd.typeCheck(table);
            if (table.contains(atd.identifier)){
                throw new SemanticException(getLine(),getColumn(),"error: decl is already defined");
            }
            table.add(atd.identifier,curDeclType);
        }

        return new Type(Type.TypeCheckingType.UNIT);
    }
    @Override
    public Type typeCheck(SymbolTable<Type> table) {
        return new Type(Type.TypeCheckingType.UNIT);
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("=");
        p.startList();
        decls.forEach(e -> e.prettyPrint(p));
        p.endList();
        expressions.forEach(e -> e.prettyPrint(p));
        p.endList();
    }

}
