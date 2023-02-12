package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.SExpPrinter;

public class Decl {
    private Id id;
    private Type type;

    public Decl( String s, Type t){
        id = new Id(s);
        type = t;
    }

    public String toString(){
        return "(" + id.toString() + ":" + type.toString() + ")";
    }

    public void prettyPrint(SExpPrinter p) {
        p.startList();
        id.prettyPrint(p);

        p.endList();
    }
}