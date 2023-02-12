package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class BinaryExpr extends Expr {
    private String bop;
    private Expr e1, e2;

    public BinaryExpr(String bop, Expr e1, Expr e2) {
        this.bop = bop;
        this.e1 = e1;
        this.e2 = e2;
        this.type = Exprs.Bop;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom(bop);
        e1.prettyPrint(p);
        e2.prettyPrint(p);
        p.endList();
    }
}
