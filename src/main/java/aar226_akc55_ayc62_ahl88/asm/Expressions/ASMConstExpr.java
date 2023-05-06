package aar226_akc55_ayc62_ahl88.asm.Expressions;

public class ASMConstExpr extends ASMExpr {

    private long value;
    public ASMConstExpr(long val) {
        value = val;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ASMConstExpr that = (ASMConstExpr) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return (int) (value ^ (value >>> 32));
    }
}
