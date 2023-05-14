package aar226_akc55_ayc62_ahl88.newast.definitions;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.Errors.SyntaxError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.declarations.AnnotatedTypeDecl;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Class for multiple global decls.
 */
public class MultiGlobalDecl extends Definition{
    private ArrayList<AnnotatedTypeDecl> decls;
    private ArrayList<Expr> expressions;

    /**
     * @param d
     * @param e
     * @param left
     * @param right
     */
    public MultiGlobalDecl (ArrayList<AnnotatedTypeDecl > d, ArrayList<Expr> e, int left, int right) {
        super(left,right);
        decls = d;
        if (e.size() != 0) {
            for (AnnotatedTypeDecl de : decls) {
                if (!de.type.dimensions.allEmpty) {
                    throw new SyntaxError(left, right, "array with init len no Val");
                }
                if ((de.type.dimensions.getDim() != 0)) {
                    throw new SyntaxError(left, right, "array can't have gets");
                }
            }
        }
        expressions = e;
    }

    public String toString(){
        String build = "";
        build +=  "( " + decls.toString() +  " )";
        return build;
    }

    public ArrayList<AnnotatedTypeDecl> getDecls() {
        return decls;
    }

    public ArrayList<Expr> getExpressions() {
        return expressions;
    }

    /**
     * @param table
     * @param currentFile
     * @return Type
     */
    @Override
    public Type firstPass(SymbolTable<Type> table, HashSet<String> currentFile) {
        if ((decls.size() != expressions.size()) && (expressions.size() != 0)) {
            throw new SemanticError(getLine(),getColumn(),"size of declarations dont match expressions");
        }
        for (AnnotatedTypeDecl atd: decls){
            Type curDeclType = atd.typeCheck(table);
            if (table.contains(atd.identifier)){
                throw new SemanticError(getLine(),getColumn(),"decl is already defined");
            }
            if (currentFile.contains(atd.identifier.toString())){
                throw new SemanticError(getLine(), getColumn(), "Current File has same identifier");
            }
            if (atd.type.isRecord || atd.type.isRecordArray()) {
                if (!table.contains(atd.type.recordName)) {
                    throw new SemanticError(getLine(), getColumn(), "undefined record type");
                }
            }
            currentFile.add(atd.identifier.toString());
            table.add(atd.identifier,curDeclType);
        }

        nodeType = new Type(Type.TypeCheckingType.UNIT);
        return nodeType;
    }
    @Override
    public Type typeCheck(SymbolTable<Type> table) {
        nodeType = new Type(Type.TypeCheckingType.UNIT);
        return nodeType;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        if (expressions.size() != 0) p.printAtom("=");
        if (expressions.size() != 0) p.startList();
        decls.forEach(e -> e.prettyPrint(p));
        if (expressions.size() != 0) p.endList();
        expressions.forEach(e -> e.prettyPrint(p));
        p.endList();
    }
    @Override
    public IRNode accept(IRVisitor visitor) {
        return visitor.visit(this);
    }
}
