package aar226_akc55_ayc62_ahl88.newast.expr.unop.intuop;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.unop.UnopEnum;
import aar226_akc55_ayc62_ahl88.newast.expr.unop.UnopExpr;

/**
 * Abstract class for all Integer unary expressions
 */
public abstract class IntUnop extends UnopExpr {
    /**
     * @param b   Integer Enum for unary Expressions
     * @param in  input Expressions 1
     * @param l   Line Number
     * @param c   Column Number
     */
    public IntUnop(UnopEnum b, Expr in, int l, int c) {
        super(b, in, l, c);
    }

    @Override
    public Type typeCheck(SymbolTable s){
        Expr e1 = getE();
        Type t1 = e1.typeCheck(s);

        if (!(t1.getType() == Type.TypeCheckingType.INT ||
                t1.getType() == Type.TypeCheckingType.UNKNOWN)) {

            throw new SemanticError(e1.getLine(), e1.getColumn(), "statements block must be of type int at");
        }

        return new Type(Type.TypeCheckingType.INT);
    }
}
