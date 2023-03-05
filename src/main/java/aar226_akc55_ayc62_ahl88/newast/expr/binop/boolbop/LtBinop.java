package aar226_akc55_ayc62_ahl88.newast.expr.binop.boolbop;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopEnum;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

/**
 * Class for Less Than Binary Operator
 */
public class LtBinop extends EquivalenceBinop {
    /**
     * @param in1 left Expression
     * @param in2 right Expression
     * @param l line number
     * @param c column number
     */
    public LtBinop (Expr in1, Expr in2, int l, int c) {
        super(BinopEnum.LT, in1, in2, l, c);
    }
    @Override
    public Type typeCheck(SymbolTable s){
        nodeType = super.typeCheck(s);
        return nodeType;
    }

    @Override
    public IRNode accept(IRVisitor visitor) {
        return visitor.visit(this);
    }
}
