package aar226_akc55_ayc62_ahl88.newast.stmt.declstmt;

import aar226_akc55_ayc62_ahl88.newast.declarations.AnnotatedTypeDecl;
import aar226_akc55_ayc62_ahl88.newast.declarations.Decl;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.stmt.Stmt;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

public class MultiDeclAssignStmt extends Stmt {
    private ArrayList<Decl> decls;
    private ArrayList<Expr> expressions;

    public MultiDeclAssignStmt(ArrayList<Decl> d, ArrayList<Expr> e,int l, int c) {
        super(l,c);
        decls = d;
        for (Decl dec: d){
            if (dec instanceof AnnotatedTypeDecl){
                AnnotatedTypeDecl cast = (AnnotatedTypeDecl) dec;
                if (!cast.type.dimensions.allEmpty) {
                    throw new Error("array in param list has init value");
                }
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
