package aar226_akc55_ayc62_ahl88.newast.expr;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class Length extends Expr {
    Expr arg;

    public Length(Expr e, int l, int c) {
        super(l,c);
        arg = e;
    }
    @Override
    public void prettyPrint(CodeWriterSExpPrinter p){
        p.startList();
        p.printAtom("length");
        arg.prettyPrint(p);
        p.endList();
    }

    // TypeCheck Arg is Array
}
