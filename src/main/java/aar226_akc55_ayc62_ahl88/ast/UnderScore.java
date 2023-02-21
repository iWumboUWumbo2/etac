package aar226_akc55_ayc62_ahl88.ast;


import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class UnderScore extends Decl{

    public UnderScore(){
        super(null, null);
    }
    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.printAtom("_");
    }
}
