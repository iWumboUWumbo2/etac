package aar226_akc55_ayc62_ahl88.newast.expr.unop.booluop;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopEnum;
import aar226_akc55_ayc62_ahl88.newast.expr.unop.UnopEnum;

/**
 * Class for Boolean Negation Unary Operator
 */
public class NotUnop extends BooleanUnop {
    /**
     * @param in input Expression
     * @param l line number
     * @param c column number
     */
    public NotUnop(Expr in, int l, int c) {
        super(UnopEnum.NOT, in, l, c);
    }

    @Override
    public Type typeCheck(SymbolTable s){
        nodeType = super.typeCheck(s);
        return nodeType;
    }
}
