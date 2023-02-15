package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

public class Reassignment extends Stmt{
    private Id id;
    private Expr expression;

    ArrayList<Expr> indexes;

    public Reassignment(String s, Expr e) {
        id = new Id(s);
        expression = e;
        indexes = new ArrayList<Expr>();
    }

    public Reassignment(ArrayAccess a, Expr e) {
        expression = e;
        id = a.getId();
        indexes = a.getIndexes();
    }

    public String toString(){
        String build = "";
//        if (value != null){
////            System.out.println("IM HERE");
//            build +=  "( " + decl.toString() + " " +value.toString() +  " )";
//        }else{
//            build +=  "( " + decl.toString() +  " )";
//        }
        build +=  "( " + id.toString() +  " )";
        return build;
    }
    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        if (expression != null) {
            p.printAtom("=");
        }
        for (Expr index : indexes) {
            p.startList();
            p.printAtom("[]");
        }
        id.prettyPrint(p);
        for (int i = 0; i<indexes.size();i++){
            indexes.get(i).prettyPrint(p);
            p.endList();
        }
        if (expression != null) {
            expression.prettyPrint(p);
        }
        p.endList();
    }

}
