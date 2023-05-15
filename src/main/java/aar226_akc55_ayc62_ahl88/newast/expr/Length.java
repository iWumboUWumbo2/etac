package aar226_akc55_ayc62_ahl88.newast.expr;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRExpr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.visitors.ContainsBreakVisitor;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

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
        Type t1;
        String message;
        t1 = arg.typeCheck(table);

        if (t1.isArray()) {
            nodeType = new Type(Type.TypeCheckingType.INT);
            return nodeType;
        } else {
            throw new SemanticError(getLine(), getColumn(), "Invalid length arg type");
        }
    }
    @Override
    public Boolean accept(ContainsBreakVisitor v) {
        return v.visit(this);
    }
    public Expr getArg() {
        return arg;
    }

    @Override
    public IRExpr accept(IRVisitor visitor) {
        return visitor.visit(this);
    }
}
