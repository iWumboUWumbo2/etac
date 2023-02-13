package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.SExpPrinter;

import java.util.ArrayList;

public class Block extends Stmt {
    private ArrayList<Stmt> stmts;
    private ArrayList<Expr> exprs;

    public Block(ArrayList<Stmt> s){
        stmts = s;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startUnifiedList();

        p.startUnifiedList();
        stmts.forEach(e -> e.prettyPrint(p));
        p.endList();

        p.startUnifiedList();
        if (exprs != null) {
            exprs.forEach(d -> d.prettyPrint(p));
        }
        p.endList();

        p.endList();
    }
}
