package aar226_akc55_ayc62_ahl88.Errors;

public class LexicalError extends EtaError {

    public LexicalError(int line, int col, String m) {
        super(EtaErrorTypes.LEXICAL, line, col, m);
    }

}
