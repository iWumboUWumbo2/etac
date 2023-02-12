package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class If extends Stmt {
    private Expr expr;
    private Block block;
    private Else lse;

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("if");
        expr.prettyPrint(p);
        block.prettyPrint(p);
        lse.prettyPrint(p);
        p.endList();
    }
}
