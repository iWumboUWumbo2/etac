package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
import java.util.Collections;

public class ArrayAccess extends Expr implements Printer {
    // does both expr and stmt
    private Expr e;
    private Id id;
    private ArrayList<Expr> indexes;

    private boolean is_stmt;

    public ArrayAccess(Expr e, ArrayList<Expr> index, boolean is_stmt) {
        this.e = e;
        this.indexes = index;
        this.type = Exprs.ArrayAccess;
        this.is_stmt = is_stmt;
    }

    public ArrayAccess(Id id, ArrayList<Expr> index, boolean is_stmt) {
        this.e = id;
        this.id = id;
        this.indexes = index;
        this.type = Exprs.ArrayAccess;
        this.is_stmt = is_stmt;

    }

    public Id getId() {
        return id;
    }

    public ArrayList<Expr> getIndexes() {
        return indexes;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
////        System.out.println("aRRACCCC");
//        p.startList();
//        e.prettyPrint(p);
//        indexes.forEach(i -> {
////            System.out.println("INSIDE");
//            p.printAtom("[");
//            i.prettyPrint(p);
//            p.printAtom("]");
//        } );
//        p.endList();
        for (int i = 0; i< indexes.size();i++){
            p.startList();
            p.printAtom("[]");
        }
        e.prettyPrint(p);
//        if (id != null) {
//            id.prettyPrint(p);
//        }
        ArrayList<Expr> rev = new ArrayList<>(indexes);
        if (is_stmt) Collections.reverse(rev);
        for (int i = 0; i<rev.size();i++){
            rev.get(i).prettyPrint(p);
            p.endList();
        }
    }



}
