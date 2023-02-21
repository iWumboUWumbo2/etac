package aar226_akc55_ayc62_ahl88.newast.expr.arrayaccessexpr;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
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
    public Boolean typeCheckHelper(SymbolTable s){
        for (Expr e : this.indicies) {
            if (e.typeCheck(s).getType() != Type.TypeCheckingType.INT){
                return false;
            }
        }
        return true;
    }
    @Override
    public Type typeCheck(SymbolTable s) {
        Type e1 = orgArray.typeCheck(s);
        Boolean indiciesCheck = typeCheckHelper(s);
        if (e1.getType() == Type.TypeCheckingType.INTARRAY){
            if (e1.dimensions.getDim() != indicies.size()) {
                throw new Error("Incorrect number of indices");
            }
            if (indiciesCheck) {
                return new Type(Type.TypeCheckingType.INT);
            } else {
                throw new Error("Index is not type int");
            }
        } else if (e1.getType() == Type.TypeCheckingType.BOOLARRAY) {
            if (e1.dimensions.getDim() != indicies.size()) {
                throw new Error("Incorrect number of indices");
            }
            if (indiciesCheck) {
                return new Type(Type.TypeCheckingType.BOOL);
            } else {
                throw new Error("Index is not type int");
            }
        } else if (e1.getType() == Type.TypeCheckingType.FILLEDARR || e1.getType() == Type.TypeCheckingType.EMPTYDIMENSIONALARRAY) {
            if (e1.dimensions.getDim() != indicies.size()) {
                throw new Error("Incorrect number of indices");
            }
            if (indiciesCheck) {
                return new Type(Type.TypeCheckingType.BOOL);
            } else {
                throw new Error("Index is not type int");
            }
        } else {
            throw new Error("Not an array");
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
