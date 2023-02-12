package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

public class Method implements Printer {
    private Id id;
    private ArrayList<Decl> decls;
    private ArrayList<Type> types;
    private Block block;

//    , Block b
    public Method(String s, ArrayList<Decl> d, ArrayList<Type> t){
        id = new Id(s);
        decls = d;
        types = t;
//        block = b;
    }

    public String toString(){
        String build = "";
        build +=  "( " + id.toString() + " " + decls.toString() + " " + types.toString()+ " )";
        return build;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
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
