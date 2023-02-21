package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.SExpPrinter;

public abstract class Expr implements Printer {
    Exprs type;

    public Expr() {
    }

    public Exprs getType() {
        return type;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {

    }
}
