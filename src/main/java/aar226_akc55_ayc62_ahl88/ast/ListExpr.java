package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

class ListExpr extends Expr {
    private ArrayList<Expr> exprs;
    private String raw;

    public ListExpr(ArrayList<Expr> exprs) {
        this.exprs = exprs;
        this.type = Exprs.ListExpr;
        this.raw = null;
    }

    public ListExpr(String s) {
        exprs = new ArrayList<>();
        this.raw = s;
        this.type = Exprs.ListExpr;
        for (char c : s.toCharArray()) {
            exprs.add(new IntLiteral(Character.toString(c)));
        }
    }

    public String toString() {
        if (raw != null) {
            return raw;
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < sb.length() - 1; i++) {
                sb.append(exprs.get(i)).append(" ");
            }
            sb.append(exprs.get(sb.length() - 1));

            return sb.toString();
        }
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        if (raw != null) {
            p.printAtom("\"");
            p.printAtom(raw);
            p.printAtom("\"");
        } else {
            p.startList();
            exprs.forEach(e -> e.prettyPrint(p));
            p.endList();
        }
    }
}
