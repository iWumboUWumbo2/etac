package aar226_akc55_ayc62_ahl88;

public class Num implements Expr {
    int value;
    public Num(int n) {
        value = n;
    }
    public String toString() {
        return Integer.toString(value);
    }
}
