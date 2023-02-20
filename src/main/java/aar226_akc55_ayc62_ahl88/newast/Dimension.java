package aar226_akc55_ayc62_ahl88.newast;

import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
import java.util.Collections;

public class Dimension extends AstNode{

    /**
     * Number of Brackets []
     */
    private long dim;

    /**
     * Every [] doesn't have a number
     */
    public boolean allEmpty;

    /**
     * We have found at least 1 empty Bracket
     */
    public boolean foundEmpty;
    /**
     * Indicies of null = [] or non null representing [4]
     */
    public ArrayList<Expr> indices;

    /**
     * @param l line number
     * @param c column number
     */
    public Dimension(int l, int c) {
        super(l,c);
        dim = 0;
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

    @Override
    /**Should not be used because we never call Dimension
     * Pretty Print
     */
    public void prettyPrint(CodeWriterSExpPrinter p) {
        System.out.println("We should not be in Dimension Pretty Print");
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
