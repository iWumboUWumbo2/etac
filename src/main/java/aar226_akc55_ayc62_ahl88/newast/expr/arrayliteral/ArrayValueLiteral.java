package aar226_akc55_ayc62_ahl88.newast.expr.arrayliteral;

import aar226_akc55_ayc62_ahl88.newast.expr.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

public class ArrayValueLiteral extends Expr {
    private String raw;
    private ArrayList<Expr> values;

    public ArrayValueLiteral(String s, int l, int c) {
        super(l, c);
        values = new ArrayList<>();
        this.raw = s;
        for (char ch : s.toCharArray()) {
            values.add(new IntLiteral(Character.toString(ch), l, c));
        }
    }
    public String toString(){
        return values.toString();
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        if (raw == null){
            for (Expr v: values){
                (v).prettyPrint(p);
            }
        }else{
            p.printAtom("\"" +raw+ "\"");
        }
    }
}
