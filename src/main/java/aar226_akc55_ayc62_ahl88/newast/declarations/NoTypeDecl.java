package aar226_akc55_ayc62_ahl88.newast.declarations;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRExpr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

import java.util.ArrayList;


/**
 * Class For Declarations that don't have a type
 * Annotation
 * Ex: a , bob, job etc
 * Not: a:int, bob:bool[]
 */
public class NoTypeDecl extends Decl{

    public ArrayList<Expr> args;
    private Type functionSig;
    public boolean isField;
    /**
     * @param i Identifier Input
     * @param l Line Number
     * @param c Column Number
     */
    public NoTypeDecl(Id i, int l, int c, boolean field) {
        super(i,l, c);
        this.isField = field;
    }

    public NoTypeDecl(Id i, ArrayList<Expr> args, int l, int c, boolean field) {
        super (i, l, c);
        this.args = args;
        this.isField = field;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        if (args != null) {
            p.startList();
            identifier.prettyPrint(p);
            args.forEach(e -> e.prettyPrint(p));
            p.endList();
        } else {
            identifier.prettyPrint(p);
        }
    }

    //todo typecheck?
    @Override
    public Type typeCheck(SymbolTable<Type> table) {
        nodeType = table.lookup(identifier);
//        System.out.println("CHECKING IF FUNC");
        if (args != null && args.size() > 0) {
            functionSig = table.lookup(identifier);


        }
//        System.out.println(nodeType.getType());
        return nodeType;
    }

    public Type getFunctionSig(){
        return functionSig;
    }

    @Override
    public IRExpr accept(IRVisitor visitor) {
        return visitor.visit(this);
    }
}