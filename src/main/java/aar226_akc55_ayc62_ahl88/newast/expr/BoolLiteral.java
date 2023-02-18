package aar226_akc55_ayc62_ahl88.newast.expr;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class BoolLiteral extends Expr {
    private boolean boolVal;

    public BoolLiteral(boolean inputBool,int l, int c) {
        super(l,c);
        boolVal = inputBool;
    }

    public String toString() {
        return Boolean.toString(boolVal);
    }

    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.printAtom(toString());
    }
}
