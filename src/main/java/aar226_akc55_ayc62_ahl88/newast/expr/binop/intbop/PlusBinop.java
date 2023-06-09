package aar226_akc55_ayc62_ahl88.newast.expr.binop.intbop;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopEnum;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopExpr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRExpr;
import aar226_akc55_ayc62_ahl88.visitors.ContainsBreakVisitor;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

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

    /**
     * @param t1 Left type
     * @param t2 Right type
     * @return True if same type else false
     */
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

    /**
     * @param s Symbol Table
     * @return Type
     */
    @Override
    public Type typeCheck(SymbolTable s){
        Expr e1 = getLeftExpr();
        Expr e2 = getRightExpr();
        Type t1 = e1.typeCheck(s);
        Type t2 = e2.typeCheck(s);

        if (!t1.sameType(t2)) {
            throw new SemanticError(getLine(), getColumn(), " plus e2 does not match e1");
        }
        if (t1.getType() == Type.TypeCheckingType.BOOL){
            throw new SemanticError(getLine(), getColumn(), " plus doesn't work on bool");
        }

        Type greaterType = t1.greaterType(t2);
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
    @Override
    public Boolean accept(ContainsBreakVisitor v) {
        return v.visit(this);
    }
    @Override
    public IRExpr accept(IRVisitor visitor) {
        return visitor.visit(this);
    }
}
