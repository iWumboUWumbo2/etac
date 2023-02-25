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
        UNKNOWNARRAY,
        UNKNOWN,
        UNIT,
        VOID,
        RETURN,
        FUNC,
        UNDERSCORE
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
    public Type(ArrayList<Type> inTy, ArrayList<Type> outTy){
        super(-1,-1);
//        System.out.println("ONLY THE FUNC TYPE");
        this.tct = Type.TypeCheckingType.FUNC;
        inputTypes = inTy;
        outputTypes = outTy;
    }
    public Type(TypeCheckingType tct, Dimension d){
        super(-1,-1);
        this.tct = tct;
        dimensions = d;
    }

    public boolean isArray() {
        return this.getType() == Type.TypeCheckingType.INTARRAY ||
                this.getType() == Type.TypeCheckingType.BOOLARRAY ||
                this.getType() == Type.TypeCheckingType.UNKNOWNARRAY;
    }
    public boolean sameType(Type rhs) {
        // check if param is array and make sure procedure input is also array. Then compare dimensions
        if (getType() == TypeCheckingType.UNDERSCORE || (rhs.getType() == TypeCheckingType.UNDERSCORE)) {
            return true;
        }
        if (isArray()) {
            if (this.getType() == Type.TypeCheckingType.UNKNOWNARRAY &&
                    rhs.getType() != Type.TypeCheckingType.UNKNOWNARRAY) {
                return this.dimensions.getDim() <= rhs.dimensions.getDim();
            } else if (rhs.getType() == Type.TypeCheckingType.UNKNOWNARRAY &&
                    this.getType() != Type.TypeCheckingType.UNKNOWNARRAY) {
                return rhs.dimensions.getDim() <= this.dimensions.getDim();
            } else if (this.getType() == Type.TypeCheckingType.UNKNOWNARRAY &&
                    this.getType() == Type.TypeCheckingType.UNKNOWNARRAY) {
                return true;
            }
            else {
                return dimensions.equalsDimension(rhs.dimensions);
            }
        }
        // if one of the types is unknown, then equality is not false
        if (getType() != rhs.getType() &&
                !(getType() == TypeCheckingType.UNKNOWN ||
                rhs.getType() == TypeCheckingType.UNKNOWN)) {
            return false;
        }

        return true;
    }
    public TypeCheckingType getType() {return tct;}


    //todo
    public String toString(){
        String builder = tct.toString();
        switch (this.tct){
            case FUNC:
                builder += (" [ ");
                for (Type t:inputTypes){
                    builder += (t.toString() + " ");
                }
                builder += ("] ");
                builder += ("[ ");
                for (Type t: outputTypes){
                    builder += (t.toString() + " ");
                }
                builder += ("]\n");
                break;
            default:
                break;

        }
        return builder;
    }

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
