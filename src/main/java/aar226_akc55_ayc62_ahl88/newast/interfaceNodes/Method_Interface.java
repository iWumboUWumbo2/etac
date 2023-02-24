package aar226_akc55_ayc62_ahl88.newast.interfaceNodes;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.AstNode;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.declarations.AnnotatedTypeDecl;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Method_Interface extends AstNode {
    private Id id;
    private ArrayList<AnnotatedTypeDecl> decls;
    public ArrayList<Type> types;

    public Method_Interface(String s, ArrayList<AnnotatedTypeDecl> d, ArrayList<Type> t,int l, int c){
        super(l,c);
        for (AnnotatedTypeDecl cur: d){
            if (!cur.type.dimensions.allEmpty) {
                throw new Error(cur + ":" + cur.getColumn() + "error: array in param list has init value");
            }
        }
        for (Type cur: t){
            if (!cur.dimensions.allEmpty) {
                throw new Error(cur.getLine() + ":" + cur.getColumn() + "error: array in type list has init value");
            }
        }
        id = new Id(s,l,c);
        decls = d;
        types = t;
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
        for (AnnotatedTypeDecl d : decls) d.prettyPrint(p);
        p.endList();

        p.startList();
        for (Type t : types) t.prettyPrint(p);
        p.endList();

        p.endList();
    }

    public Type typeCheck(HashMap<Id, Type> resultingGlobals, SymbolTable<Type> methods) {

        HashSet<String> prev = new HashSet<>();
        for (AnnotatedTypeDecl atd: decls){
            if (atd.identifier.toString() == id.toString()){ // decl name and function name
                throw new Error("function and paramter have same name");
            }
            Type curDeclType = atd.typeCheck(methods);
            if (prev.contains(atd.identifier.toString())){
                throw new Error("paramter with same name in list");
            }
            prev.add(atd.identifier.toString());
//            inputTypes.add(curDeclType);
        }
        // to do
        return new Type(Type.TypeCheckingType.UNIT);
    }

    public Id getName(){
        return id;
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

