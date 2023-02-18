package aar226_akc55_ayc62_ahl88.newast.expr;

import aar226_akc55_ayc62_ahl88.ast.Id;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

public class FunctionCall extends Expr {
    Id id;
    ArrayList<Expr> args;

    public FunctionCall(Id i, ArrayList<Expr> inArgs, int l, int c) {
        super (l, c);
        id = i;
        args = inArgs;
    }
    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        id.prettyPrint(p);
        args.forEach(e -> e.prettyPrint(p));
        p.endList();

    }
}
