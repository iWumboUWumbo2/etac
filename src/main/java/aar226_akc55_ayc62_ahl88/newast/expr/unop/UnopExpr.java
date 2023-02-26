package aar226_akc55_ayc62_ahl88.newast.expr.unop;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/**
 * Abstract class for all Unary Expressions
 */
public abstract class UnopExpr extends Expr {
    Expr e;
    UnopEnum unopType;

    /**
     * @param u  Unary Enum
     * @param in Expression
     * @param l line number
     * @param c column number
     */
    public UnopExpr(UnopEnum u, Expr in, int l, int c) {
        super(l,c);
        e = in;
        unopType = u;
    }

    public Expr getE() {
        return e;
    }

    public void prettyPrint(CodeWriterSExpPrinter p) {

        String out;

        switch (unopType){
            case NOT:
                out = "!";
                break;
            case INT_NEG:
                out = "-";
                break;
            default:
                throw new SemanticError(getLine(), getColumn(), "Error in Unop Case Statement");
        }
        p.startList();
        p.printAtom(out);
        e.prettyPrint(p);
        p.endList();
    }
}
