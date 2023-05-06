package aar226_akc55_ayc62_ahl88.newast.stmt;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

/**
 * While Class for AST node
 */
public class While extends Stmt {
    private Expr guard;
    private Stmt stmt;

    /**
     * @param expr Expression
     * @param s Statement
     * @param l line number
     * @param c colunn number
     */
    public While(Expr expr, Stmt s, int l, int c) {
        super(l,c);
        guard = expr;
        stmt = s;
    }

    @Override
    public Type typeCheck(SymbolTable<Type> table) {
        Type tg = guard.typeCheck(table);
        if (tg.getType() != Type.TypeCheckingType.BOOL) {
            throw new SemanticError(guard.getLine() ,guard.getColumn() ,"guard is not bool");
        }

        table.enterScope();
        // Save old var
        Boolean oldParentLoop = table.parentLoop;
        table.parentLoop = true;
        // Typecheck stmts
        Type cond1 = stmt.typeCheck(table);
        // Restore old var
        table.parentLoop = oldParentLoop;
        table.exitScope();

        if (!isRType(cond1)){
            throw new SemanticError(stmt.getLine() ,stmt.getColumn() ,"Statement in WHILE is not Unit or Void");
        }
        nodeType = new Type(Type.TypeCheckingType.UNIT);
        return nodeType;
    }

    @Override
    public IRStmt accept(IRVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("while");
        guard.prettyPrint(p);
        stmt.prettyPrint(p);
        p.endList();
    }

    public Expr getGuard() {
        return guard;
    }

    public Stmt getStmt() {
        return stmt;
    }
}
