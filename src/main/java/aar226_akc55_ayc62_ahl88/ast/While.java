package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class While extends Stmt {
    private Expr expr;
    private Block block;

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("while");
        expr.prettyPrint(p);
        block.prettyPrint(p);
        p.endList();
    }
}
