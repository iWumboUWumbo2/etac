package aar226_akc55_ayc62_ahl88.newast.expr.arrayliteral;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Dimension;
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

    private boolean typeEquals(Type t1, Type t2) {
        if (t1.getType() == Type.TypeCheckingType.INT ||
                t1.getType() == Type.TypeCheckingType.BOOL) {
            return t2.getType() == t1.getType();
        } else if (t1.getType() == Type.TypeCheckingType.INTARRAY ||
                t1.getType() == Type.TypeCheckingType.BOOLARRAY) {
            return t2.getType() == t1.getType() &&
                    t1.dimensions.equalsDimension(t2.dimensions);
        } else {
            return false;
        }
    }

    @Override
    public Type typeCheck(SymbolTable s) throws Error{

        Type t1 = values.get(0).typeCheck(s);
        for (Expr e : values) {
            if (!typeEquals(t1, e.typeCheck(s))) {
                String message = Integer.toString(e.getLine())
                        + ":" + Integer.toString(e.getColumn())
                        + "  TypeError: array element type mismatch";
                throw new Error(message);
            }
        }
        // literal
        if (t1.getType() == Type.TypeCheckingType.INT) {
            return new Type(Type.TypeCheckingType.INT);
        } else if (t1.getType() == Type.TypeCheckingType.BOOL) {
            return new Type(Type.TypeCheckingType.BOOL);

        }
        // if not literal, then must be array
        else {
            long dim_num = t1.dimensions.getDim()+1;
            Dimension dim = new Dimension(dim_num, getLine(), getColumn());
            return new Type(t1.getType(), dim, t1);
        }
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
