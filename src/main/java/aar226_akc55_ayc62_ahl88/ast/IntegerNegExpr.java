package aar226_akc55_ayc62_ahl88.ast;

class IntegerNegExpr extends UnaryExpr {
    public IntegerNegExpr(Expr e) {
        super("-", e);
    }
}
