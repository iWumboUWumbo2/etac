package aar226_akc55_ayc62_ahl88.newast.expr.binop.boolbop;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopEnum;

/**
 * Class for Greater Than Binary Operator
 */
public class GtBinop extends IntegerComparisonBinop {
    /**
     * @param in1 left Expression
     * @param in2 right Expression
     * @param l line number
     * @param c column number
     */
    public GtBinop (Expr in1, Expr in2, int l, int c) {
        super(BinopEnum.GT, in1, in2, l, c);
    }

    @Override
    public Type typeCheck(SymbolTable s){
        return super.typeCheck(s);
    }
}
