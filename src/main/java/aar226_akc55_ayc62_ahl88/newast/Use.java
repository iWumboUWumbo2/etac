package aar226_akc55_ayc62_ahl88.newast;

import aar226_akc55_ayc62_ahl88.newast.expr.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class Use {
    private Id id;

    public Use(Id id) {
        this.id = id;
    }

    public Use(String s, int l, int r) {
        this.id = new Id(s, l, r);
    }

    public String toString(){
        return "(" + "use " + id.toString() + ")";
    }

    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("use");
        id.prettyPrint(p);
        p.endList();
    }
}
