package aar226_akc55_ayc62_ahl88.newast.expr.unop.intuop;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.unop.UnopEnum;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRExpr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.visitors.ContainsBreakVisitor;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

/**
 * Class for Integer Negation Unary Operator
 */
public class IntegerNegExpr extends IntUnop {
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
    @Override
    public Boolean accept(ContainsBreakVisitor v) {
        return v.visit(this);
    }
    @Override
    public IRExpr accept(IRVisitor visitor) {
        return visitor.visit(this);
    }
}
