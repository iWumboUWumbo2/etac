package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.SExpPrinter;

public class IntLiteral extends Expr implements Value {
    private long i;
    private String raw = null;

    private ValueType vt;
    public IntLiteral(long i) {
        this.i = i;
        this.type = Exprs.IntLit;
    }

    public IntLiteral(String c) {
        this.i = Character.getNumericValue(c.charAt(0));
        this.type = Exprs.IntLit;
        this.raw = c;
        vt = ValueType.INTLITERAL;
    }

    public ValueType getValueType(){
        return vt;
    }
    public String toString() {
        return (raw == null) ? Long.toString(i) : raw;
    }

    public void prettyPrint(SExpPrinter p) {
        if (raw == null) {
            p.printAtom(this.toString());
        }
        else {
            p.printAtom("'");
            p.printAtom(this.toString());
            p.printAtom("'");
        }
    }
}
