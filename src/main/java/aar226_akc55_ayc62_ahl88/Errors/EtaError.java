package aar226_akc55_ayc62_ahl88.Errors;

/**
 * Class for Eta errors.
 */
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

    /**
     * @param filename Filename
     */
    public void printError(String filename) {
        System.out.printf("%s error beginning at %s:%d:%d: %s\n",
                getErrorType(), filename, getLine(), getCol(), getErrorString());
    }

    /**
     * @param type Error type
     * @param line Line number
     * @param col Column number
     * @param m Message
     */
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
