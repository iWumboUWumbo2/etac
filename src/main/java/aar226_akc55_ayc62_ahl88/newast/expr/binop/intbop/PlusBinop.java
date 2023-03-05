package aar226_akc55_ayc62_ahl88.newast.expr.binop.intbop;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopEnum;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopExpr;

/**
 * Class for Plus Binary Operator
 */
public class PlusBinop extends BinopExpr {
    /**
     * @param in1 left Expression
     * @param in2 right Expression
     * @param l line number
     * @param c column number
     */
    public PlusBinop(Expr in1, Expr in2, int l, int c) {
        super(BinopEnum.PLUS, in1, in2, l, c);
    }


    private boolean typeListEquals(Type t1, Type t2) {
        if (t1.getType() == Type.TypeCheckingType.INT ||
                t1.getType() == Type.TypeCheckingType.BOOL) {
            return t2.getType() == t1.getType();
        } else if (t1.getType() == Type.TypeCheckingType.INTARRAY ||
                t1.getType() == Type.TypeCheckingType.BOOLARRAY) {
            return t2.getType() == t1.getType() &&
                    t1.dimensions.equalsDimension(t2.dimensions);
        } else {
            return false;
        }
    }


    // can add Arrays too
    @Override
    public Type typeCheck(SymbolTable s){
        Expr e1 = getLeftExpr();
        Expr e2 = getRightExpr();
        Type t1 = e1.typeCheck(s);
        Type t2 = e2.typeCheck(s);

        if (!t1.sameType(t2)) {
            throw new SemanticError(getLine(), getColumn(), "plus e2 does not match e1");
        }

        Type greaterType = t1.greaterType(t2);
//        System.out.println(greaterType.dimensions.getDim());
        if (greaterType.getType() == Type.TypeCheckingType.INT) {
            nodeType = new Type(Type.TypeCheckingType.INT);
            return nodeType;
        } else if (greaterType.isArray()) {
            nodeType = new Type(greaterType.getType(), greaterType.dimensions);
            return nodeType;
        } else {
            nodeType = new Type(Type.TypeCheckingType.UNKNOWN);
            return nodeType;
        }
    }
}
