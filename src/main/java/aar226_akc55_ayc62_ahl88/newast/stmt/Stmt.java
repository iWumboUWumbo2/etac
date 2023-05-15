package aar226_akc55_ayc62_ahl88.newast.stmt;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.AstNode;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;
import aar226_akc55_ayc62_ahl88.visitors.ContainsBreakVisitor;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

/**
 * Abstract class for all statements
 */
public abstract class Stmt extends AstNode {


    /**
     * @param l line number
     * @param c column number
     */
    public Stmt(int l, int c) {
        super(l, c);
    }

    public Type lub(Type R1, Type R2) {
        if (R1.getType() == R2.getType()) {
            return R1;
        }

        if (R1.getType() == Type.TypeCheckingType.UNIT || R2.getType() == Type.TypeCheckingType.UNIT) {
            return new Type(Type.TypeCheckingType.UNIT);
        }

        throw new SemanticError(getLine(),getColumn(), "Yabai");
    }

    public abstract void prettyPrint(CodeWriterSExpPrinter p);
    public abstract Type typeCheck(SymbolTable<Type> table);
    public abstract IRStmt accept(IRVisitor visitor);
    public abstract Boolean accept(ContainsBreakVisitor v);
    public boolean isRType(Type t){
        return t.getType() == Type.TypeCheckingType.UNIT || t.getType() ==Type.TypeCheckingType.VOID;
    }
}
