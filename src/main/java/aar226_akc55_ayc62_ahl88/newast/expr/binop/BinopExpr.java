package aar226_akc55_ayc62_ahl88.newast.expr.binop;

import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/**
 * Abstract class for all Binary Expressions
 */
public abstract class BinopExpr extends Expr {

    Expr e1;
    Expr e2;
    BinopEnum binopType;

    /**
     * @param b binary operation type
     * @param in1 left Expression
     * @param in2 right Expression
     * @param l line number
     * @param c column number
     */
    public BinopExpr(BinopEnum b, Expr in1, Expr in2, int l, int c) {
        super(l,c);
        e1 = in1;
        e2 = in2;
        binopType = b;
    }
    @Override
    public void prettyPrint(CodeWriterSExpPrinter p){
        String out;
        switch (binopType){
            case AND:
                out = "&";
                break;
            case OR:
                out = "|";
                break;
            case EQUALS:
                out = "==";
                break;
            case NOTEQUALS:
                out = "!=";
                break;
            case GEQ:
                out = ">=";
                break;
            case LEQ:
                out = "<=";
                break;
            case LT:
                out = "<";
                break;
            case GT:
                out = ">";
                break;
            case PLUS:
                out = "+";
                break;
            case MINUS:
                out = "-";
                break;
            case DIVIDE:
                out = "/";
                break;
            case TIMES:
                out = "*";
                break;
            case HIGHMULT:
                out = ">>*";
                break;
            case MODULO:
                out = "%";
                break;
            default:
                throw new Error(getLine()+":" + getColumn() + "Error in Binop Case Statement");
        }
        p.startList();
        p.printAtom(out);
        e1.prettyPrint(p);
        e2.prettyPrint(p);
        p.endList();
    }

    // Type checking
}
