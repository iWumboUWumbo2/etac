package aar226_akc55_ayc62_ahl88.newast.expr;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRExpr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.visitors.ContainsBreakVisitor;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

/**
 * Class for boolean literals.
 */
public class BoolLiteral extends Expr {
    public boolean boolVal;

    /**
     * @param inputBool
     * @param l
     * @param c
     */
    public BoolLiteral(boolean inputBool,int l, int c) {
        super(l,c);
        boolVal = inputBool;
    }

    public boolean getBoolVal(){
        return boolVal;
    }

    @Override
    public Type typeCheck(SymbolTable s){
        nodeType = new Type(Type.TypeCheckingType.BOOL);
        return nodeType;
    }
    public String toString() {
        return Boolean.toString(boolVal);
    }

    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.printAtom(toString());
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
