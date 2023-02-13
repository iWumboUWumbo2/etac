package aar226_akc55_ayc62_ahl88.ast;

public class Not_EqualExpr extends BinaryExpr {
    public Not_EqualExpr(Expr e1, Expr e2) {
        super("!=", e1, e2);
    }
}
