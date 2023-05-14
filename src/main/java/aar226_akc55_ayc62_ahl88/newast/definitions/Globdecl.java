package aar226_akc55_ayc62_ahl88.newast.definitions;

import aar226_akc55_ayc62_ahl88.Errors.SyntaxError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.declarations.*;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRData;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

import java.util.HashSet;

/**
 * Description for global declaration
 */
public class Globdecl extends Definition {
    private AnnotatedTypeDecl decl;
    private Expr value;

    public AnnotatedTypeDecl getDecl() {
        return decl;
    }

    public Expr getValue() {
        return value;
    }

    /**
     * @param d Declaration
     * @param v Value
     * @param l Line number
     * @param c Column number
     */
    public Globdecl (AnnotatedTypeDecl d, Expr v, int l, int c) {
        super(l, c);
        decl = d;
        if (!decl.type.dimensions.allEmpty){
            throw new SyntaxError(l, c ,"global array can't be initalized");
        }
        value = v;
    }

    public String toString(){
        String build = "";
        if (value != null){
//            System.out.println("IM HERE");
            build +=  "( " + decl.toString() + " " +value.toString() +  " )";
        }else{
            build +=  "( " + decl.toString() +  " )";
        }
        return build;
    }
    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom(":global");
        decl.identifier.prettyPrint(p);
        decl.type.prettyPrint(p);
        if (value != null){
            value.prettyPrint(p);
        }
        p.endList();
    }

    @Override
    public Type firstPass(SymbolTable<Type> table, HashSet<String> currentFile) {
        if (table.contains(decl.identifier)){
            throw new SemanticError(getLine(), getColumn(), "global decl not same type");
        }
        if (currentFile.contains(decl.identifier.toString())){
            throw new SemanticError(getLine(), getColumn(), "Current File has same identifier");
        }
        if (decl.type.isRecord || decl.type.isRecordArray()) {
            if (!table.contains(decl.type.recordName)) {
                throw new SemanticError(getLine(), getColumn(), "undefined record type");
            }
        }
        Type declType = decl.type;
        if (value != null) {
            Type valueType = value.typeCheck(table);
            if (!declType.sameType(valueType)) {
                throw new SemanticError(getLine(), getColumn(),"global decl not same type");
            }
        }
        currentFile.add(decl.identifier.toString());
        table.add(decl.identifier,declType);

        nodeType = new Type(Type.TypeCheckingType.UNIT);
        return nodeType;
    }

    @Override
    public Type typeCheck(SymbolTable<Type> table) {
        if (nodeType == null) {
            nodeType = new Type(Type.TypeCheckingType.UNIT);
        }
        return nodeType;
    }

    @Override
    public IRNode accept(IRVisitor visitor) {
        return visitor.visit(this);
    }
}
