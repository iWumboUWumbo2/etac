package aar226_akc55_ayc62_ahl88.newast.stmt;


import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/**
 * Class for If-Else Statements
 */
public class IfElse extends Stmt {
    private Expr guard;
    private Stmt ifState;
    private Stmt elseState;

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

    @Override
    public Type typeCheck(SymbolTable table) {

        Type tg = guard.typeCheck(table);
        if (tg.getType() != Type.TypeCheckingType.BOOL) {
            throw new SemanticError(guard.getLine() ,guard.getColumn() ,"guard is not bool");
        }
        table.enterScope();
//        System.out.println("IF CONTEXT: \n");
        Type trueClause = ifState.typeCheck(table);
//        if (!(ifState instanceof Block)) {
//            table.printContext();
//        }
//        System.out.println("\nEND IF CONTEXT. \n");
        table.exitScope();

        table.enterScope();
//        System.out.println("ELSE CONTEXT: \n");
        Type falseClause = elseState.typeCheck(table);
//        if (!(elseState instanceof Block)) {
//            table.printContext();
//        }
//        System.out.println("\nEND ELSE CONTEXT. \n");
        table.exitScope();

        if (!isRType(trueClause) || !isRType(falseClause)) {
            throw new SemanticError(ifState.getLine() , ifState.getColumn(),"Statement in If is not Unit or Void");
        }

        return lub(trueClause, falseClause);
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
}
