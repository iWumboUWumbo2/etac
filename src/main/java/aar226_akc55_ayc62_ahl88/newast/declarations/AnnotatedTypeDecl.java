package aar226_akc55_ayc62_ahl88.newast.declarations;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

/**
 * Class For Declarations that do have a type
 * Annotation
 * Ex: a:int, bob:bool[], doom:int[4][3][2]
 */
public class AnnotatedTypeDecl extends Decl{
    /**
     * @param i Identifier
     * @param t Type of Identifier
     * @param l line Number
     * @param c Column Number
     */

    public Type type;
    public AnnotatedTypeDecl(Id i, Type t, int l, int c) {
        super(i, l, c);
        type = t;

    }

    @Override
    public Type typeCheck(SymbolTable<Type> table) {
        if (table.contains(identifier)) {
            throw new SemanticError(getLine(), getColumn(), "variable has already been seen");
        }
        type.dimensions.typeCheck(table); // get side effects

        if (type.isRecord()) {
            nodeType = table.lookup(new Id(type.recordName, getColumn(), getLine()));
            nodeType.dimensions = type.dimensions;
        } else if (type.isRecordArray()) {
            nodeType = table.lookup(new Id(type.recordName, getColumn(), getLine()));
            nodeType.dimensions = type.dimensions;
            nodeType.setType(Type.TypeCheckingType.RECORDARRAY);
        } else {
            nodeType = type;
        }
        type = nodeType;
        return type;
    }

    @Override
    public IRStmt accept(IRVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        identifier.prettyPrint(p);
        type.prettyPrint(p);
        p.endList();
    }
}