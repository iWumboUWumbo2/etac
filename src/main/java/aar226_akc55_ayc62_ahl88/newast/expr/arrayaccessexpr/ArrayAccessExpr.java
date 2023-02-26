package aar226_akc55_ayc62_ahl88.newast.expr.arrayaccessexpr;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Dimension;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.*;
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
    public ArrayAccessExpr(Expr argArray, ArrayList<Expr> arrayOfIndexing, int l, int c){
        super (l, c);
        orgArray = argArray;
        indicies = arrayOfIndexing;
    }
    private void typeCheckIndices(SymbolTable s) throws Error {
        for (Expr e : indicies) {
            if (e.typeCheck(s).getType() != Type.TypeCheckingType.INT){
                throw new SemanticError(e.getLine(), e.getColumn(), "array index type not int");
            }
        }
    }
    @Override
    public Type typeCheck(SymbolTable s) throws Error {
        Type e = orgArray.typeCheck(s);

        // check if indices are all ints
        typeCheckIndices(s);

        // throw error if arg is not array
        if (!e.isArray()) {
            throw new SemanticError(orgArray.getLine(), orgArray.getColumn(), "type is not indexable");
        } else {
            long return_dim = e.dimensions.getDim() - indicies.size();

            // throw error if length of access indices > arg dimension
            if (return_dim < 0) {
                throw new SemanticError(orgArray.getLine(), orgArray.getColumn(), "array index size mismatch");
            } else if (return_dim == 0) {   // if return dim == arg dim, return literal type
                if (e.getType() == Type.TypeCheckingType.BOOLARRAY)
                    return new Type(Type.TypeCheckingType.BOOL);
                if (e.getType() == Type.TypeCheckingType.INTARRAY)
                    return new Type(Type.TypeCheckingType.INT);
                return new Type(Type.TypeCheckingType.UNKNOWN);
            } else {    // otherwise, return array type
                return new Type(e.getType(), new Dimension(return_dim, getLine(), getColumn()));
            }
        }
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
