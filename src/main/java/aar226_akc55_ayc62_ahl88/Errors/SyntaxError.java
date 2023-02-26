package aar226_akc55_ayc62_ahl88.Errors;

public class SyntaxError extends Error{
    public SyntaxError(int line, int col, String m) {
        super(String.format("%d:%d error:%s", line, col, m));
    }
}
