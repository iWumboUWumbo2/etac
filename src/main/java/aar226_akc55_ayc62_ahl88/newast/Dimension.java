package aar226_akc55_ayc62_ahl88.newast;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
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

    public Dimension(long dim, int l, int c) {
        super(l,c);
        this.dim = dim;
        allEmpty = false;
        foundEmpty = false;
        indices = new ArrayList<>();
    }

    public void increment() {
        dim++;
    }

    public long getDim() {
        return dim;
    }

    public boolean equalsDimension(Dimension d) {
        return this.getDim() == d.getDim();
//        if (this.allEmpty != d.allEmpty) return false;
//        if (this.dim != d.dim) return false;
//        if ((this.indices != null) && (d.indices != null)) {
//            if (this.indices.size() != d.indices.size()) return false;
//            for (int i = 0; i < this.indices.size(); i++) {
//                if (this.indices.get(i) != d.indices.get(i)) {
//                    return false;
//                }
//            }
//        }
//        else {
//            return false;
//        }
//
//        return true;
    }

    @Override
    /**Should not be used because we never call Dimension
     * Pretty Print
     */
    public void prettyPrint(CodeWriterSExpPrinter p) {
//        System.out.println("We should not be in Dimension Pretty Print");
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

    public Type typeCheck(SymbolTable s) {
        for (Expr index : indices) {
            if (index != null) {
                index.typeCheck(s);
            }
        }
        nodeType = new Type(Type.TypeCheckingType.UNIT);
        return nodeType;
    }

    public ArrayList<Expr> getIndices() {
        return indices;
    }
}