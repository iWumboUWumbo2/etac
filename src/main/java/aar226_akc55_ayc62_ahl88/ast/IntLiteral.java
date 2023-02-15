package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.SExpPrinter;

public class IntLiteral extends Expr implements Value {
    private long i;
    private String raw = null;

    private ValueType vt;
    public IntLiteral(long i,boolean pos,int left, int right) {
        if (i == Long.MIN_VALUE && pos){
            throw new Error(left + ":" + right + " error: INTEGER OUT OF RANGE PARSING");
        }
        this.i = i;
        if (pos){
            this.i = -i;
        }
        this.type = Exprs.IntLit;
    }

    public IntLiteral(String c) {
        this.i = Character.getNumericValue(c.charAt(0));
        this.type = Exprs.IntLit;
        this.raw = c;
        vt = ValueType.INTLITERAL;
    }
    public long getLong(){
        return i;
    }
    public ValueType getValueType(){
        return vt;
    }
    public String toString() {
//        System.out.println("IM IN INTEGER LIT to string");
        return (raw == null) ? Long.toString(i) : raw;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
//        System.out.println("CALLING PRETTY PRINT");
        if (raw == null) {
            p.printAtom(toString());
        }
        else {
            p.printAtom("'"+ toString()+ "'");
        }
//        System.out.println("FINISHED CALLING");
    }
}
