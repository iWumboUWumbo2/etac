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
    public Type typeChecker(SymbolTable s){
        Type t1 = this.getLeftExpr().typeChecker(s);
        Type t2 = this.getLeftExpr().typeChecker(s);
        if (t1.getTct() == Type.TypeCheckingType.Int && t2.getTct() == Type.TypeCheckingType.Int) {
            return new Type(Type.TypeCheckingType.Int, getLine(), getColumn());
        }
        throw new Error("One or both are not of type int.");
    }
}
