package aar226_akc55_ayc62_ahl88.newast.expr.binop.boolbop;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopEnum;

/**
 * Class for And Binary Operator
 */
public class AndBinop extends LogicalBinop {
    protected Type nodeType;
    /**
     * @param in1 left Expression
     * @param in2 right Expression
     * @param l line number
     * @param c row number
     */
    public AndBinop (Expr in1, Expr in2, int l, int c) {
        super(BinopEnum.AND, in1, in2, l, c);
    }
    @Override
    public Type typeCheck(SymbolTable s){
        nodeType = super.typeCheck(s);
        return nodeType;
    }
}
