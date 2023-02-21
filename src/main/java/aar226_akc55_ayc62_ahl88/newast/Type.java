package aar226_akc55_ayc62_ahl88.newast;

import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class to represent a type
 */
public class Type extends AstNode {
    public enum TypeCheckingType {
        Int,
        Bool,
        EmptyDimensionalArray,
        MutliTypes,
        Unit,
        Void,
        Return,
        Func,
        FilledArr
    }
    public Dimension dimensions;
    private boolean isInt;

    private TypeCheckingType tct;

    private ArrayList<Type> inputTypes, outputTypes;

    /**
     * @param t type
     * @param d dimension
     * @param l line number
     * @param c column number
     */
    public Type (boolean t,Dimension d,int l, int c) {
        super(l,c);
        isInt = t;
        dimensions = d;
    }

    public Type(TypeCheckingType tct, int l, int c) {
        super(l, c);
        this.tct = tct;
    }

    public boolean isBasicInt(){
        return isInt && (dimensions.getDim() == 0);
    }
    public boolean isBasicBool(){
        return (!isInt) && (dimensions.getDim() == 0);
    }
    private String getTypeAsString() {
        return (isInt) ? "int" : "bool";
    }

    public TypeCheckingType getTct () {
        return tct;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        ArrayList<Expr> rev = new ArrayList<>(dimensions.indices);
        Collections.reverse(rev);
        for (int i = 0; i < dimensions.getDim(); i++) {
            p.startList();
            p.printAtom("[]");
        }
        p.printAtom(getTypeAsString());
        for (int i = 0; i < dimensions.getDim(); i++) {
            if (rev.get(i) != null){
                rev.get(i).prettyPrint(p);
            }
            p.endList();
        }

    }
}
