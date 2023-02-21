package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class Length extends Expr {

    Expr arg;

    public Length(Expr e) {
        arg = e;
    }

//    public Length(Id id) {
//        arg = id;
//    }
//
//    public Length(ArrayExpr e) {
//        arg = e;
//    }
//
//    public Length(ArrayValueLiteral e) {
//        arg = e;
//    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p){
        p.startList();
        p.printAtom("length");
        arg.prettyPrint(p);
        p.endList();
    }
}
