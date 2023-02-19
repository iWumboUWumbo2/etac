package aar226_akc55_ayc62_ahl88.newast.definitions;

import aar226_akc55_ayc62_ahl88.ast.Decl;
import aar226_akc55_ayc62_ahl88.ast.Expr;
import aar226_akc55_ayc62_ahl88.ast.Value;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class Globdecl extends Definition {
    private Decl decl;
    private Value value;

    public Globdecl (Decl d, Value v, int l, int c) {
        super(l, c);
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
