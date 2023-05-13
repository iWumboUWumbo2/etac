package aar226_akc55_ayc62_ahl88.newast.expr.binop;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Dimension;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRExpr;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

public class RecordAcessBinop extends BinopExpr{

    public String rightId;
    /**
     * @param in1 left Expression
     * @param in2 right Expression
     * @param l line number
     * @param c column number
     */
    public RecordAcessBinop(Expr in1, Id in2, int l, int c) {
        super(BinopEnum.PERIOD, in1, in2, l, c);
    }

    //todo fix null
    @Override
    public Type typeCheck(SymbolTable s){
        Expr e1 = getLeftExpr();
        Expr e2 = getRightExpr();
        Type t1 = e1.typeCheck(s);

        //b:Point = {}[0]
        if (t1.getType() != Type.TypeCheckingType.RECORD) {
            throw new SemanticError(e1.getLine(), e1.getColumn(), "statements block must be of type record at");
        }

        if (!(e2 instanceof Id i)) {
            throw new SemanticError(e2.getLine(), e2.getColumn(), "statements block must be of type int at");
        }

        this.rightId = i.toString();
        Id recordId = new Id(t1.recordName,0,0);

        if (s.contains(recordId)) {
            Type recordType = (Type) s.lookup(recordId);

            if (recordType.recordFieldToIndex.containsKey(rightId)) {
                int index = recordType.recordFieldToIndex.get(this.rightId);
                Type t = recordType.recordFieldTypes.get(index);
                nodeType = t;
                return correctType(t, t.dimensions, s);
            } else {
                throw new SemanticError(e2.getLine(), e2.getColumn(), "Invalid field at ");
            }
        } else {
            throw new SemanticError(e2.getLine(), e2.getColumn(), "Not valid record.");
        }
    }

    /**
     * @param t
     * @param d
     * @param table
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
            return temp;
        } else if (t.isRecordArray() && d.getDim() == 0) {
            Type recordType = table.lookup(new Id(t.recordName, getLine(),getColumn()));
            temp = new Type(recordType.recordName, recordType.recordFieldTypes, t.getColumn(), t.getLine());
            temp.recordFieldToIndex = recordType.recordFieldToIndex;
            temp.setType(Type.TypeCheckingType.RECORD);
            temp.dimensions = d;
            return temp;
        } else if (t.isRecordArray() && d.getDim() != 0) {
            Type recordType = table.lookup(new Id(t.recordName, getLine(),getColumn()));
            temp = new Type(recordType.recordName, recordType.recordFieldTypes, t.getColumn(), t.getLine());
            temp.recordFieldToIndex = recordType.recordFieldToIndex;
            temp.dimensions = d;
            temp.setType(Type.TypeCheckingType.RECORDARRAY);
            return temp;
        } else {
            temp = new Type(t.getType(), d);
            return temp;
        }
    }

    @Override
    public IRExpr accept(IRVisitor visitor) {
        return visitor.visit(this);
    }
}
