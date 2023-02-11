package aar226_akc55_ayc62_ahl88.ast;

public class Modulo extends Binary {
    public Modulo(Expr e1, Expr e2) {
        super("%", e1, e2);
    }
}
