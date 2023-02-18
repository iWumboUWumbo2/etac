package aar226_akc55_ayc62_ahl88.newast.expr;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/**
 * Class for Identifer
 */
public  class Id extends Expr {
    private String identifer;

    /**
     * @param id Identifer Name
     * @param l  line Number
     * @param r  Column Number
     */
    public Id(String id, int l, int r) {
        super(l,r);
        identifer = id;
    }
    public String toString() {
        return identifer;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.printAtom(identifer);
    }
}

