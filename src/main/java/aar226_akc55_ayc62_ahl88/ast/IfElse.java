package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class IfElse extends Stmt {
    private Expr expr;
    private Stmt ifState;
    private Stmt elseState;

    public IfElse(Expr e, Stmt ifS, Stmt elseS){
        expr = e;
        ifState = ifS;
        elseState = elseS;
    }
    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("if");
        expr.prettyPrint(p);
        ifState.prettyPrint(p);
        elseState.prettyPrint(p);
        p.endList();
    }
}
