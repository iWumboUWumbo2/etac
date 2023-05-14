package aar226_akc55_ayc62_ahl88.Errors;

/**
 * Class for type checking errors.
 */
public class SemanticError extends EtaError {

    /**
     * @param line Line number
     * @param col Column number
     * @param m Message
     */
    public SemanticError(int line, int col, String m) {
        super(EtaErrorTypes.SEMANTIC, line, col, m);
    }
}
