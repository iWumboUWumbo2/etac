package aar226_akc55_ayc62_ahl88.newast.stmt.declstmt;


import aar226_akc55_ayc62_ahl88.newast.declarations.*;
import aar226_akc55_ayc62_ahl88.newast.expr.*;
import aar226_akc55_ayc62_ahl88.newast.stmt.Stmt;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class DeclNoAssignStmt extends Stmt {

    private Decl decl;

    public DeclNoAssignStmt (Decl d, int l, int c) {
        super(l,c);
        decl = d;
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
        decl.prettyPrint(p);
    }

}
