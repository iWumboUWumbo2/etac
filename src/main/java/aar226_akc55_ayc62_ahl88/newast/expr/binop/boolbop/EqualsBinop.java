package aar226_akc55_ayc62_ahl88.newast.expr.binop.boolbop;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopEnum;

/**
 * Class for Equals Binary Operator
 */
public class EqualsBinop extends EquivalenceBinop {
    /**
     * @param in1 left Expression
     * @param in2 right Expression
     * @param l line number
     * @param c column number
     */
    public EqualsBinop (Expr in1, Expr in2, int l, int c) {
        super(BinopEnum.EQUALS, in1, in2, l, c);
    }

    @Override
    public Type typeCheck(SymbolTable s) throws Error {
        Expr e1 = getLeftExpr();
        Expr e2 = getRightExpr();
        Type t1 = getLeftExpr().typeCheck(s);
        Type t2 = getRightExpr().typeCheck(s);
        String message;

        if (t1.getType() == Type.TypeCheckingType.INT) {
            if (t2.getType() != Type.TypeCheckingType.INT) {
                message = Integer.toString(e2.getLine())
                        + ":" + Integer.toString(e2.getColumn())
                        + "  TypeError: plus e2 does not match e1 ";
            } else {
                return new Type(Type.TypeCheckingType.INT);
            }
        } else if (t1.getType() == Type.TypeCheckingType.INTARRAY) {
            if (t2.getType() != Type.TypeCheckingType.INTARRAY) {
                message = Integer.toString(e2.getLine())
                        + ":" + Integer.toString(e2.getColumn())
                        + "  TypeError: plus e2 does not match e1 ";
            } else {
                return new Type(Type.TypeCheckingType.INTARRAY);
            }
        } else if (t1.getType() == Type.TypeCheckingType.BOOLARRAY) {
            if (t2.getType() != Type.TypeCheckingType.BOOLARRAY) {
                message = Integer.toString(e2.getLine())
                        + ":" + Integer.toString(e2.getColumn())
                        + "  TypeError: plus e2 does not match e1 ";
            } else {
                return new Type(Type.TypeCheckingType.BOOLARRAY);
            }
        }
        message = Integer.toString(e1.getLine())
                + ":" + Integer.toString(e1.getColumn())
                + "  TypeError: plus invalid e1 type ";
        throw new Error(message);
    }
}
