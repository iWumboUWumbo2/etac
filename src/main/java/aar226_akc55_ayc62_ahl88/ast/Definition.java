package aar226_akc55_ayc62_ahl88.ast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class Definition implements Printer {
    private Method method;
    private Globdecl globdecl;

    public Definition( Method m){

    }

    public String toString(){
        return "( " + method.toString() + " )";
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {

    }
}
