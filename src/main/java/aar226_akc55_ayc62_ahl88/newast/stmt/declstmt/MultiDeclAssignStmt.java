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

        // unpack deez nuts
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

            // multi literal assign
            if (expressions.size() != 1) {
                throw new Error(expressions.get(0).getLine() + ":" + expressions.get(0).getColumn() +
                        " Semantic error: Cannot unpack " + declarationTypes.size() + " into " + exprTypes.size());
            }

            // function multi assign
            else {
                throw new Error(decls.get(0).getLine() + ":" + decls.get(0).getColumn() +
                        " Semantic error: Mismatched number of values for func multi ass");
            }
        }
        for (int i = 0; i < exprTypes.size(); i++) {
            Type decT = declarationTypes.get(i);
            Type exprT = exprTypes.get(i);
            if (!decT.sameType(exprT)){
                throw new Error(decls.get(i).getLine() + ":" + decls.get(i).getColumn() + " Semantic error: Variable Type doesn't match Expr Type");
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
