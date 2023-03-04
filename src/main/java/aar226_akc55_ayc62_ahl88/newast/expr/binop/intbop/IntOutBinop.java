package aar226_akc55_ayc62_ahl88.newast.expr.binop.intbop;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopEnum;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopExpr;

/**
 * Abstract class for all binary expressions
 */
public abstract class IntOutBinop extends BinopExpr {
    /**
     * @param b binary operation type
     * @param in1 first expression input
     * @param in2 second expressioin input
     * @param l line number
     * @param c column number
     */
    public IntOutBinop(BinopEnum b, Expr in1, Expr in2, int l, int c) {
        super(b, in1, in2, l, c);
    }
    // Type Checking is In1, In2 both must be Int and output is Int

    @Override
    public Type typeCheck(SymbolTable s){
        Expr e1 = getLeftExpr();
        Expr e2 = getRightExpr();
        Type t1 = e1.typeCheck(s);
        Type t2 = e2.typeCheck(s);

        if (!(t1.getType() == Type.TypeCheckingType.INT ||
                t1.getType() == Type.TypeCheckingType.UNKNOWN)) {
            throw new SemanticError(e1.getLine(), e1.getColumn(), "statements block must be of type int at");
        }

        if (!(t2.getType() == Type.TypeCheckingType.INT ||
                t2.getType() == Type.TypeCheckingType.UNKNOWN)) {

            throw new SemanticError(e2.getLine(), e2.getColumn(), "statements block must be of type int at");
        }
        return new Type(Type.TypeCheckingType.INT);
    }
}
