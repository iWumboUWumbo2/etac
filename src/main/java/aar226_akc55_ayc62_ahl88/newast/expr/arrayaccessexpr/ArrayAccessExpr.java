package aar226_akc55_ayc62_ahl88.newast.expr.arrayaccessexpr;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Dimension;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRExpr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.visitors.ContainsBreakVisitor;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

import java.util.ArrayList;
/**
 * Class for Array Accessing
 */
public class ArrayAccessExpr extends Expr {

    // Type Check that it is an Actual Array
    Expr orgArray;
    // Read This Left to Right
    ArrayList<Expr> indicies;
    public Expr getOrgArray() {
        return orgArray;
    }
    public ArrayList<Expr> getIndicies() {
        return indicies;
    }

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

        // allow array accesses to unknown
        if (e.getType() == Type.TypeCheckingType.UNKNOWN) {
            nodeType = new Type(Type.TypeCheckingType.UNKNOWN);
            return nodeType;
        }

        // throw error if arg is not array
        if (!e.isArray()) {
            throw new SemanticError(orgArray.getLine(), orgArray.getColumn(), "type is not indexable");
        } else {
            long return_dim = e.dimensions.getDim() - indicies.size();
            Dimension new_dim = new Dimension(return_dim, getLine(), getColumn());
            // throw error if length of access indices > arg dimension
            if (return_dim < 0) {
                // allow infinite access to unknown array
                if (e.getType() == Type.TypeCheckingType.UNKNOWNARRAY) {
                    nodeType = new Type(Type.TypeCheckingType.UNKNOWN);
                    return nodeType;
                }
                throw new SemanticError(orgArray.getLine(), orgArray.getColumn(), "array index size mismatch");
            } else {
                nodeType = correctType(e, new_dim, s);
                return nodeType;
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
        if (t.isRecordArray() && d.getDim() == 0) {
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
        } else if (t.isUnknown() && d.getDim() == 0) {
            temp = new Type(Type.TypeCheckingType.UNKNOWN);
        }else {
            temp = new Type(t.getType(), d);
        }
        return temp;
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
    @Override
    public Boolean accept(ContainsBreakVisitor v) {
        return v.visit(this);
    }
    @Override
    public IRExpr accept(IRVisitor visitor) {
        return visitor.visit(this);
    }
}
