package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class Use implements Printer {
    private Id id;

    public Use(Id id) {
        this.id = id;
    }
    public Use(String s) {
        this.id = new Id(s);
    }
    public String toString(){
        return "(" + "use " + id.toString() + ")";
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("use");
        id.prettyPrint(p);
        p.endList();
    }
}
