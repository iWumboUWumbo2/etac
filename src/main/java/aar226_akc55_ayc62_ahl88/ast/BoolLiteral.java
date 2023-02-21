package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.SExpPrinter;

public class BoolLiteral extends Expr implements Value {
    private boolean b;
    private ValueType vt;

    public BoolLiteral(boolean b) {
        this.b = b;
        this.type = Exprs.BoolLit;
        vt = ValueType.BOOLLITERAL;
    }

    public ValueType getValueType() {
        return vt;
    }

    public String toString() {
        return Boolean.toString(b);
    }

    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.printAtom(toString());
    }
}
