package aar226_akc55_ayc62_ahl88.newast.expr.arrayliteral;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Dimension;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRExpr;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;
import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;

/**
 * Class for array value literal
 */
public class ArrayValueLiteral extends Expr {
    private String raw;
    private String escape;
    private ArrayList<Expr> values;
    public ArrayList<Expr> getValues() {
        return values;
    }
    public String getRaw() {
        return raw;
    }

    /**
     * @param s Raw string
     * @param l Line number
     * @param c Column number
     */
    public ArrayValueLiteral(String s, int l, int c) {
        super(l, c);
        values = new ArrayList<>();
        this.raw = s;
        escape = s.replaceAll("\\\\x","\\\\\\"+"\\x");
        escape = StringEscapeUtils.unescapeJava(escape);
        for (char ch : escape.toCharArray()) {
            values.add(new IntLiteral(ch, l, c));
        }
    }

    /**
     * @param e List of expressions
     * @param l Line number
     * @param c Column number
     */
    public ArrayValueLiteral(ArrayList<Expr> e, int l, int c) {
        super(l,c);
        values = e;
        raw = null;
    }

    /**
     * @param s Symbol table
     * @return Uknown type
     * @throws Error
     */
    private Type typeCheckUnknown(SymbolTable s) throws Error {
        return new Type(Type.TypeCheckingType.UNKNOWNARRAY,
                new Dimension(1, getLine(), getColumn())); // unknown dimension
    }

    /**
     * @param s Symbol table
     * @return Type
     * @throws Error
     * Guarantees values has length > 0
     */
    private Type typeCheckArray(SymbolTable s) throws Error {
        Type t1 = values.get(0).typeCheck(s);

        Type arrCheck = t1;
        for (Expr e : values) {     // check all elements same type
            Type eType = e.typeCheck(s);

            if (!arrCheck.sameType(eType)) {
                throw new SemanticError(e.getLine(), e.getColumn(), "array element type mismatch");
            }
            arrCheck = arrCheck.greaterType(eType);
        }

        // if t1 is array, then return multidimensional array lit
        if (arrCheck.isArray()) {
            long dim_num = arrCheck.dimensions.getDim()+1;
            Dimension dim = new Dimension(dim_num, getLine(), getColumn());
            if (arrCheck.isRecord() || arrCheck.isRecordArray()) {
                return correctType(arrCheck, dim, s);
            }
            return new Type(arrCheck.getType(),dim);
        }
        // if t1 not array, return dim 1 array
        else {
            Dimension dim = new Dimension(1, getLine(), getColumn());
            if (arrCheck.getType() == Type.TypeCheckingType.INT){
                return new Type(Type.TypeCheckingType.INTARRAY,dim);
            }else if (arrCheck.getType() == Type.TypeCheckingType.BOOL){
                return new Type(Type.TypeCheckingType.BOOLARRAY,dim);
            }else if (arrCheck.getType() == Type.TypeCheckingType.RECORD){
                return correctType(arrCheck, dim, s);
            }else if (arrCheck.getType() == Type.TypeCheckingType.UNKNOWN){
                return new Type(Type.TypeCheckingType.UNKNOWNARRAY,dim);
            }else if (arrCheck.getType() == Type.TypeCheckingType.NULL){
                return new Type(Type.TypeCheckingType.NULLARRAY,dim);
            }else{
                throw new SemanticError(getLine(), getColumn(), "Not a basic type");
            }
        }
    }

    /**
     * @param t original type
     * @param d new dimensions
     * @param table typechecking table
     * @return new type with dimension d
     */
    public Type correctType(Type t, Dimension d, SymbolTable<Type> table) {
        Type temp;
        Type recordType = table.lookup(new Id(t.recordName, getColumn(), getLine()));
        temp = new Type(recordType.recordName, recordType.recordFieldTypes, t.getColumn(), t.getLine());
        temp.recordFieldToIndex = recordType.recordFieldToIndex;
        temp.dimensions = d;
        temp.setType(Type.TypeCheckingType.RECORDARRAY);
        return temp;
    }

    @Override
    public Type typeCheck(SymbolTable s) throws Error{
        if (values.size() == 0) {
            nodeType = typeCheckUnknown(s);
            return nodeType;
        } else {
            nodeType = typeCheckArray(s);
            return nodeType;
        }
    }

    public String toString(){
        return values.toString();
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        if (raw == null){
            p.startList();
            for (Expr v: values){
                (v).prettyPrint(p);
            }
            p.endList();
        }else{
            p.printAtom("\"" +raw+ "\"");
        }
    }

    @Override
    public IRExpr accept(IRVisitor visitor) {
        return visitor.visit(this);
    }
}