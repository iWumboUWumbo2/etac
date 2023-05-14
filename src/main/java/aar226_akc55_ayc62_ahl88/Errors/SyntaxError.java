package aar226_akc55_ayc62_ahl88.Errors;

/**
 * Class for syntax errors.
 */
public class SyntaxError extends EtaError {

    /**
     * @param line Line number
     * @param col Column number
     * @param m Message
     */
    public SyntaxError(int line, int col, String m) {
        super(EtaErrorTypes.SYNTAX, line, col, m);
    }
}
