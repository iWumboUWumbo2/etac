package aar226_akc55_ayc62_ahl88.newast.definitions;

import aar226_akc55_ayc62_ahl88.newast.declarations.*;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/**
 * Description for global declaration
 */
public class Globdecl extends Definition {
    private AnnotatedTypeDecl decl;
    private Expr value;

    /**
     * @param d Declaration
     * @param v Value
     * @param l Line number
     * @param c Column number
     */
    public Globdecl (AnnotatedTypeDecl d, Expr v, int l, int c) {
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
            value.prettyPrint(p);
        }
        p.endList();
    }

}
