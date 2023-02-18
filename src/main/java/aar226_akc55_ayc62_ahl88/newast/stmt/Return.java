package aar226_akc55_ayc62_ahl88.newast.stmt;

import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
/**
 * Return Class for Ast Nodes
 */
public class Return extends Stmt{
    private ArrayList<Expr> returnArgList;


    /**
     * @param inputreturnList list of Return
     * @param l line Number
     * @param c Column Number
     */
    public Return(ArrayList<Expr> inputreturnList, int l, int c){
        super(l,c);
        returnArgList = inputreturnList;
    }
    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("return");
        returnArgList.forEach(e -> e.prettyPrint(p));
        p.endList();
    }
}
