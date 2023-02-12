package aar226_akc55_ayc62_ahl88.ast;

import java.util.ArrayList;

public class ArrayValueLiteral implements Value{

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
}
