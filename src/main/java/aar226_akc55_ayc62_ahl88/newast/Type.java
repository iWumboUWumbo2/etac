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
        INT,
        BOOL,
        INTARRAY, // Any Dimension
        BOOLARRAY, // Any Dimension
        UNIT,
        VOID,
        RETURN,
        FUNC,
        UNDERSCORE
//        FILLEDARR
    }
    public Dimension dimensions;
    private boolean isInt;

    private TypeCheckingType tct;

    public ArrayList<Type> inputTypes, outputTypes;

    public Type arrayType;


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
        if (d.getDim() == 0) {
            tct = (isInt) ? TypeCheckingType.INT : TypeCheckingType.BOOL;
        }
        else {
            tct = (isInt) ? TypeCheckingType.INTARRAY : TypeCheckingType.BOOLARRAY;
        }
    }

    public Type(TypeCheckingType tct) {
        super(-1, -1);
        this.tct = tct;
    }

    public Type(TypeCheckingType tct, Dimension d){
        super(-1,-1);
        this.tct = tct;
        dimensions = d;
    }
    public Type(ArrayList<Type> inTy, ArrayList<Type> outTy){
        super(-1,-1);
        System.out.println("ONLY THE FUNC TYPE");
        this.tct = Type.TypeCheckingType.FUNC;
        inputTypes = inTy;
        outputTypes = outTy;
    }

    public boolean isArray(Type t) {
        return t.getType() == Type.TypeCheckingType.INTARRAY ||
                t.getType() == Type.TypeCheckingType.BOOLARRAY;
    }
    public boolean sameType(Type rhs) {
        if (getType() == TypeCheckingType.UNDERSCORE || (rhs.getType() == TypeCheckingType.UNDERSCORE)) {
            return true;
        }
        if (getType() != rhs.getType()) {
            return false;
        }
        // check if param is array and make sure procedure input is also array. Then compare dimensions
        if (isArray(this)) {
            return dimensions.equalsDimension(rhs.dimensions);
        }
        return true;
    }
    public TypeCheckingType getType() {return tct;}
    private String getTypeAsString() {
        return (isInt) ? "int" : "bool";
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
