package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class Type implements Printer {
    private Dimension dimensions;
    private boolean isInt;

    public Type (boolean t,Dimension d) {
        isInt = t;
        dimensions = d;
    }

    private String getTypeAsString() {
        return (isInt) ? "int" : "bool";
    }

    public String toString(){
        String s = getTypeAsString();
        return "( " + s  + dimensions.toString() + " )";
    }

    public void prettyPrint(CodeWriterSExpPrinter p) {
        for (long i = 0; i < dimensions.getDim(); i++) {
            p.startList();
            p.printAtom("[]");
        }
        p.printAtom(getTypeAsString());
        for (long i = 0; i < dimensions.getDim(); i++) {
            p.endList();
        }
    }
}
