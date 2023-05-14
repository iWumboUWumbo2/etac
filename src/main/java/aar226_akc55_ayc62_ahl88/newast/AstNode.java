package aar226_akc55_ayc62_ahl88.newast;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/**
 * Abstract Class for AST nodes
 */
public abstract class AstNode implements Printer {
    int line;
    int col;
    protected Type nodeType;
    public int getLine(){return line;}
    public int getColumn(){return col;}

    /**
     * @param l line number
     * @param c column number
     */
    public AstNode(int l, int c){
        line = l;
        col = c;
    }
    public abstract void prettyPrint(CodeWriterSExpPrinter p);
    public Type getNodeType(){
        return nodeType;
    }
}
