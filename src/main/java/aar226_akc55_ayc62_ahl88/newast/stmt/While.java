package aar226_akc55_ayc62_ahl88.newast.stmt;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

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
//        System.out.println("WHILE CONTEXT: \n");
        Type cond1 = stmt.typeCheck(table);
//        if (!(stmt instanceof Block)) {
//            table.printContext();
//        }
//        System.out.println("\nEND WHILE CONTEXT. \n");
        table.exitScope();

        if (!isRType(cond1)){
            throw new SemanticError(stmt.getLine() ,stmt.getColumn() ,"Statement in WHILE is not Unit or Void");
        }
        nodeType = new Type(Type.TypeCheckingType.UNIT);
        return nodeType;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("while");
        guard.prettyPrint(p);
        stmt.prettyPrint(p);
        p.endList();
    }

}
