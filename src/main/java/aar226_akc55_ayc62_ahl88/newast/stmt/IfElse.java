package aar226_akc55_ayc62_ahl88.newast.stmt;


import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/**
 * Class for If-Else Statements
 */
public class IfElse extends Stmt {
    private Expr guard;
    private Stmt ifState;
    private Stmt elseState;

    /**
     * @param e Expression
     * @param ifS If statement
     * @param elseS Else statement
     * @param l Line
     * @param c Column
     */
    public IfElse(Expr e, Stmt ifS, Stmt elseS, int l, int c)  {
        super(l, c);
        guard = e;
        ifState = ifS;
        elseState = elseS;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("if");
        guard.prettyPrint(p);
        ifState.prettyPrint(p);
        elseState.prettyPrint(p);
        p.endList();
    }
}
