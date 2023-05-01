package aar226_akc55_ayc62_ahl88.newast.expr.binop;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRExpr;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

public class RecordAcessBinop extends BinopExpr{

    /**
     * @param in1 left Expression
     * @param in2 right Expression
     * @param l line number
     * @param c column number
     */
    public RecordAcessBinop(Expr in1, Id in2, int l, int c) {
        super(BinopEnum.PERIOD, in1, in2, l, c);
    }
    @Override
    public Type typeCheck(SymbolTable s){
        Expr e1 = getLeftExpr();
        Expr e2 = getRightExpr();
        Type t1 = e1.typeCheck(s);

        // Doulbe check unknown part a:Point[] = {}

        //b:Point = {}[0]
        if (t1.getType() != Type.TypeCheckingType.RECORD) {
            throw new SemanticError(e1.getLine(), e1.getColumn(), "statements block must be of type record at");
        }

        if (!(e2 instanceof Id i)) {
            throw new SemanticError(e2.getLine(), e2.getColumn(), "statements block must be of type int at");
        }

        // Type should be type of field of record
        if (!t1.recordFieldToIndex.containsKey(i.toString())) {
            throw new SemanticError(e2.getLine(), e2.getColumn(), "Invalid field at ");
        }

        int index = t1.recordFieldToIndex.get(i.toString());
        return t1.recordFieldTypes.get(index);
    }

    @Override
    public IRExpr accept(IRVisitor visitor) {
        return visitor.visit(this);
    }
}
