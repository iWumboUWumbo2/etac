package aar226_akc55_ayc62_ahl88.newast.expr.binop.boolbop;

import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopEnum;

public class LeqBinop extends EquivalenceBinop {
    public LeqBinop (Expr in1, Expr in2, int l, int c) {
        super(BinopEnum.LEQ, in1, in2, l, c);
    }
}
