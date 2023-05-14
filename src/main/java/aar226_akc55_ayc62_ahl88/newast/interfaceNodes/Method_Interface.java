package aar226_akc55_ayc62_ahl88.newast.interfaceNodes;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.Errors.SyntaxError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.AstNode;
import aar226_akc55_ayc62_ahl88.newast.Dimension;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.declarations.AnnotatedTypeDecl;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Class for method interfaces.
 */
public class Method_Interface extends AstNode {
    private Id id;
    private ArrayList<AnnotatedTypeDecl> decls;
    public ArrayList<Type> types;
    public Type recordType;
    public boolean isRecord;

    public ArrayList<AnnotatedTypeDecl> fields;

    /**
     * @param s
     * @param d
     * @param t
     * @param l
     * @param c
     */
    public Method_Interface(String s, ArrayList<AnnotatedTypeDecl> d, ArrayList<Type> t,int l, int c){
        super(l,c);
        for (AnnotatedTypeDecl cur: d){
            if (!cur.type.dimensions.allEmpty) {
                throw new SyntaxError(cur.getLine() ,cur.getColumn(),"array in param list has init value");
            }
        }
        for (Type cur: t){
            if (!cur.dimensions.allEmpty) {
                throw new SyntaxError(cur.getLine() , cur.getColumn() ,"array in type list has init value");
            }
        }
        id = new Id(s,l,c);
        decls = d;
        types = t;
        isRecord = false;
        recordType = null;
    }

    /**
     * @param s
     * @param fieldInputs
     * @param l
     * @param c
     */
    public Method_Interface(String s, ArrayList<AnnotatedTypeDecl> fieldInputs, int l, int c) {
        super(l, c);
        isRecord = true;
        id = new Id(s, l, c);
        decls = new ArrayList<>();
        types = new ArrayList<>();
        Dimension emptyDim = new Dimension(0, l, c);
        recordType = new Type(s, emptyDim, l, c);
        fields = fieldInputs;
    }

    public String toString(){
        String build = "";
        build +=  "( " + id.toString() + " " + decls.toString() + " " + types.toString() +  " )";
        return build;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        if (isRecord){
            p.startUnifiedList();
            id.prettyPrint(p);

            p.startList();
            for (AnnotatedTypeDecl field : fields) field.prettyPrint(p);
            p.endList();

            p.endList();
        }else {
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
    }

    /**
     * @param resultingGlobals
     * @param methods
     * @return
     */
    public Type typeCheck(HashMap<Id, Type> resultingGlobals, SymbolTable<Type> methods) {

        HashSet<String> prev = new HashSet<>();

        for (AnnotatedTypeDecl atd: decls){
            if (atd.identifier.toString().equals(id.toString())){ // decl name and function name
                throw new SemanticError(atd.getLine(), atd.getColumn(), "function and parameter have same name");
            }
            Type curDeclType = atd.typeCheck(methods);
            if (prev.contains(atd.identifier.toString())){
                throw new SemanticError(atd.getLine(), atd.getColumn(),"paramter with same name in list");
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