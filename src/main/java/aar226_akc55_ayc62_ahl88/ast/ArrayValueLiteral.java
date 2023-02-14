package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

public class ArrayValueLiteral extends Expr implements Value{
    private String raw;
    private ArrayList<Value> values;

    private ValueType vt;
    public ArrayValueLiteral(String s) {
        values = new ArrayList<>();
        this.raw = s;
        for (char c : s.toCharArray()) {
            values.add(new IntLiteral(Character.toString(c)));
        }
        vt = ValueType.ARRAYVALUELITERAL;
    }
    public ArrayValueLiteral(ArrayList<Value> v) {
        values = v;
    }
    public ValueType getValueType(){
        return vt;
    }

    public String toString(){
        return values.toString();
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        if (raw == null){
            for (Value v: values){
                ((Expr) v).prettyPrint(p);
            }
        }else{
            p.printAtom("\"" +raw+ "\"");
        }
        p.endList();
    }
}
