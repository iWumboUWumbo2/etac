package aar226_akc55_ayc62_ahl88.ast;

public class AndExpr extends BinaryExpr {
    public AndExpr(Expr e1, Expr e2) {
        super("&", e1, e2);
    }
}
