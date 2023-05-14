package aar226_akc55_ayc62_ahl88.newast.stmt.declstmt;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.Errors.SyntaxError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.declarations.AnnotatedTypeDecl;
import aar226_akc55_ayc62_ahl88.newast.declarations.Decl;
import aar226_akc55_ayc62_ahl88.newast.declarations.NoTypeDecl;
import aar226_akc55_ayc62_ahl88.newast.declarations.UnderScore;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.stmt.Stmt;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

import java.util.ArrayList;

/**
 * Class for multiple decl assignments
 */
public class MultiDeclAssignStmt extends Stmt {
    private ArrayList<Decl> decls;
    private ArrayList<Expr> expressions;

    /**
     * @param d
     * @param e
     * @param l
     * @param c
     */
    public MultiDeclAssignStmt(ArrayList<Decl> d, ArrayList<Expr> e,int l, int c) {
        super(l,c);

        for (Decl dec: d) {
            if (dec instanceof AnnotatedTypeDecl) {
                AnnotatedTypeDecl cast = (AnnotatedTypeDecl) dec;
                if (!cast.type.dimensions.allEmpty) {
                    throw new SyntaxError(cast.getLine(), cast.getColumn(), "array in param list has init value");
                }
            }
        }

        expressions = e;

        ArrayList<Decl> decl_list = new ArrayList<>();
        if (e.size() == 0) {    // if e is null, then we have stmt form x, y, z : type
            int last_decl_i = d.size()-1;
            if (!(d.get(last_decl_i) instanceof AnnotatedTypeDecl)) {
                throw new SyntaxError(d.get(last_decl_i).getLine(), d.get(last_decl_i).getColumn(),
                        "invalid variable declaration statement");
            }
            decl_list.add(d.get(last_decl_i));
            Type last_type = ((AnnotatedTypeDecl) d.get(last_decl_i)).type;
            for (int i = 0; i < last_decl_i; i ++) {
                if (!(d.get(i) instanceof NoTypeDecl)) {
                    throw new SyntaxError(d.get(i).getLine(), d.get(i).getColumn(),
                            "invalid variable declaration statement");
                }
                if (!(d.get(i) instanceof UnderScore)) {
                    AnnotatedTypeDecl newdecl = new AnnotatedTypeDecl(d.get(i).identifier, last_type, d.get(i).getLine(), d.get(i).getColumn());
                    decl_list.add(newdecl);
                }
            }
            decls = decl_list;

        } else {
            decls = d;
        }
    }

    public String toString(){
        String build = "";
        build +=  "( " + decls.toString() +  " )";
        return build;
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
    public Type typeCheck(SymbolTable<Type> table) {
        ArrayList<Type> declarationTypes = new ArrayList<>();
        ArrayList<Type> exprTypes = new ArrayList<>();

        // unpack deez nuts
        for (Decl d: decls){
            Type curDeclType = d.typeCheck(table);
            declarationTypes.add(curDeclType);
        }

        if (expressions.size() == 0) {

            for (int i = 0; i < decls.size(); i ++) {
                Decl decl_i = decls.get(i);
                if ((decl_i instanceof AnnotatedTypeDecl)) {
                    table.add(decl_i.identifier, declarationTypes.get(i)); // add the identifier and the type only if new type
                }
            }

            nodeType = new Type(Type.TypeCheckingType.UNIT);
            return nodeType;
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
                throw new SemanticError(expressions.get(0).getLine(), expressions.get(0).getColumn(),
                        " Cannot unpack " + declarationTypes.size() + " into " + exprTypes.size());
            }
            // function multi assign
            else {
                throw new SemanticError(decls.get(0).getLine() ,decls.get(0).getColumn() ,"Mismatched number of values for assign.");
            }
        }
        for (int i = 0; i < exprTypes.size(); i++) {
            Type decT = declarationTypes.get(i);
            Type exprT = exprTypes.get(i);
            if (!decT.sameType(exprT)){
//                System.out.println(decT.getType());
//                System.out.println(exprT.getType());
                throw new SemanticError(decls.get(i).getLine() ,decls.get(i).getColumn() ,"Variable Type doesn't match Expr Type");
            }
        }
        // add these multi declarations to
        for (int i = 0; i< decls.size();i++){
            Decl curD = decls.get(i);
            if (curD instanceof AnnotatedTypeDecl){
                table.add(curD.identifier,declarationTypes.get(i));
            }
        }
        nodeType = new Type(Type.TypeCheckingType.UNIT);
        return nodeType;
    }

    @Override
    public IRStmt accept(IRVisitor visitor) {
        return visitor.visit(this);
    }

    public ArrayList<Decl> getDecls() {
        return decls;
    }

    public ArrayList<Expr> getExpressions() {
        return expressions;
    }
}
