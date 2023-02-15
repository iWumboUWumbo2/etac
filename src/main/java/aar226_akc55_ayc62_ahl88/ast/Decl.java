package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.SExpPrinter;

public class Decl implements Printer {
    private Id id;
    public Type type;

    private Value value;

    private ArrayAccess acc;
    public Decl( String s, Type t){
        id = new Id(s);
        type = t;
    }
    public Decl( String s){
        id = new Id(s);
        type = null;
    }

    public Decl (String s, Type t, Value v) {
        id = new Id(s);
        type = t;
        value = v;
    }

    public Decl (ArrayAccess acc) {
        this.acc = acc;
    }


    public String toString(){
        if (this.value != null) {
            return "(" + id.toString() + ":" + type.toString() + " = " + value.toString() +  ")";
        } else {
            return "(" + id.toString() + ":" + type.toString() + ")";
        }
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
    if (type != null && id != null){
            p.startList();
            id.prettyPrint(p);
            type.prettyPrint(p);
            p.endList();
        } else if (id != null){
            id.prettyPrint(p);
        }
        else acc.prettyPrint(p);
    }
}