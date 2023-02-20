package aar226_akc55_ayc62_ahl88.newast.stmt.declstmt;


import aar226_akc55_ayc62_ahl88.newast.stmt.*;
import aar226_akc55_ayc62_ahl88.newast.declarations.*;
import aar226_akc55_ayc62_ahl88.newast.expr.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/**
 * Class for Declarations that deal with assignment
 */
public class DeclAssignStmt extends Stmt{

    private Decl decl;
    private Expr expression;

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

    public String toString(){
        String build = "";
        build +=  "( " + decl.toString() +  " )";
        return build;
    }
    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("=");
        decl.prettyPrint(p);
        expression.prettyPrint(p);
        p.endList();

    }

}
