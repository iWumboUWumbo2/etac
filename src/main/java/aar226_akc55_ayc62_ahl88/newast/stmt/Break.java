package aar226_akc55_ayc62_ahl88.newast.stmt;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

public class Break extends Stmt {

    public Break(int l, int c) {
        super(l, c);
    }
    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.printAtom("break");
    }

    @Override
    public Type typeCheck(SymbolTable<Type> table) {
        return null;
    }

    @Override
    public IRStmt accept(IRVisitor visitor) {
        return visitor.visit(this);
    }
}
