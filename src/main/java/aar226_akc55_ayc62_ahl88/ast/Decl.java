package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.SExpPrinter;

public class Decl extends Stmt implements Printer {
    private Id id;
    private Type type;

    private Value value;
    public Decl( String s, Type t){
        id = new Id(s);
        type = t;
    }

    public Decl (String s, Type t, Value v) {
        id = new Id(s);
        type = t;
        value = v;
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
        id.prettyPrint(p);
        type.prettyPrint(p);
    }
}