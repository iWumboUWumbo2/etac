package aar226_akc55_ayc62_ahl88;

public class Unary implements Expr{
    String op;
    Expr e1;

    public Unary(String op, Expr e1) {
        this.op = op;
        this.e1 = e1;
    }

    public String toString() {
        return "(" + op + " " + e1 + ")";
    }
}
