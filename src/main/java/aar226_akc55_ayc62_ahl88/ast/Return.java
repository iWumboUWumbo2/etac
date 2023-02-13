package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

public class Return extends Stmt {
    private ArrayList<Expr> exprs;


    public Return(ArrayList<Expr> e){
        exprs = e;
    }
    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("return");
        exprs.forEach(e -> e.prettyPrint(p));
        p.endList();
    }
}
