package aar226_akc55_ayc62_ahl88.newast;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class to represent a type
 */
public class Type implements Printer {
    public enum TypeCheckingType {
        INT,
        BOOL,
        INTARRAY, // Any Dimension
        BOOLARRAY, // Any Dimension
        RECORDARRAY,
        UNKNOWNARRAY,
        UNKNOWN,
        UNIT,
        VOID,
        RETURN,
        FUNC,
        UNDERSCORE,
        MULTIRETURN,
        RECORD
    }
    /**
     * @return Return line number for element
     */
    public int getLine(){return line;}

    /**
     * @return Return starting column number for element
     */
    public int getColumn(){return col;}
    int line;
    int col;
    public Dimension dimensions;
    private boolean isInt;

    private TypeCheckingType tct;

    public ArrayList<Type> inputTypes, outputTypes;
    public ArrayList<Type> multiTypes;

    public ArrayList<Type> recordFieldTypes;

    public String recordName;

    public Type arrayType;


    /**
     * @param t type
     * @param d dimension
     * @param l line number
     * @param c column number
     */
    public Type (boolean t,Dimension d,int l, int c) {
//        super(l,c);
        line = l;
        col = c;
        isInt = t;
        dimensions = d;
        if (d.getDim() == 0) {
            tct = (isInt) ? TypeCheckingType.INT : TypeCheckingType.BOOL;
        }
        else {
            tct = (isInt) ? TypeCheckingType.INTARRAY : TypeCheckingType.BOOLARRAY;
        }
//        inputTypes = new ArrayList<>();
//        outputTypes = new ArrayList<>();
    }

    public Type (String record,Dimension d,int l, int c) {
//        super(l,c);
        line = l;
        col = c;
        isInt = false;
        dimensions = d;
        recordName = record;
        if (d.getDim() == 0) {
            tct = TypeCheckingType.RECORD;
        } else {
            tct = TypeCheckingType.RECORDARRAY;
        }

//        inputTypes = new ArrayList<>();
//        outputTypes = new ArrayList<>();
    }

    public Type (String record, ArrayList<Type> types, int l, int c) {
//        super(l,c);
        line = l;
        col = c;
        isInt = false;
        recordName = record;
        tct = TypeCheckingType.RECORD;
        recordFieldTypes = new ArrayList<Type>(types);
    }

    public Type(ArrayList<Type> multiTypes) {
//        super(-1, -1);
        line = -1;
        col = -1;
        this.multiTypes = multiTypes;
        this.tct = TypeCheckingType.MULTIRETURN;
    }

    public Type(TypeCheckingType tct) {
//        super(-1, -1);
        line = -1;
        col = -1;
        this.tct = tct;
//        dimensions = new Dimension(0, getLine(), getColumn());
//        inputTypes = new ArrayList<>();
//        outputTypes = new ArrayList<>();
    }
    public Type(ArrayList<Type> inTy, ArrayList<Type> outTy){
        line = -1;
        col = -1;
        this.tct = Type.TypeCheckingType.FUNC;
        inputTypes = inTy;
        outputTypes = outTy;
    }
    public Type(TypeCheckingType tct, Dimension d){
        line = -1;
        col = -1;
        this.tct = tct;
        dimensions = d;
    }

    public boolean isArray() {
        return this.getType() == Type.TypeCheckingType.INTARRAY ||
                this.getType() == Type.TypeCheckingType.BOOLARRAY ||
                this.getType() == Type.TypeCheckingType.UNKNOWNARRAY;
    }

    public boolean isBasic(){
        return getType() == TypeCheckingType.INT
                || getType() == TypeCheckingType.BOOL
                || getType() == TypeCheckingType.UNKNOWN;
    }

    public boolean isSameFunc(Type rhs){
        if (!(tct == TypeCheckingType.FUNC && rhs.getType() == TypeCheckingType.FUNC)){
            throw new SemanticError(-1,-1, "both aren't functions");
        }
        ArrayList<Type> rhsIn = rhs.inputTypes;
        ArrayList<Type> rhsOut = rhs.outputTypes;
        if (rhsIn.size() != inputTypes.size()){
            return false;
        }
        for (int i = 0; i< rhsIn.size();i++){
            if (!inputTypes.get(i).sameType(rhsIn.get(i))){
                return false;
            }
        }
        if (rhsOut.size() != outputTypes.size()){
            return false;
        }
//        System.out.println("go through Out");
        for (int i = 0; i< rhsOut.size();i++){
            if (!outputTypes.get(i).sameType(rhsOut.get(i))){
                return false;
            }
        }
        return true;
    }

