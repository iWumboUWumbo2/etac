package aar226_akc55_ayc62_ahl88.newast.definitions;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.*;
import aar226_akc55_ayc62_ahl88.newast.declarations.AnnotatedTypeDecl;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.newast.declarations.Decl;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.newast.stmt.Block;
import java.util.ArrayList;

public class Method extends Definition {
    private Id id;
    private ArrayList<AnnotatedTypeDecl> decls;
    private ArrayList<Type> types;
    private Block block;

    public Method(String s, ArrayList<AnnotatedTypeDecl> d, ArrayList<Type> t, Block b, int l, int c){
        super(l,c);
        for (AnnotatedTypeDecl cur: d){
            if (!cur.type.dimensions.allEmpty) {
                throw new Error(cur.getLine() + ":" + cur.getColumn() + " error: array in param list has init value");
            }
        }
        for (Type cur: t){
            if (!cur.dimensions.allEmpty) {
                throw new Error(cur.getLine() + ":" + cur.getColumn() + " error: array in type list has init value");
            }
        }
        id = new Id(s, l, c);
        decls = d;
        types = t;
        block = b;
    }

    public String toString(){
        String build = "";
        build +=  "( " + id.toString() + " " + decls.toString() + " " + types.toString() +  " )";
        return build;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startUnifiedList();
        id.prettyPrint(p);

        p.startList();
        for (Decl d : decls) d.prettyPrint(p);
        p.endList();

        p.startList();
        for (Type t : types) t.prettyPrint(p);
        p.endList();

        if (block != null) {
            block.prettyPrint(p);
        }
        p.endList();
    }

    @Override
    public Type typeCheck(SymbolTable<Type> table) {
        table.enterScope();
        for (AnnotatedTypeDecl atd: decls){
            if (id.toString().equals(atd.identifier.toString())){
                throw new SemanticException(getLine(),getColumn(),"error: paramter same name as method");
            }
            if (table.contains(atd.identifier)){
                throw new SemanticException(getLine(),getColumn(),"error: parameter already present");
            }
            table.add(atd.identifier,atd.type);
        }
        table.exitScope();
        return new Type(Type.TypeCheckingType.UNIT);
    }

    @Override
    public Type firstPass(SymbolTable<Type> table) {
        table.enterScope();
        for (AnnotatedTypeDecl atd: decls){
            if (id.toString().equals(atd.identifier.toString())){
                throw new SemanticException(getLine(),getColumn(),"error: paramter same name as method");
            }
            if (table.contains(atd.identifier)){
                throw new SemanticException(getLine(),getColumn(),"error: parameter already present");
            }
            table.add(atd.identifier,atd.type);
        }
        table.exitScope();
        Type methodType = new Type(getInputTypes(),getOutputtypes());
        table.add(id,methodType);
        return new Type(Type.TypeCheckingType.UNIT);
    }
    public ArrayList<Type> getInputTypes(){
        ArrayList<Type> inputTypes = new ArrayList<>();
        for (AnnotatedTypeDecl atd: decls){
            inputTypes.add(atd.type);
        }
        return inputTypes;
    }

    public ArrayList<Type> getOutputtypes(){
        return types;
    }
}
