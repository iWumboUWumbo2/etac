package aar226_akc55_ayc62_ahl88.newast.stmt;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.declarations.AnnotatedTypeDecl;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

/**
 * Class for break.
 */
public class Break extends Stmt {

    /**
     * @param l
     * @param c
     */
    public Break(int l, int c) {
        super(l, c);
    }
    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.printAtom("break");
    }

    @Override
    public Type typeCheck(SymbolTable<Type> table) {
        boolean isInLoop = table.parentLoop;
        if (!isInLoop) {
            throw new SemanticError(getLine(),getColumn(),"Break not in loop.");
        }
        return new Type(Type.TypeCheckingType.UNIT);
    }

    @Override
    public IRStmt accept(IRVisitor visitor) {
        return visitor.visit(this);
    }
}
