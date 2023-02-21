package aar226_akc55_ayc62_ahl88.newast.expr;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/**
 * Class for length call
 */
public class Length extends Expr {
    Expr arg;

    /**
     * @param e Expression argument of length()
     * @param l line number
     * @param c column number
     */
    public Length(Expr e, int l, int c) {
        super(l,c);
        arg = e;
    }
    @Override
    public void prettyPrint(CodeWriterSExpPrinter p){
        p.startList();
        p.printAtom("length");
        arg.prettyPrint(p);
        p.endList();
    }

    // TypeCheck Arg is Array
    @Override
    public Type typeCheck(SymbolTable<Type> table) throws Error {
        Type.TypeCheckingType t1;
        String message;
        try {
            t1 = arg.typeCheck(table).getType();
        }
        catch (Error e) {
            message = Integer.toString(getLine())
                    + ":" + Integer.toString(getColumn())
                    + "  TypeError: unbound variable name";
            throw new Error(message);
        }
        if ((t1 == Type.TypeCheckingType.INTARRAY) ||
                (t1 == Type.TypeCheckingType.BOOLARRAY) ||
                (t1 == Type.TypeCheckingType.EMPTYDIMENSIONALARRAY) ||
                (t1 == Type.TypeCheckingType.FILLEDARR))
        {
            return (new Type(Type.TypeCheckingType.INT));
        } else {
            message = Integer.toString(getLine())
                    + ":" + Integer.toString(getColumn())
                    + "  TypeError: Invalid length arg type";
            throw new Error(message);
        }
    }
}
