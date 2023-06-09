package aar226_akc55_ayc62_ahl88.newast;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.newast.declarations.AnnotatedTypeDecl;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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
        RECORD,
        NULL,
        NULLARRAY
    }
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
    public boolean isRecord;
    public HashMap<String, Integer> recordFieldToIndex;
    /**
     * @return Return starting column number for element
     */
    public int getColumn(){return col;}
    /**
     * @return Return line number for element
     */
    public int getLine(){return line;}

    /**
     * @param t type
     * @param d dimension
     * @param l line number
     * @param c column number
     */
    public Type (boolean t,Dimension d,int l, int c) {
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
    }

    public Type (String record,Dimension d,int l, int c) {
        line = l;
        col = c;
        isInt = false;
        dimensions = d;
        recordName = record;
        isRecord = true;
        if (d.getDim() == 0) {
            tct = TypeCheckingType.RECORD;
        } else {
            tct = TypeCheckingType.RECORDARRAY;
        }
    }

    public ArrayList<Type> getInputTypes(ArrayList<AnnotatedTypeDecl> recordTypes){
        ArrayList<Type> inputTypes = new ArrayList<>();
        for (AnnotatedTypeDecl atd: recordTypes){
            inputTypes.add(atd.type);
        }
        return inputTypes;
    }
    public Type (String record, ArrayList<AnnotatedTypeDecl> types, int l, int c, boolean forRecord) {
        line = l;
        col = c;
        isInt = false;
        recordName = record;
        tct = TypeCheckingType.RECORD;
        isRecord = forRecord;
        recordFieldTypes = new ArrayList<Type>(getInputTypes(types));
        recordFieldToIndex = new HashMap<String, Integer>();
        for (int i = 0; i < types.size(); i++) {
            recordFieldToIndex.put(types.get(i).identifier.toString(), i);
        }
    }

    public Type (String record, ArrayList<Type> types, int l, int c) {
        line = l;
        col = c;
        isInt = false;
        recordName = record;
        tct = TypeCheckingType.RECORD;
        recordFieldTypes = types;
    }

    public Type(ArrayList<Type> multiTypes) {
        line = -1;
        col = -1;
        this.multiTypes = multiTypes;
        this.tct = TypeCheckingType.MULTIRETURN;
    }

    public Type(TypeCheckingType tct) {
        line = -1;
        col = -1;
        this.tct = tct;
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
                this.getType() == Type.TypeCheckingType.UNKNOWNARRAY ||
                this.getType() == TypeCheckingType.RECORDARRAY ||
                this.getType() == TypeCheckingType.NULLARRAY;
    }

    public boolean isNullArray() { return getType() == TypeCheckingType.NULLARRAY; }
    public boolean isNullable() {
        return isArray() || getType() == TypeCheckingType.RECORD;
    }
    public boolean isRecord() {
        return getType() == TypeCheckingType.RECORD;
    }
    public boolean isRecordArray() {
        return getType() == TypeCheckingType.RECORDARRAY;
    }
    public boolean isFunc() {
        return getType() == TypeCheckingType.FUNC;
    }
    public boolean isIntArray() {
        return getType() == TypeCheckingType.INTARRAY;
    }
    public boolean isBoolArray() {
        return getType() == TypeCheckingType.BOOLARRAY;
    }


    public boolean isBasic(){
        return getType() == TypeCheckingType.INT
                || getType() == TypeCheckingType.BOOL
                || getType() == TypeCheckingType.UNKNOWN;
    }

    public boolean isNull() {
        return getType() == TypeCheckingType.NULL;
    }


    public boolean isSameFunc(Type rhs){
        if (tct == TypeCheckingType.RECORD && rhs.getType() == TypeCheckingType.RECORD) {
            if (recordName.equals(rhs.recordName)) {
                return true;
            }
            throw new SemanticError(-1,-1, "not same record");
        }

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
        for (int i = 0; i< rhsOut.size();i++){
            if (!outputTypes.get(i).sameType(rhsOut.get(i))){
                return false;
            }
        }
        return true;
    }

    public boolean isSameRecord(Type table_t){
        // table_t is type from table
        if (!(tct == TypeCheckingType.RECORD && table_t.getType() == TypeCheckingType.RECORD)){
            throw new SemanticError(getLine(),getColumn(), "both aren't records");
        }
        if (!recordName.equals(table_t.recordName)) return false;
        ArrayList<Type> table_t_fields = table_t.recordFieldTypes;
        HashMap<String, Integer> table_t_map = table_t.recordFieldToIndex;
        if (table_t_map == null || table_t_fields == null ||
                table_t_map.size() == 0 || table_t_fields.size() == 0) {
            return true;
        }

        for (String id : table_t_map.keySet()) {
            int table_index = table_t_map.get(id);
            int this_index = (recordFieldToIndex.get(id) == null) ? -1 : recordFieldToIndex.get(id);

            if (!(table_index == this_index)) {
                throw new SemanticError(getLine(), getColumn(),
                        "field declarations out of order from interface declaration");
            }

            Type table_type = table_t_fields.get(table_index);
            Type this_type = recordFieldTypes.get(table_index);
            if (!table_type.sameType(this_type)) {
                throw new SemanticError(getLine(), getColumn(),
                        "Same field different types");
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

    public boolean isUnknown() {
        return this.getType() == Type.TypeCheckingType.UNKNOWN;
    }
    public boolean isUnknownArray() {
        return this.getType() == Type.TypeCheckingType.UNKNOWNARRAY;
    }

    public Type getGreaterDim(Type t1, Type t2) {
        if ((t1.dimensions != null) && (t2.dimensions != null)
                && (t1.dimensions.getDim() > t2.dimensions.getDim())) {
            return t1;
        } else {
            return t2;
        }
    }

    /**
     * PRECONDITION: assume both types have passed sameType
     * @param t
     * @return
     */
    public Type greaterType(Type t) {
        if (!sameType(t)) throw new SemanticError(-1, -1,"U DONE FUCED UP");
        // CASE 1: both null arrays
        if (isNullArray() && t.isNullArray()) {
            return getGreaterDim(this, t);
        }
        // CASE 2: one is null and the other is unknown
        // {{{}}} {{{null}}}
        else if (isNullArray() && t.isUnknownArray()) {
            return getGreaterDim(t, this);
        } else if (isUnknownArray() && t.isNullArray()) {
            return getGreaterDim(this, t);
        }
        // CASE 3: neither unknown or null
        else if (!(isUnknown() || isUnknownArray() || isNull() || isNullArray()) &&
                !(t.isUnknown() || t.isUnknownArray() || t.isNull() || t.isNullArray())) {
            return this;
            // CASE 4: both unknown
        } else if ((isUnknown() || isUnknownArray()) &&
                (t.isUnknown() || t.isUnknownArray())) {
            return getGreaterDim(this, t);
        }
        // CASE 5: this unknown or null and the other is not
        else if (isUnknown() || isUnknownArray() || isNull()|| isNullArray()) {
            return t;
        // CASE 6: this
        } else {
            return this;
        }
    }

    public boolean sameType(Type rhs) {
        if (getType() == TypeCheckingType.UNDERSCORE){
            return rhs.getType() != TypeCheckingType.MULTIRETURN;
        }

        // type record = null
        // type list = null
        if ((isNullable() && rhs.isNull()) || (rhs.isNullable() && isNull())) {
            return true;
        }

        // if one of the types is ambiguous, then equality is true
        // otherwise, if both types are not ambiguous, we type check
        if (getType() != rhs.getType() &&
                !(getType() == TypeCheckingType.UNKNOWN
                        || rhs.getType() == TypeCheckingType.UNKNOWN
                        || getType() == TypeCheckingType.UNKNOWNARRAY
                        || rhs.getType() == TypeCheckingType.UNKNOWNARRAY
                        || getType() == TypeCheckingType.UNDERSCORE
                        || rhs.getType() == TypeCheckingType.UNDERSCORE
                        || getType() == TypeCheckingType.NULLARRAY
                        || rhs.getType() == TypeCheckingType.NULLARRAY)) {
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
                // fail x:int[] = {null}
                // pass x:Record[] = {null}
            } else if (getType() == Type.TypeCheckingType.NULLARRAY &&
                    rhs.getType() != Type.TypeCheckingType.NULLARRAY ) {
                if (rhs.isRecordArray()) return (this.dimensions.getDim() <= rhs.dimensions.getDim());
                else return (this.dimensions.getDim() < rhs.dimensions.getDim());
            } else if (rhs.getType() == Type.TypeCheckingType.NULLARRAY &&
                    getType() != Type.TypeCheckingType.NULLARRAY) {
                if (this.isRecordArray()) return (rhs.dimensions.getDim() <= this.dimensions.getDim());
                else return (rhs.dimensions.getDim() < this.dimensions.getDim());
            } else if (rhs.getType() == Type.TypeCheckingType.NULLARRAY &&
                    getType() == Type.TypeCheckingType.NULLARRAY) {
                return true;
            } else if (rhs.isRecordArray() && isRecordArray()) {
                if (!rhs.recordName.equals(recordName)) {
                    return false;
                }
                return dimensions.equalsDimension(rhs.dimensions);
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

    public void setType(TypeCheckingType type) { tct = type; }

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
        if (recordName != null) {
            return recordName;
        } else {
            return (isInt) ? "int" : "bool";
        }
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