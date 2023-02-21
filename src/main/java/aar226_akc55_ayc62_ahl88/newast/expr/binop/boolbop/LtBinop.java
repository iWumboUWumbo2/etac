package aar226_akc55_ayc62_ahl88.newast.expr.binop.boolbop;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopEnum;

/**
 * Class for Less Than Binary Operator
 */
public class LtBinop extends IntegerComparisonBinop {
    /**
     * @param in1 left Expression
     * @param in2 right Expression
     * @param l line number
     * @param c column number
     */
    public LtBinop (Expr in1, Expr in2, int l, int c) {
        super(BinopEnum.LT, in1, in2, l, c);
    }

    public Type typeCheck(SymbolTable s){
        return super.typeCheck(s);
    }
}
