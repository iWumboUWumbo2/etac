package aar226_akc55_ayc62_ahl88.newast.stmt;

import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/**
 * Class for If-Statement without Else
 */
public class IfOnly extends Stmt {
    private Expr guard;
    private Stmt ifState;

    /**
     * @param e Expression
     * @param ifS If Statement
     * @param l Line
     * @param c Column
     */
    public IfOnly(Expr e, Stmt ifS, int l, int c) {
        super(l, c);
        guard = e;
        ifState = ifS;
    }
    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("if");
        guard.prettyPrint(p);
        ifState.prettyPrint(p);
        p.endList();
    }
}
