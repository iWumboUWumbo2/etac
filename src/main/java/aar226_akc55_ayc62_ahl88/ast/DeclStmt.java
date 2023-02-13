package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class DeclStmt extends Stmt{
    private Decl decl;
    private Expr expression;

    public DeclStmt (Decl d, Expr e) {
        decl = d;
        expression = e;
    }

    public String toString(){
        String build = "";
//        if (value != null){
////            System.out.println("IM HERE");
//            build +=  "( " + decl.toString() + " " +value.toString() +  " )";
//        }else{
//            build +=  "( " + decl.toString() +  " )";
//        }
        build +=  "( " + decl.toString() +  " )";
        return build;
    }
    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        if (expression != null) {
            p.printAtom("=");
        }
        p.startList();
        decl.prettyPrint(p);
        p.endList();
        if (expression != null) {
            expression.prettyPrint(p);
        }
        p.endList();
    }

}
