package aar226_akc55_ayc62_ahl88.newast.stmt;

import aar226_akc55_ayc62_ahl88.ast.Expr;
import aar226_akc55_ayc62_ahl88.ast.Stmt;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/**
 * While Class for AST node
 */
public class While extends Stmt {
    private Expr expr;
    private Stmt stmt;

    /**
     * @param expr Expression
     * @param s Statement
     * @param l line number
     * @param c colunn number
     */
    public While(Expr expr, Stmt s, int l, int c) {
        super(l,c);
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
