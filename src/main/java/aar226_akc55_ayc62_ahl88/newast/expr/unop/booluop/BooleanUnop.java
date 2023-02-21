package aar226_akc55_ayc62_ahl88.newast.expr.unop.booluop;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.unop.*;

/**
 * Abstract class for all boolean unary expressions
 */
public abstract class BooleanUnop extends UnopExpr {
    /**
     * @param b   Boolean Unop
     * @param in  Expressions Input 1
     * @param l   Line Number
     * @param c   Column Number
     */
    public BooleanUnop(UnopEnum b, Expr in, int l, int c) {
        super(b, in, l, c);
    }

    @Override
    public Type typeCheck(SymbolTable s){
        Expr e1 = getE();
        Type t1 = e1.typeCheck(s);

        if (t1.getType() != Type.TypeCheckingType.BOOL) {
            String message = Integer.toString(e1.getLine())
                    + ":" + Integer.toString(e1.getColumn())
                    + "  TypeError: statements block must be of type int at ";
            throw new Error(message);
        }

        return new Type(Type.TypeCheckingType.BOOL);
    }
}
