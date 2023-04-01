package aar226_akc55_ayc62_ahl88.asm.Expressions;

public class ASMConstExpr extends ASMExpr {

    private long value;
    public ASMConstExpr(long val) {
        value = val;
    }

    public long getValue() {
        return value;
    }
}
