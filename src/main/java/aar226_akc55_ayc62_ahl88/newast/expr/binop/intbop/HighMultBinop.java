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

        nodeType = super.typeCheck(s);
        return nodeType;
    }
}
