package aar226_akc55_ayc62_ahl88.newast.expr.binop.boolbop;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopEnum;

/**
 * Abstract class for all equivalence binary expressions ( == , != )
 * Can be used for both integer and Boolean and array inputs
 */
public abstract class EquivalenceBinop extends BoolOutBinop {
    /**
     * @param b binary operation type
     * @param in1 left Expression
     * @param in2 right Expression
     * @param l line number
     * @param c column number
     */
    public EquivalenceBinop (BinopEnum b, Expr in1, Expr in2, int l, int c) {
        super(b, in1, in2, l, c);
    }

    // Type Checking In1,In2 Int or Bool, or arrays

    @Override
    public Type typeCheck(SymbolTable s) throws Error {
        Expr e1 = getLeftExpr();
        Expr e2 = getRightExpr();
        Type t1 = e1.typeCheck(s);
        Type t2 = e2.typeCheck(s);
        String message;

        if (t1.getType() == Type.TypeCheckingType.INT) {
            if (t2.getType() != Type.TypeCheckingType.INT) {
                message = Integer.toString(e2.getLine())
                        + ":" + Integer.toString(e2.getColumn())
                        + "  TypeError: plus e2 does not match e1 ";
            } else {
                return new Type(Type.TypeCheckingType.BOOL);
            }
        } else if (t1.getType() == Type.TypeCheckingType.INTARRAY
            || t1.getType() == Type.TypeCheckingType.BOOLARRAY) {
            if (t2.getType() != t1.getType() ||
                    !t1.dimensions.equalsDimension(t2.dimensions)) {
                message = Integer.toString(e2.getLine())
                        + ":" + Integer.toString(e2.getColumn())
                        + "  TypeError: plus e2 does not match e1 ";
            } else {
                return new Type(Type.TypeCheckingType.BOOL);
            }
        }
        message = Integer.toString(e1.getLine())
                + ":" + Integer.toString(e1.getColumn())
                + "  TypeError: plus invalid e1 type ";
        throw new Error(message);
    }
}
