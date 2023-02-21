package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class Globdecl implements Definition{
    private Decl decl;
    private Value value;

    public Globdecl (Decl d, Value v) {
        decl = d;
        value = v;
    }

    public String toString(){
        String build = "";
        if (value != null){
//            System.out.println("IM HERE");
            build +=  "( " + decl.toString() + " " +value.toString() +  " )";
        }else{
            build +=  "( " + decl.toString() +  " )";
        }
        return build;
    }
    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        decl.prettyPrint(p);
        if (value != null){
            ((Expr) value).prettyPrint(p);
        }
        p.endList();
    }

}
