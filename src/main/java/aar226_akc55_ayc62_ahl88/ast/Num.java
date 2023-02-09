package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.ast.Expr;

public class Num implements Expr {
    int value;
    public Num(int n) {
        value = n;
    }
    public String toString() {
        return Integer.toString(value);
    }
}
