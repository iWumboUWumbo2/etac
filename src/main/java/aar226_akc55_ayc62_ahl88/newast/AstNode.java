package aar226_akc55_ayc62_ahl88.newast;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/**
 * Abstract Class for AST nodes
 */
public abstract class AstNode implements Printer {
    int line;
    int col;

    /**
     * @param l line number
     * @param c column number
     */
    public AstNode(int l, int c){
        line = l;
        col = c;
    }

    /**
     * @return Return line number for element
     */
    public int getLine(){return line;}

    /**
     * @return Return starting column number for element
     */
    public int getColumn(){return col;}
    public abstract void prettyPrint(CodeWriterSExpPrinter p);

    public abstract Type typeCheck(SymbolTable table);
}
