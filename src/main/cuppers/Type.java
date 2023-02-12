package aar226_akc55_ayc62_ahl88.ast;

public class Type {
    private Dimension dimensions;
    private boolean isInt;

    public Type (boolean t,Dimension d) {
        isInt = t;
        dimensions = d;
    }

    public String toString(){
        String s = "";

        if (isInt){
            s += "int";
        }else{
            s += "bool";
        }
        return "( " +s  + dimensions.toString() + " )";
    }
}
