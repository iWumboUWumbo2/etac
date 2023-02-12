package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class Globdecl {
    private Decl decl;
    private Value value;

    public Globdecl (Decl d, Value v) {
        decl = d;
        value = v;
    }

    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        decl.prettyPrint(p);
        p.endList();
    }
}
