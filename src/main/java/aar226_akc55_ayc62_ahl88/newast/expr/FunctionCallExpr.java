package aar226_akc55_ayc62_ahl88.newast.expr;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

/**
 * Class for function call expressions
 */
public class FunctionCallExpr extends Expr {
    Id id;
    ArrayList<Expr> args;

    /**
     * @param i function name
     * @param inArgs function arguments
     * @param l line number
     * @param c column number
     */
    public FunctionCallExpr(Id i, ArrayList<Expr> inArgs, int l, int c) {
        super (l, c);
        id = i;
        args = inArgs;
    }

    @Override
    public Type typeCheck(SymbolTable<Type> table) {
        try {
            return table.lookup(id);
        }
        catch (Error e) {
            String message = Integer.toString(getLine())
                    + ":" + Integer.toString(getColumn())
                    + "  TypeError: id not in scope";
            throw new Error(message);
        }
    }

    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        id.prettyPrint(p);
        args.forEach(e -> e.prettyPrint(p));
        p.endList();

    }
}
