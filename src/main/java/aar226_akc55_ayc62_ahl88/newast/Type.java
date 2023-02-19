package aar226_akc55_ayc62_ahl88.newast;

import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
import java.util.Collections;

public class Type extends AstNode {
    public Dimension dimensions;
    private boolean isInt;

    public Type (boolean t,Dimension d,int l, int c) {
        super(l,c);
        isInt = t;
        dimensions = d;
    }

    private String getTypeAsString() {
        return (isInt) ? "int" : "bool";
    }


    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        ArrayList<Expr> rev = new ArrayList<>(dimensions.indices);
        Collections.reverse(rev);
        for (int i = 0; i < dimensions.getDim(); i++) {
            p.startList();
            p.printAtom("[]");
        }
        p.printAtom(getTypeAsString());
        for (int i = 0; i < dimensions.getDim(); i++) {
            if (rev.get(i) != null){
                rev.get(i).prettyPrint(p);
            }
            p.endList();
        }

    }
}
