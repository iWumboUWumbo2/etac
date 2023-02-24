package aar226_akc55_ayc62_ahl88.newast;

public class SemanticException extends Error {
    public SemanticException(int line, int column, String message) {
        super("" + line + ":" + column + " semantic error: " + message);
    }
}
