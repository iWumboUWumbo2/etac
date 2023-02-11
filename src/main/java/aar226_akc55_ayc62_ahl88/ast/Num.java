package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.ast.Expr;

public class Num implements Expr {
    long value;
    public Num(long n) {
        value = n;
    }
    public String toString() {
        return Long.toString(value);
    }
}
