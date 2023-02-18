package aar226_akc55_ayc62_ahl88.newast.stmt;

import aar226_akc55_ayc62_ahl88.ast.Expr;
import aar226_akc55_ayc62_ahl88.ast.Stmt;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class While extends Stmt {
    private Expr expr;
    private Stmt stmt;

    public While(Expr expr, Stmt s) {
        this.expr = expr;
        stmt = s;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("while");
        expr.prettyPrint(p);
        stmt.prettyPrint(p);
        p.endList();
    }

}
