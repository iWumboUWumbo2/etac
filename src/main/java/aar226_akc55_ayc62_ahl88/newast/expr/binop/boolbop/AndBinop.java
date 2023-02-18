package aar226_akc55_ayc62_ahl88.newast.expr.binop.boolbop;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopEnum;

public class AndBinop extends LogicalBinop {
    public AndBinop (Expr e1, Expr e2, int l, int c) {
        super(BinopEnum.AND, in1, in2, l, c);
    }
}
