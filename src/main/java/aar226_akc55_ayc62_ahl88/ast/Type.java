package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
import java.util.Collections;

public class Type implements Printer {
    public Dimension dimensions;
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
        ArrayList<Long> rev = new ArrayList<>(dimensions.indices);
        Collections.reverse(rev);
        for (int i = 0; i < dimensions.getDim(); i++) {
            p.startList();
            if (rev.get(i) != null){
                p.printAtom("[" + rev.get(i) +"]");
            }else {
                p.printAtom("[]");
            }
        }
        p.printAtom(getTypeAsString());
        for (int i = 0; i < dimensions.getDim(); i++) {
            p.endList();
        }
    }
}
