package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.SExpPrinter;

class Id implements Printer {
    private String id;
    public Id(String id) {
        this.id = id;
    }
    public String toString() {
        return id;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.printAtom(id);
    }
}
