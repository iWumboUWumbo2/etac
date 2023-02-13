package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

public class MultiDeclStmt extends Stmt {
    private ArrayList<Decl> decls;
    private ArrayList<Expr> expressions;

    public MultiDeclStmt(ArrayList<Decl> d, ArrayList<Expr> e) {
        decls = d;
        for (Decl de: decls){
            if (de.type != null && !de.type.dimensions.allEmpty) {
                throw new Error("array with init len no Val");
            }
        }
        expressions = e;
    }
    public MultiDeclStmt(ArrayList<Decl> d) {
        decls = d;
        expressions = new ArrayList<Expr>();
    }

    public String toString(){
        String build = "";
//        if (value != null){
////            System.out.println("IM HERE");
//            build +=  "( " + decl.toString() + " " +value.toString() +  " )";
//        }else{
//            build +=  "( " + decl.toString() +  " )";
//        }
        build +=  "( " + decls.toString() +  " )";
        return build;
    }
    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        if (expressions != null) {
            p.printAtom("=");
        }
        p.startList();
        decls.forEach(e-> e.prettyPrint(p));
        p.endList();
        if (expressions != null) {
            expressions.forEach(e-> e.prettyPrint(p));
        }
        p.endList();
    }

}
