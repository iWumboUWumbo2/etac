package aar226_akc55_ayc62_ahl88.ast;

public  class ModuloExpr extends BinaryExpr {
    public ModuloExpr(Expr e1, Expr e2) {
        super("%", e1, e2);
    }
}
