package aar226_akc55_ayc62_ahl88.newast.expr.unop.intuop;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.unop.UnopEnum;

/**
 * Class for Integer Negation Unary Operator
 */
public class IntegerNegExpr extends IntUnop {
<<<<<<< HEAD

    protected Type nodeType;

=======
    protected Type nodeType;
>>>>>>> 83cfe19b4a0fe9dd675fee468ea6be1d690af870
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
