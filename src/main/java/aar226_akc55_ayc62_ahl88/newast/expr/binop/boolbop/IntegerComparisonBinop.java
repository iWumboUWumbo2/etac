package aar226_akc55_ayc62_ahl88.newast.expr.binop.boolbop;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopEnum;


/**
 * Abstract class for integer all equivalence binary expressions (>, <, >=, <=)
 * Integer inputs only
 */
public abstract class IntegerComparisonBinop extends BoolOutBinop{
    /**
     * @param b   binary operation type
     * @param in1 left Expression
     * @param in2 right Expression
     * @param l   line number
     * @param c   column number
     */
    public IntegerComparisonBinop(BinopEnum b, Expr in1, Expr in2, int l, int c) {
        super(b, in1, in2, l, c);
    }

    // typecheck ints only -> bool
    @Override
    public Type typeCheck(SymbolTable s){
        Expr e1 = getLeftExpr();
        Expr e2 = getRightExpr();
        Type t1 = e1.typeCheck(s);
        Type t2 = e2.typeCheck(s);

        if (!(t1.getType() == Type.TypeCheckingType.INT ||
                t1.getType() == Type.TypeCheckingType.UNKNOWN)) {

            throw new SemanticError(e1.getLine(), e1.getColumn(), "statements block must be of type int at");
        }

        if (!(t2.getType() == Type.TypeCheckingType.INT ||
                t2.getType() == Type.TypeCheckingType.UNKNOWN)) {
            throw new SemanticError(e2.getLine(),e2.getColumn(),"statements block must be of type int at"  );
        }

        return new Type(Type.TypeCheckingType.BOOL);
    }
}
