package aar226_akc55_ayc62_ahl88.ast;

public class EQ extends Binary{
    public EQ(Expr e1, Expr e2) {
        super("==", e1, e2);
    }
}
