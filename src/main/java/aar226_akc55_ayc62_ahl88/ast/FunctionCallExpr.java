package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

public class FunctionCallExpr extends Expr {
    Id id;
    ArrayList<Expr> param;

    public FunctionCallExpr(Id id, ArrayList<Expr> param) {
        this.id = id;
        this.param = param;
        this.type = Exprs.FuncCall;
    }

    public FunctionCallExpr(String s, ArrayList<Expr> param) {
        this.id = new Id(s);
        this.param = param;
        this.type = Exprs.FuncCall;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        id.prettyPrint(p);
        param.forEach(e -> e.prettyPrint(p));
        p.endList();
    }
}
