package aar226_akc55_ayc62_ahl88.newast.declarations;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.AstNode;
import aar226_akc55_ayc62_ahl88.newast.Dimension;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

/**
 * Abstract Class for Declaration Statements
 * works for a:int
 * works for a
 * works for _
 * works for a[2][4]
 */

public abstract class Decl extends AstNode {

    /**
     * Constructor for Abstract Declarations
     * @param l line number
     * @param c column number
     */

    public Id identifier;
    public Decl(Id i , int l, int c) {
        super(l, c);
        identifier = i;
    }

    /**
     * @param t original type
     * @param d new dimensions
     * @param table typechecking table
     * @return new type with dimension d
     */
    public Type correctType(Type t, Dimension d, SymbolTable<Type> table) {
        Type temp;
        if (t.isRecord()) {
            Type recordType = table.lookup(new Id(t.recordName, getColumn(), getLine()));
            temp = new Type(recordType.recordName, recordType.recordFieldTypes, t.getColumn(), t.getLine());
            temp.recordFieldToIndex = recordType.recordFieldToIndex;
            temp.setType(Type.TypeCheckingType.RECORD);
            temp.dimensions = d;
        } else if (t.isRecordArray() && d.getDim() == 0) {
            Type recordType = table.lookup(new Id(t.recordName, getLine(),getColumn()));
            temp = new Type(recordType.recordName, recordType.recordFieldTypes, t.getColumn(), t.getLine());
            temp.recordFieldToIndex = recordType.recordFieldToIndex;
            temp.setType(Type.TypeCheckingType.RECORD);
            temp.dimensions = d;
        } else if (t.isRecordArray() && d.getDim() != 0) {
            Type recordType = table.lookup(new Id(t.recordName, getLine(),getColumn()));
            temp = new Type(recordType.recordName, recordType.recordFieldTypes, t.getColumn(), t.getLine());
            temp.recordFieldToIndex = recordType.recordFieldToIndex;
            temp.dimensions = d;
            temp.setType(Type.TypeCheckingType.RECORDARRAY);
        } else if (t.isIntArray() && d.getDim() == 0) {
            temp = new Type(Type.TypeCheckingType.INT);
        } else if (t.isBoolArray() && d.getDim() == 0) {
            temp = new Type(Type.TypeCheckingType.BOOL);
        } else {
            temp = new Type(t.getType(), d);
        }
        return temp;
    }

    public abstract void prettyPrint(CodeWriterSExpPrinter p);

    public abstract Type typeCheck(SymbolTable<Type> table);

    public abstract IRNode accept(IRVisitor visitor);

    public Id getIdentifier() {
        return identifier;
    }
}

