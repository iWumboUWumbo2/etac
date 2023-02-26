package aar226_akc55_ayc62_ahl88.Errors;

public class LexicalError extends Error{

    public LexicalError(int line, int col, String m) {
        super(String.format("%d:%d error:%s", line, col, m));
    }

}
