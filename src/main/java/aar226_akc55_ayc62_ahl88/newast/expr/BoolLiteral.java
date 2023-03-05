package aar226_akc55_ayc62_ahl88.newast.expr;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

public class BoolLiteral extends Expr {
    public boolean boolVal;

    public BoolLiteral(boolean inputBool,int l, int c) {
        super(l,c);
        boolVal = inputBool;
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
    public IRNode accept(IRVisitor visitor) {
        return visitor.visit(this);
    }
}
