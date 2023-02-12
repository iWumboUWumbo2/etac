package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

public class Procedure extends Stmt {
    private Id id;
    private ArrayList<Param> params;

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        id.prettyPrint(p);
        params.forEach(e -> e.prettyPrint(p));
        p.endList();
    }
}
