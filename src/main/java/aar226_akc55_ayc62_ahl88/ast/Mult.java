package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.ast.Binary;
import aar226_akc55_ayc62_ahl88.ast.Expr;

public class Mult extends Binary {
    public Mult(Expr e1, Expr e2) {
        super("*", e1, e2);
    }
}
