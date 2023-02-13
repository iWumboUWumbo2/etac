package aar226_akc55_ayc62_ahl88.ast;

public class NotExpr extends UnaryExpr {
    public NotExpr(Expr e) {
        super("!", e);
    }
}
