package aar226_akc55_ayc62_ahl88.newast.expr.binop.intbop;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopEnum;

/**
 * Class for HighMult Binary Operator
 */
public class HighMultBinop extends IntOutBinop {
    /**
     * @param in1 left Expression
     * @param in2 right Expression
     * @param l line number
     * @param c column number
     */
    public HighMultBinop(Expr in1, Expr in2, int l, int c) {
        super(BinopEnum.HIGHMULT, in1, in2, l, c);
    }

    @Override
    public Type typeCheck(SymbolTable s){
        Expr e1 = getLeftExpr();
        Expr e2 = getRightExpr();
        Type t1 = getLeftExpr().typeCheck(s);
        Type t2 = getRightExpr().typeCheck(s);

        if ((t1.getType() != Type.TypeCheckingType.INT)) {
            String message = Integer.toString(e1.getLine())
                    + ":" + Integer.toString(e1.getColumn())
                    + "  TypeError: statements block must be of type int at ";
            throw new Error(message);
        }

        if ((t2.getType() != Type.TypeCheckingType.INT)) {
            String message = Integer.toString(e2.getLine())
                    + ":" + Integer.toString(e2.getColumn())
                    + "  TypeError: statements block must be of type int at ";
            throw new Error(message);
        }

        return new Type(Type.TypeCheckingType.INT);
    }
}
