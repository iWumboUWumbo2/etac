package aar226_akc55_ayc62_ahl88.newast.stmt;


import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
/**
 * Class for Block Ast Node
 */
public class Block extends Stmt {
    private ArrayList<Stmt> statementList;


    /**
     * @param inStmtList statement list inside our block
     * @param l line number
     * @param c column number
     */
    public Block(ArrayList<Stmt> inStmtList, int l, int c){
        super(l,c);
        statementList = inStmtList;
    }

    public void prettyPrint(CodeWriterSExpPrinter p) {

        p.startUnifiedList();
        statementList.forEach(e -> e.prettyPrint(p));
        p.endList();

    }
}

