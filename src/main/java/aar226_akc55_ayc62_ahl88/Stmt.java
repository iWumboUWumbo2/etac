package aar226_akc55_ayc62_ahl88;

public class Stmt implements Node{
    String op;
    Node e[];
    Expr condition;

    public Stmt(String op, Expr condition, Node[] e) {
        this.op = op;
        this.e = e;
        this.condition = condition;
    }

    public String toString(){
        String x = "(" + op + "(" + condition.toString() + ")";
        for (Node y : e) {
            x += "(" + y  + ")";
        }
        x += ")";
        return x;
    }
}
