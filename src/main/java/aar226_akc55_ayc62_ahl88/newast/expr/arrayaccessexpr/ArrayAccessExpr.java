package aar226_akc55_ayc62_ahl88.newast.expr.arrayaccessexpr;

import aar226_akc55_ayc62_ahl88.ast.Expr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
/**
 * Class for Array Accessing
 */
public class ArrayAccessExpr extends Expr {

    // Type Check that it is an Actual Array
    Expr orgArray;

    // Read This Left to Right
    ArrayList<Expr> indicies;

    /**
     * @param argArray Expression prior to array indexing
     * @param arrayOfIndexing Expressions found within indexing area.
     */
    public ArrayAccessExpr(Expr argArray, ArrayList<Expr> arrayOfIndexing){
        orgArray = argArray;
        indicies = arrayOfIndexing;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        for (int i = 0; i < indicies.size();i++){
            p.startList();
            p.printAtom("[]");
        }
        orgArray.prettyPrint(p);
        for (int i = 0; i< indicies.size(); i++){
            indicies.get(i).prettyPrint(p);
            p.endList();
        }
    }

}
