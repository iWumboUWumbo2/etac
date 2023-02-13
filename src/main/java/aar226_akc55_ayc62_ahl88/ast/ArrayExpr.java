package aar226_akc55_ayc62_ahl88.ast;

import java.util.ArrayList;

public class ArrayExpr extends Expr {
    private ArrayList<Expr> expr;
    String raw;
   public ArrayExpr(ArrayList<Expr> e) {
        expr = e;
        raw = null;
        this.type = Exprs.ListExpr;
   }


    public ArrayExpr(String s) {
        expr = new ArrayList<Expr>();
        this.raw = s;
        for (char c : s.toCharArray()) {
            expr.add(new IntLiteral(Character.toString(c)));
        }
        this.type = Exprs.ListExpr;
    }
}