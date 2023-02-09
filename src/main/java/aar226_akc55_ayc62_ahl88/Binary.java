package aar226_akc55_ayc62_ahl88;

class Binary implements Expr {
    String op;
    Expr e1, e2;

    public Binary(String op, Expr e1, Expr e2) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
    }

    public String toString() {
        return "(" + op + " " + e1 + " " + e2 + ")";
    }
}