    public boolean isSameRecord(Type rhs){
        if (!(tct == TypeCheckingType.RECORD && rhs.getType() == TypeCheckingType.RECORD)){
            throw new SemanticError(-1,-1, "both aren't functions");
        }
        ArrayList<Type> rhsIn = rhs.recordFieldTypes;
        if (rhsIn.size() != recordFieldTypes.size()){
            return false;
        }
        for (int i = 0; i< rhsIn.size();i++){
            if (!recordFieldTypes.get(i).sameType(rhsIn.get(i))){
                return false;
            }
        }
        return true;
    }

    public boolean sameArray(Type rhs){
        if (!(isArray() && rhs.isArray())){
            throw new SemanticError(getLine(), getColumn(),"we shouldnt be in array checker");
        }
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
    public boolean sameBasic(Type rhs){
        if (!(isBasic() && rhs.isBasic())){
            throw new SemanticError(getLine(), getColumn(), "we shouldn't be in same basic checker");
        }
        TypeCheckingType lhsType = getType();
        TypeCheckingType rhsType = rhs.getType();

        if (lhsType == TypeCheckingType.UNKNOWN || rhsType == TypeCheckingType.UNKNOWN){
            return true;
        }
        else if (lhsType == TypeCheckingType.INT && rhsType == TypeCheckingType.INT){
            return true;
        }else if (lhsType == TypeCheckingType.BOOL && rhsType == TypeCheckingType.BOOL){
            return true;
        }else if (lhsType == TypeCheckingType.INT && rhsType == TypeCheckingType.BOOL){
            return false;
        }else if (lhsType == TypeCheckingType.BOOL && rhsType == TypeCheckingType.BOOL){
            return false;
        }
        throw new SemanticError(getLine(), getColumn(), "somehow we missed a case in same basic");
    }

    public boolean isUnknown() {
        return this.getType() == Type.TypeCheckingType.UNKNOWN;
    }
    public boolean isUnknownArray() {
        return this.getType() == Type.TypeCheckingType.UNKNOWNARRAY;
    }

    /**
     * PRECONDITION: assume both types have passed sameType
     * @param t
     * @return
     */
    public Type greaterType(Type t) {
        if (!sameType(t)) throw new SemanticError(-1, -1,"U DONE FUCED UP");
        // neither unknown
        if (!(isUnknown() || isUnknownArray()) && !(t.isUnknown() || t.isUnknownArray())) {
            return this;
        // both unknown
        } else if ((isUnknown() || isUnknownArray()) && (t.isUnknown() || t.isUnknownArray())) {
//            System.out.println("this");
//            System.out.println(dimensions.getDim());
//            System.out.println("t");
//            System.out.println(t.dimensions.getDim());
            if ((dimensions != null) && (t.dimensions != null)
                    && (dimensions.getDim() > t.dimensions.getDim())) {
//                System.out.println("here");
                return this;
            } else {
                return t;
            }
        }
        // this unknown
        else if (isUnknown() || isUnknownArray()) {
            return t;
        // t unknown
        } else {
            return this;
        }
    }

    public boolean sameType(Type rhs) {
        if (getType() == TypeCheckingType.UNDERSCORE){
            return rhs.getType() != TypeCheckingType.MULTIRETURN;
        }
        // if one of the types is ambiguous, then equality is true
        // otherwise, if both types are not ambiguous, we type check
        if (getType() != rhs.getType() &&
                !(getType() == TypeCheckingType.UNKNOWN
                || rhs.getType() == TypeCheckingType.UNKNOWN
                || getType() == TypeCheckingType.UNKNOWNARRAY
                || rhs.getType() == TypeCheckingType.UNKNOWNARRAY
                || getType() == TypeCheckingType.UNDERSCORE
                || rhs.getType() == TypeCheckingType.UNDERSCORE)) {
            return false;
        }

        if (getType() == TypeCheckingType.RECORD && rhs.getType() == TypeCheckingType.RECORD) {
            return recordName.equals(rhs.recordName);
        }

        // type check array
        if (isArray() && rhs.isArray()) { // if both sides are array, then type check them as array
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
        } else if ((isArray() || rhs.isArray()) &&
            !(isUnknown() || rhs.isUnknown())) {
            // if one side is not array and neither unknown, then they do not type check
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
