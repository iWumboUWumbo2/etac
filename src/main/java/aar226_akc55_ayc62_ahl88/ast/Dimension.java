package aar226_akc55_ayc62_ahl88.ast;

public class Dimension {
    private long dim;

    public Dimension(long d) {
        dim = d;
    }

    public void increment() {
        dim++;
    }

    public long getDim() {
        return dim;
    }

    public String toString() {
        String s = "";
        for (long i = 0; i < dim; i++) {
            s += "[]";
        }
        return s;
    }
}
