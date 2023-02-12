package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.SExpPrinter;

public class Use {
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
    public void prettyPrint(SExpPrinter p) {
        p.startList();
        p.printAtom("use");
        p.printAtom(id.toString());
        p.endList();
    }
}
