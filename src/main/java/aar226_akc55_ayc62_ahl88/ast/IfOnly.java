package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class IfOnly extends Stmt {
    private Expr expr;
    private Stmt ifState;

    public IfOnly(Expr e, Stmt ifS){
        expr = e;
        ifState = ifS;
    }
    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("if");
        expr.prettyPrint(p);
        ifState.prettyPrint(p);
        p.endList();
    }
}