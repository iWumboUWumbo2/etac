package aar226_akc55_ayc62_ahl88.newast.expr.unop.intuop;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.unop.UnopEnum;

/**
 * Class for Integer Negation Unary Operator
 */
public class IntegerNegExpr extends IntUnop {
    protected Type nodeType;
    /**
     * @param in Expression input
     * @param l line number
     * @param c column number
     */
    public IntegerNegExpr(Expr in, int l, int c) {
        super(UnopEnum.INT_NEG, in, l, c);
    }

    @Override
    public Type typeCheck(SymbolTable s){
        nodeType = super.typeCheck(s);
        return nodeType;
    }
}
