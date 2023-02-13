package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

public class ArrayAccess extends Expr implements Printer {


    private Expr e;
    private ArrayList<Expr> index;

    public ArrayAccess(Expr e, ArrayList<Expr> index) {
        this.e = e;
        this.index = index;
        this.type = Exprs.ArrayAccess;
    }

    public void prettyPrint(CodeWriterSExpPrinter p) {
        System.out.println("aRRACCCC");
        p.startList();
        e.prettyPrint(p);
        index.forEach(i -> {
            p.printAtom("[");
            i.prettyPrint(p);
            p.printAtom("]");
        } );

    }



}
