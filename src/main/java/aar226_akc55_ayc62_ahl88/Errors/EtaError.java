package aar226_akc55_ayc62_ahl88.Errors;

public class EtaError extends Error {
    private int line;
    private int col;
    private String m;
    private String errorType;

    public int getLine() {
        return line;
    }

    public int getCol() {
        return col;
    }

    public String getErrorString() {
        return m;
    }

    public String getErrorType() {
        return errorType;
    }

    public EtaError(EtaErrorTypes type, int line, int col, String m) {
        super(String.format("%d:%d error:%s", line, col, m));

        switch (type) {
            case SYNTAX -> this.errorType = "Syntax";
            case LEXICAL -> this.errorType = "Lexical";
            case SEMANTIC -> this.errorType = "Semantic";
        }

        this.line = line;
        this.col = col;
        this.m = m;
    }
}
