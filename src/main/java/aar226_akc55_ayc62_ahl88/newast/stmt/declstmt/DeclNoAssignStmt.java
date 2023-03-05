package aar226_akc55_ayc62_ahl88.newast.stmt.declstmt;


import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.declarations.*;
import aar226_akc55_ayc62_ahl88.newast.expr.*;
import aar226_akc55_ayc62_ahl88.newast.stmt.Stmt;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

/**
 * Class for Declarations that do not deal with assignment
 */
public class DeclNoAssignStmt extends Stmt {

    private Decl decl;

    /**
     * @param d Declaration
     * @param l Line number
     * @param c Column number
     */
    public DeclNoAssignStmt (Decl d, int l, int c) {
        super(l,c);
        decl = d;
    }

    public String toString(){
        String build = "";
//        if (value != null){
////            System.out.println("IM HERE");
//            build +=  "( " + decl.toString() + " " +value.toString() +  " )";
//        }else{
//            build +=  "( " + decl.toString() +  " )";
//        }
        build +=  "( " + decl.toString() +  " )";
        return build;
    }
    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        decl.prettyPrint(p);
    }

    @Override
    public Type typeCheck(SymbolTable<Type> table) {

        Type declType = decl.typeCheck(table);
        if ((decl instanceof AnnotatedTypeDecl)){
            table.add(decl.identifier,declType); // add the identifier and the type only if new type
        }
        nodeType = new Type(Type.TypeCheckingType.UNIT);
        return nodeType;
    }

    @Override
    public IRNode accept(IRVisitor visitor) {
        return visitor.visit(this);
    }

}
