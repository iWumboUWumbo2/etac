package aar226_akc55_ayc62_ahl88.newast.definitions;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.Errors.SyntaxError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.*;
import aar226_akc55_ayc62_ahl88.newast.declarations.AnnotatedTypeDecl;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.newast.declarations.Decl;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.newast.stmt.Block;
import java.util.ArrayList;
import java.util.HashSet;

public class Method extends Definition {
    private Id id;
    private ArrayList<AnnotatedTypeDecl> decls;
    private ArrayList<Type> types;
    private Block block;

    public Method(String s, ArrayList<AnnotatedTypeDecl> d, ArrayList<Type> t, Block b, int l, int c){
        super(l,c);
        for (AnnotatedTypeDecl cur: d){
            if (!cur.type.dimensions.allEmpty) {
                throw new SyntaxError(cur.getLine(), cur.getColumn(), "array in param list has init value");
            }
        }
        for (Type cur: t){
            if (!cur.dimensions.allEmpty) {
                throw new SyntaxError(cur.getLine() ,cur.getColumn() ," error: array in type list has init value");
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
//        System.out.println("in method");
        table.enterScope();
        Id old = table.currentParentFunction;
        table.currentParentFunction = id;
        for (AnnotatedTypeDecl atd: decls){
            if (id.toString().equals(atd.identifier.toString())){
                throw new SemanticError(getLine(),getColumn(),"parameter same name as method");
            }
            if (table.contains(atd.identifier)){
                throw new SemanticError(getLine(),getColumn(),"parameter already present");
            }
            table.add(atd.identifier,atd.type);
        }
        Type blockType = block.typeCheck(table);
        table.currentParentFunction = old;
        table.exitScope();

//        System.out.println(getOutputtypes().size());

        // if is function, check if return void
        if (getOutputtypes().size() != 0) {
            if (blockType.getType() != Type.TypeCheckingType.VOID)
                throw new SemanticError(
                    block.getLine(),
                    block.getColumn(),
                    "no return type detected"
                );
        }
        return new Type(Type.TypeCheckingType.UNIT);
    }

    @Override
    public Type firstPass(SymbolTable<Type> table, HashSet<String> currentFile) {
        table.enterScope();
        Id old = table.currentParentFunction;
        table.currentParentFunction = id;
        for (AnnotatedTypeDecl atd: decls){
            if (id.toString().equals(atd.identifier.toString())){
                throw new SemanticError(getLine(),getColumn(),"paramter same name as method");
            }
            if (table.contains(atd.identifier)){
                throw new SemanticError(getLine(),getColumn(),"parameter already present");
            }
            table.add(atd.identifier,atd.type);
        }
        table.currentParentFunction = old;
        table.exitScope();
        Type methodType = new Type(getInputTypes(),getOutputtypes());
        if (currentFile.contains(id.toString())){
            throw new SemanticError(getLine(), getColumn(), "Current File has same identifier");
        }
        currentFile.add(id.toString());
        if (table.contains(id)){
            Type rhs = table.lookup(id);
            if (rhs.getType() != Type.TypeCheckingType.FUNC){
                throw new  SemanticError(getLine(),getColumn(),"Another declaration in table that isn't method");
            }
            if (!methodType.isSameFunc(rhs)) {
                throw new SemanticError(getLine(), getColumn(), " Duplicate Function not exact same");
            }
        }else {
            table.add(id, methodType);
        }
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
