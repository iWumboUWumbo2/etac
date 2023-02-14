package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

public class ArrayAccess extends Expr implements Printer {


    private Expr e;
    private Id id;
    private ArrayList<Expr> indexes;



    public ArrayAccess(Expr e, ArrayList<Expr> index) {
        this.e = e;
        this.indexes = index;
        this.type = Exprs.ArrayAccess;
    }

    public ArrayAccess(Id id, ArrayList<Expr> index) {
        this.id = id;
        this.indexes = index;
        this.type = Exprs.ArrayAccess;
    }

    public Id getId() {
        return id;
    }

    public ArrayList<Expr> getIndexes() {
        return indexes;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
//        System.out.println("aRRACCCC");
        p.startList();
        e.prettyPrint(p);
        indexes.forEach(i -> {
            p.printAtom("[");
            i.prettyPrint(p);
            p.printAtom("]");
        } );
        p.endList();
    }



}
