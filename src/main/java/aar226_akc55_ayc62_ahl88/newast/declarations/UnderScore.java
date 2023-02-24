package aar226_akc55_ayc62_ahl88.newast.declarations;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
/**
 * Class for UnderScore Declaration
 */
public class UnderScore extends Decl{
    /**
     * Constructor for UnderScore Declaration
     * @param l line number
     * @param c column number
     */
    public UnderScore(int l, int c) {
        super(l, c);
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.printAtom("_");
    }
}