package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

class UnaryExpr extends Expr {
    private String uop;
    private Expr e;

    public UnaryExpr(String uop, Expr e) {
        this.uop = uop;
        this.e = e;
        this.type = Exprs.Uop;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom(uop);
        e.prettyPrint(p);
        p.endList();
    }

}
