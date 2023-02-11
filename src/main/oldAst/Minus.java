package aar226_akc55_ayc62_ahl88.ast;

public class Minus extends Binary { //binary + unary???

    public Minus(Expr e1, Expr e2) {
        super("-", e1, e2);
    }

    public Minus(Expr e1) {
        super("-", new Num(0L), e1);
    }
}
