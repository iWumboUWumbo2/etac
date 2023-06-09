package aar226_akc55_ayc62_ahl88.newast.stmt;


import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;
import aar226_akc55_ayc62_ahl88.visitors.ContainsBreakVisitor;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

/**
 * Class for If-Else Statements
 */
public class IfElse extends Stmt {
    private Expr guard;
    private Stmt ifState;
    private Stmt elseState;
    public Expr getGuard() {
        return guard;
    }
    public Stmt getIfState() {
        return ifState;
    }
    public Stmt getElseState() {
        return elseState;
    }

    /**
     * @param e Expression
     * @param ifS If statement
     * @param elseS Else statement
     * @param l Line number
     * @param c Column number
     */
    public IfElse(Expr e, Stmt ifS, Stmt elseS, int l, int c)  {
        super(l, c);
        guard = e;
        ifState = ifS;
        elseState = elseS;
    }

    /**
     * @param table Symbol table
     * @return Type
     */
    @Override
    public Type typeCheck(SymbolTable table) {

        Type tg = guard.typeCheck(table);
        if (tg.getType() != Type.TypeCheckingType.BOOL) {
            throw new SemanticError(guard.getLine() ,guard.getColumn() ,"guard is not bool");
        }
        table.enterScope();
        Type trueClause = ifState.typeCheck(table);
        table.exitScope();

        table.enterScope();
        Type falseClause = elseState.typeCheck(table);
        table.exitScope();

        if (!isRType(trueClause) || !isRType(falseClause)) {
            throw new SemanticError(ifState.getLine() , ifState.getColumn(),"Statement in If is not Unit or Void");
        }

        nodeType = lub(trueClause, falseClause);
        return nodeType;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("if");
        guard.prettyPrint(p);
        ifState.prettyPrint(p);
        elseState.prettyPrint(p);
        p.endList();
    }

    @Override
    public IRStmt accept(IRVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Boolean accept(ContainsBreakVisitor v) {
        return v.visit(this);
    }
}
