package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

public class MultiGlobalDecl implements Definition{
    private ArrayList<Decl> decls;
    private ArrayList<Value> expressions;

    public MultiGlobalDecl (ArrayList<Decl> d, ArrayList<Value> e, int left, int right) {
        decls = d;
        for (Decl de: decls){
            if (de.type != null && !de.type.dimensions.allEmpty) {
                throw new Error(left + ":" + right +" array with init len no Val");
            }
            if (de.type != null && (de.type.dimensions.getDim() != 0)){
                throw new Error(left + ":" + right + " array can't have gets");
            }
        }
        expressions = e;
    }
    public MultiGlobalDecl(ArrayList<Decl> d) {
        decls = d;
        expressions = null ;
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

        if (expressions == null){
            p.startList();
            decls.forEach(e-> e.prettyPrint(p));
            p.endList();
        }else {
            p.startList();
            if (expressions != null) {
                p.printAtom("=");
            }
            p.startList();
            decls.forEach(e -> e.prettyPrint(p));
            p.endList();
            if (expressions != null) {
                expressions.forEach(e -> ((Expr) e).prettyPrint(p));
            }
            p.endList();
        }
    }

}
