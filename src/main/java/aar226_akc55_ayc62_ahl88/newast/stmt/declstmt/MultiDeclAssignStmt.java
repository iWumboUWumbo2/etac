package aar226_akc55_ayc62_ahl88.newast.stmt.declstmt;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.declarations.AnnotatedTypeDecl;
import aar226_akc55_ayc62_ahl88.newast.declarations.Decl;
import aar226_akc55_ayc62_ahl88.newast.declarations.UnderScore;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.stmt.Stmt;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

public class MultiDeclAssignStmt extends Stmt {
    private ArrayList<Decl> decls;
    private ArrayList<Expr> expressions;

    public MultiDeclAssignStmt(ArrayList<Decl> d, ArrayList<Expr> e,int l, int c) {
        super(l,c);
        decls = d;
        for (Decl dec: d){
            if (dec instanceof AnnotatedTypeDecl){
                AnnotatedTypeDecl cast = (AnnotatedTypeDecl) dec;
                if (!cast.type.dimensions.allEmpty) {
                    throw new Error(cast.getLine() + ":" + cast.getColumn() +  " error: array in param list has init value");
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
    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
            p.startList();
            p.printAtom("=");
            p.startList();
            decls.forEach(e -> e.prettyPrint(p));
            p.endList();
            expressions.forEach(e -> e.prettyPrint(p));
            p.endList();
    }

    @Override
    public Type typeCheck(SymbolTable<Type> table) {
        ArrayList<Type> declarationTypes = new ArrayList<>();
        ArrayList<Type> exprTypes = new ArrayList<>();
        for (Decl d: decls){
            Type curDeclType = d.typeCheck(table);
            declarationTypes.add(curDeclType);
        }
        for (Expr e: expressions){
            Type curExprType = e.typeCheck(table);
            if (curExprType.getType() == Type.TypeCheckingType.MULTIRETURN) {
                exprTypes.addAll(curExprType.multiTypes);
            }
            else {
                exprTypes.add(curExprType);
            }
        }

        if ( declarationTypes.size() != exprTypes.size()) {
            throw new Error(getLine() + ":" + getColumn() + " Semantic error:  Number of variable decls doesn't match number of expressions");
        }
        for (int i = 0; i < exprTypes.size(); i++) {
            Type decT = declarationTypes.get(i);
            Type exprT = exprTypes.get(i);
            if (!decT.sameType(exprT)){
                throw new Error(getLine() + ":" + getColumn() + " Semantic error: Variable Type doesn't match Expr Type");
            }
        }
        // add these multi declarations to
        for (int i = 0; i< decls.size();i++){
            Decl curD = decls.get(i);
            if (curD instanceof AnnotatedTypeDecl){
                table.add(curD.identifier,declarationTypes.get(i));
            }
        }
        return new Type(Type.TypeCheckingType.UNIT);
    }
}
