package aar226_akc55_ayc62_ahl88.Errors;

/**
 * Class for lexing errors.
 */
public class LexicalError extends EtaError {

    /**
     * @param line
     * @param col
     * @param m Message
     */
    public LexicalError(int line, int col, String m) {
        super(EtaErrorTypes.LEXICAL, line, col, m);
    }

}
