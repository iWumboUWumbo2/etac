package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
import java.util.Collections;

public class Dimension implements Printer{
    private long dim;

    public boolean allEmpty;
    public boolean foundEmpty;
    public ArrayList<Expr> indices;
    public Dimension(long d) {
        dim = d;
        allEmpty = true;
        foundEmpty = false;
        indices = new ArrayList<>();
    }

    public void increment() {
        dim++;
    }

    public long getDim() {
        return dim;
    }

    public String toString() {
        String s = "";
        ArrayList<Expr> rev = new ArrayList<>(indices);
        Collections.reverse(rev);
        for (int i = 0; i< rev.size();i++) {
            if (rev.get(i) != null){
                s += "["+ rev.get(i)+"]";
            }else{
                s += "[]";
            }
        }
        return s;
    }
    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {

        ArrayList<Expr> rev = new ArrayList<>(indices);
        Collections.reverse(rev);
        p.startList();

        for (Expr expr : rev) {
            p.printAtom("[");
            expr.prettyPrint(p);
            p.printAtom("]");
        }
        p.endList();
    }
}
