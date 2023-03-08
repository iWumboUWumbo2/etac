package aar226_akc55_ayc62_ahl88.newast.declarations;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRExpr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

/**
 * Class for UnderScore Declaration
 */
public class UnderScore extends Decl{
    /**
     * Constructor for UnderScore Declaration
     * @param l line number
     * @param c column number
     */
    public UnderScore(int l, int c) {
        super(new Id("_",l,c),l, c);
    }

    @Override
    public Type typeCheck(SymbolTable<Type> table) {
        nodeType = new Type(Type.TypeCheckingType.UNDERSCORE);
        return nodeType;
    }

    @Override
    public IRExpr accept(IRVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.printAtom("_");
    }
}
