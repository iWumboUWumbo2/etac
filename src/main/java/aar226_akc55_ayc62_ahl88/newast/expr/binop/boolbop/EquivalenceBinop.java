package aar226_akc55_ayc62_ahl88.newast.expr.binop.boolbop;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopEnum;

/**
 * Abstract class for all equivalence binary expressions ( == , != )
 * Can be used for both integer and Boolean and array and record inputs
 */
public abstract class EquivalenceBinop extends BoolOutBinop {
    /**
     * @param b binary operation type
     * @param in1 left Expression
     * @param in2 right Expression
     * @param l line number
     * @param c column number
     */
    public EquivalenceBinop (BinopEnum b, Expr in1, Expr in2, int l, int c) {
        super(b, in1, in2, l, c);
    }

    // Type Checking In1,In2 Int or Bool, or arrays
    @Override
    public Type typeCheck(SymbolTable s) throws Error {
        Expr e1 = getLeftExpr();
        Expr e2 = getRightExpr();
        Type t1 = e1.typeCheck(s);
        Type t2 = e2.typeCheck(s);
        String message;

        if (t1.getType() == Type.TypeCheckingType.INT ||
                t1.getType() == Type.TypeCheckingType.BOOL ||
                t1.getType() == Type.TypeCheckingType.UNKNOWN ||
                t1.getType() == Type.TypeCheckingType.RECORD ||
                t1.getType() == Type.TypeCheckingType.NULL) {
            if (!t1.sameType(t2)) {
                throw new SemanticError(e2.getLine(),e2.getColumn(),  "equivalence e2 does not match e1");
            } else {
                nodeType = new Type(Type.TypeCheckingType.BOOL);
                return nodeType;
            }
        } else if (t1.isArray()) {
            if (!t1.sameType(t2)) {

                throw new SemanticError(e2.getLine(),e2.getColumn(), "equivalence e2 does not match e1" );
            } else {
                nodeType = new Type(Type.TypeCheckingType.BOOL);
                return nodeType;
            }
        }else {

            throw new SemanticError(e1.getLine(), e1.getColumn(), "equivalence invalid e1 type");
        }
    }
}
