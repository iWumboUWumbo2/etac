package aar226_akc55_ayc62_ahl88.newast;

import aar226_akc55_ayc62_ahl88.ast.Printer;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public abstract class AstNode implements Printer {
    int line;
    int col;
    public AstNode(int l, int c){
        line = l;
        col = c;
    }
    public int getLine(){return line;}
    public int getColumn(){return col;}
    public abstract void prettyPrint(CodeWriterSExpPrinter p);
}
