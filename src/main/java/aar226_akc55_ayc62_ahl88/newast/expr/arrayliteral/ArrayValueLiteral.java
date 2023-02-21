package aar226_akc55_ayc62_ahl88.newast.expr.arrayliteral;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
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

    public ArrayValueLiteral(ArrayList<Expr> e, int l, int c) {
        super(l,c);
        values = e;
        raw = null;
    }

    @Override
    public Type typeCheck(SymbolTable s) throws Error{

        Type.TypeCheckingType t1 = values.get(0).typeCheck(s).getType();
        for (Expr e : values) {
            if (!(e.typeCheck(s).getType() == t1)) {
                String message = Integer.toString(e.getLine())
                        + ":" + Integer.toString(e.getColumn())
                        + "  TypeError: array element type mismatch";
                throw new Error(message);
            }
        }

        Type.TypeCheckingType listType = (t1 == Type.TypeCheckingType.INT) ?
                Type.TypeCheckingType.INTARRAY : Type.TypeCheckingType.BOOLARRAY;

        return new Type(listType);
    }

    public String toString(){
        return values.toString();
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        if (raw == null){
            p.startList();
            for (Expr v: values){
                (v).prettyPrint(p);
            }
            p.endList();
        }else{
            p.printAtom("\"" +raw+ "\"");
        }
    }
}
