package aar226_akc55_ayc62_ahl88.newast.expr.binop;

import aar226_akc55_ayc62_ahl88.Errors.SyntaxError;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRBinOp;

/**
 * Abstract class for all Binary Expressions
 */
public abstract class BinopExpr extends Expr {

    Expr e1;
    Expr e2;
    BinopEnum binopType;

    public BinopEnum getBinopType() {
        return binopType;
    }

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
                out = "*>>";
                break;
            case MODULO:
                out = "%";
                break;
            case PERIOD:
                out = ".";
                break;
            default:
                throw new SyntaxError(getLine(),getColumn(),"Error in Binop Case Statement");
        }
        p.startList();
        p.printAtom(out);
        e1.prettyPrint(p);
        e2.prettyPrint(p);
        p.endList();
    }

    public Expr getLeftExpr() {
        return e1;
    }

    public Expr getRightExpr() {
        return e2;
    }

    public IRBinOp.OpType getOpType(){
        switch (binopType) {
            case DIVIDE: return IRBinOp.OpType.DIV;
            case HIGHMULT: return IRBinOp.OpType.HMUL;
            case MINUS: return IRBinOp.OpType.SUB;
            case MODULO: return IRBinOp.OpType.MOD;
            case TIMES: return IRBinOp.OpType.MUL;
            case AND: return IRBinOp.OpType.AND;
            case OR: return IRBinOp.OpType.OR;
            case EQUALS: return IRBinOp.OpType.EQ;
            case NOTEQUALS: return IRBinOp.OpType.NEQ;
            case GEQ: return IRBinOp.OpType.GEQ;
            case LEQ: return IRBinOp.OpType.LEQ;
            case LT: return IRBinOp.OpType.LT;
            case GT: return IRBinOp.OpType.GT;
            case PLUS: return IRBinOp.OpType.ADD;
            default:
                throw new Error("Invalid binary operation");
        }
    }
    // Type checking
}
