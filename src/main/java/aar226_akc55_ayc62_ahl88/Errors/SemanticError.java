package aar226_akc55_ayc62_ahl88.Errors;

public class SemanticError extends EtaError {
    public SemanticError(int line, int col, String m) {
        super(EtaErrorTypes.SEMANTIC, line, col, m);
    }
}
