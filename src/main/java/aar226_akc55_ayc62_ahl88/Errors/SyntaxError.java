package aar226_akc55_ayc62_ahl88.Errors;

public class SyntaxError extends EtaError {
    public SyntaxError(int line, int col, String m) {
        super(EtaErrorTypes.SYNTAX, line, col, m);
    }
}
