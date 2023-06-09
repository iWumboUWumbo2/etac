package aar226_akc55_ayc62_ahl88.newast.stmt;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;
import aar226_akc55_ayc62_ahl88.visitors.ContainsBreakVisitor;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

/**
 * Class for If-Statement without Else
 */
public class IfOnly extends Stmt {
    public Expr guard;
    public Stmt ifState;

    /**
     * @param e Expression
     * @param ifS If Statement
     * @param l Line number
     * @param c Column number
     */
    public IfOnly(Expr e, Stmt ifS, int l, int c) {
        super(l, c);
        guard = e;
        ifState = ifS;
    }

    @Override
    public Type typeCheck(SymbolTable<Type> table) {

        Type guardType = guard.typeCheck(table);
        if (guardType.getType() != Type.TypeCheckingType.BOOL){
            throw new SemanticError(guard.getLine() ,guard.getColumn() ,"guard is not bool");
        }

        table.enterScope();
        Type cond1 = ifState.typeCheck(table);
        table.exitScope();

        if (!isRType(cond1)){
            throw new SemanticError(ifState.getLine() , ifState.getColumn() ,"Statement in If is not Unit or Void");
        }
        nodeType = new Type(Type.TypeCheckingType.UNIT);
        return nodeType;
    }

    @Override
    public IRStmt accept(IRVisitor visitor) {
        return visitor.visit(this);
    }
    @Override
    public Boolean accept(ContainsBreakVisitor v) {
        return v.visit(this);
    }
    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("if");
        guard.prettyPrint(p);
        ifState.prettyPrint(p);
        p.endList();
    }
}
