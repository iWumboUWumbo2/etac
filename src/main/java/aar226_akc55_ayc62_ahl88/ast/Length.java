package aar226_akc55_ayc62_ahl88.ast;

public class Length extends Expr {

    Expr arg;
    public Length(Id id) {
        arg = id;
    }

    public Length(ArrayExpr e) {
        arg = e;
    }

    public Length(ArrayValueLiteral e) {
        arg = e;
    }
}
