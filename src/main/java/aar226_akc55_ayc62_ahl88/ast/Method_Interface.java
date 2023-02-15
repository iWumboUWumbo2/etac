package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

public class Method_Interface implements Printer {
    private Id id;
    private ArrayList<Decl> decls;
    private ArrayList<Type> types;

    public Method_Interface(String s, ArrayList<Decl> d, ArrayList<Type> t){
        for (Decl cur: d){
            if (!cur.type.dimensions.allEmpty) {
                throw new Error("array in param list has init value");
            }
        }
        for (Type cur: t){
            if (!cur.dimensions.allEmpty) {
                throw new Error("array in type list has init value");
            }
        }
        id = new Id(s);
        decls = d;
        types = t;
    }

//    public Method(String s, ArrayList<Decl> d, ArrayList<Type> t){
//        for (Decl cur: d){
//            if (!cur.type.dimensions.allEmpty) {
//                throw new Error("array in param list has init value");
//            }
//        }
//        id = new Id(s);
//        decls = d;
//        types = t;
//        block = null;
////        block = b;
//    }

    public String toString(){
        String build = "";
        build +=  "( " + id.toString() + " " + decls.toString() + " " + types.toString() +  " )";
        return build;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startUnifiedList();
        id.prettyPrint(p);

        p.startList();
        for (Decl d : decls) d.prettyPrint(p);
        p.endList();

        p.startList();
        for (Type t : types) t.prettyPrint(p);
        p.endList();

        p.endList();
    }
}
