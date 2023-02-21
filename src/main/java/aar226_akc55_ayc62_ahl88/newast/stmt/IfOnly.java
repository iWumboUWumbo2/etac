package aar226_akc55_ayc62_ahl88.newast.stmt;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/**
 * Class for If-Statement without Else
 */
public class IfOnly extends Stmt {
    private Expr guard;
    private Stmt ifState;

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
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("if");
        guard.prettyPrint(p);
        ifState.prettyPrint(p);
        p.endList();
    }
    @Override
    public Type typeCheck(SymbolTable table) {

        Type guardType = guard.typeCheck(table);
        if (guardType.getType() != Type.TypeCheckingType.BOOL){
            throw new Error(guard.getLine() + ":" + guard.getColumn() + " Semantic error:  guard is not bool");
        }
        Type cond1 = ifState.typeCheck(table);
        if (cond1.getType() !=Type.TypeCheckingType.UNIT && cond1.getType() !=Type.TypeCheckingType.VOID){
            throw new Error(ifState.getLine() + ":" + ifState.getColumn() + " Semantic error: Statement in If is not Unit or Void");
        }
        return new Type(Type.TypeCheckingType.UNIT);
    }

}
