package aar226_akc55_ayc62_ahl88.newast.expr.arrayliteral;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Dimension;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.Errors.SemanticError;

import java.util.ArrayList;

public class ArrayValueLiteral extends Expr {
    private String raw;
    private ArrayList<Expr> values;

    public ArrayValueLiteral(String s, int l, int c) {
        super(l, c);
        values = new ArrayList<>();
        this.raw = s;
        for (char ch : s.toCharArray()) {
            values.add(new IntLiteral(Character.toString(ch), l, c));
        }
    }

    public ArrayValueLiteral(ArrayList<Expr> e, int l, int c) {
        super(l,c);
        values = e;
        raw = null;
    }

    private Type typeCheckUnknown(SymbolTable s) throws Error {
        return new Type(Type.TypeCheckingType.UNKNOWNARRAY,
                new Dimension(1, getLine(), getColumn())); // unknown dimension
    }


    // guarantess values has length > 0
    private Type typeCheckArray(SymbolTable s) throws Error {
        Type t1 = values.get(0).typeCheck(s);

        Type arrCheck = t1;
        for (Expr e : values) {     // check all elements same type
            Type eType = e.typeCheck(s);
            if (!arrCheck.sameType(eType)) {
                throw new SemanticError(e.getLine(), e.getColumn(), "array element type mismatch");
            }
            if (t1.getType() == Type.TypeCheckingType.UNKNOWNARRAY &&
                    (eType.getType() == Type.TypeCheckingType.BOOLARRAY ||
                            eType.getType() == Type.TypeCheckingType.INTARRAY)){
                arrCheck = eType;
            }
        }

        // if t1 is array, then return multidimensional array lit
        if (t1.isArray()) {
            long dim_num = t1.dimensions.getDim()+1;
            Dimension dim = new Dimension(dim_num, getLine(), getColumn());
            return new Type(arrCheck.getType(), dim);

        }
        // if t1 not array, return dim 1 array
        else {
            Dimension dim = new Dimension(1, getLine(), getColumn());
            if (arrCheck.getType() == Type.TypeCheckingType.INT){
                return new Type(Type.TypeCheckingType.INTARRAY,dim);
            }else if (arrCheck.getType() == Type.TypeCheckingType.BOOL){
                return new Type(Type.TypeCheckingType.BOOLARRAY,dim);
            }else if (arrCheck.getType() == Type.TypeCheckingType.UNKNOWN){
                return new Type(Type.TypeCheckingType.UNKNOWNARRAY,dim);
            }else{
                throw new Error("Not a basic type");
            }
        }

    }

    @Override
    public Type typeCheck(SymbolTable s) throws Error{
//        System.out.println("w hat si going on");
        if (values.size() == 0) {
            return typeCheckUnknown(s);
        } else {
            return typeCheckArray(s);
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
}
