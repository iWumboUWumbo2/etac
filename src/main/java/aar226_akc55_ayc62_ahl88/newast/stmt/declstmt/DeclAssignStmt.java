package aar226_akc55_ayc62_ahl88.newast.stmt.declstmt;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.stmt.*;
import aar226_akc55_ayc62_ahl88.newast.declarations.*;
import aar226_akc55_ayc62_ahl88.newast.expr.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;
import aar226_akc55_ayc62_ahl88.visitors.ContainsBreakVisitor;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

/**
 * Class for Declarations that deal with assignment
 */
public class DeclAssignStmt extends Stmt{

    private Decl decl;
    private Expr expression;
    public Decl getDecl() {
        return decl;
    }
    public Expr getExpression() {
        return expression;
    }

    /**
     * @param d Declaration
     * @param e Expression
     * @param l Line number
     * @param c Column number
     */
    public DeclAssignStmt (Decl d, Expr e, int l, int c) {
        super(l,c);
        decl = d;
        expression = e;
    }

    @Override
    public Type typeCheck(SymbolTable<Type> table) {
        Type declType = decl.typeCheck(table);
        Type exprType = expression.typeCheck(table);
        if (!declType.sameType(exprType)) {
            throw new SemanticError(expression.getLine(), expression.getColumn(),"expression type not the same as declaration type");
        }
        if (decl instanceof AnnotatedTypeDecl) {
            table.add(decl.identifier, declType); // add the identifier and the type only if its a new type
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
        p.printAtom("=");
        decl.prettyPrint(p);
        expression.prettyPrint(p);
        p.endList();
    }

    public String toString(){
        String build = "";
        build +=  "( " + decl.toString() +  " )";
        return build;
    }

}
