package aar226_akc55_ayc62_ahl88.ast;

public class IntLiteral implements Expr {
    private int i;
    private String raw;

    public IntLiteral(int i) {
        this.i = i;
    }

    public IntLiteral(String s) {

    }

    public IntLiteral(char c) {
        this.i = Character.getNumericValue(c);
    }
}
