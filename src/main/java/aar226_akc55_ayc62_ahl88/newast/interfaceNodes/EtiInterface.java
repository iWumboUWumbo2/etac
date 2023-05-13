package aar226_akc55_ayc62_ahl88.newast.interfaceNodes;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.Errors.SyntaxError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.AstNode;
import aar226_akc55_ayc62_ahl88.newast.Dimension;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.Use;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class EtiInterface extends AstNode {
    private ArrayList<Method_Interface> methods_inter;
    private ArrayList<Use> useList;

    public EtiInterface(ArrayList<Method_Interface> mi,int l, int c) {
        super(l,c);
        this.methods_inter = mi;
        useList = new ArrayList<>();
        if (mi.size() == 0){
            throw new SyntaxError(1,1,"no method definitions");
        }
    }

    public EtiInterface(ArrayList<Use> useList, ArrayList<Method_Interface> mi,int l, int c) {
        super(l,c);
        this.methods_inter = mi;
        this.useList = useList;
        if (mi.size() == 0){
            throw new SyntaxError(1,1,"no method definitions");
        }
    }

    // need pretty
    public String toString() {
        String build = "";
        build += methods_inter.toString();
        return build;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startUnifiedList();
        p.startUnifiedList();
        methods_inter.forEach(d -> d.prettyPrint(p));
        p.endList();
        p.endList();
    }

    public HashMap<Id,Type> firstPass(String zhenFileName, HashMap<Id,Type> res, ArrayList<Id> useInterfaceMethods,
                                      ArrayList<String> visitedInterfaces) {
        HashSet<String> methodName= new HashSet<>();
        SymbolTable<Type> methodSymbols = new SymbolTable<Type>();
        methodSymbols.enterScope();

        // TODO: for ri file, typecheck all uses and use modules
        // TODO: everything declared in the interface must be defined in the module.
        for (Use u:useList) {
            if (!visitedInterfaces.contains(u.id.toString())) {
                Type useType = u.typeCheck(methodSymbols, zhenFileName, res, visitedInterfaces);
                if (useType.getType() != Type.TypeCheckingType.UNIT) {
                    throw new SemanticError(u.getLine(), u.getColumn(), "use somehow not unit");
                }
            }


        }

        for (Method_Interface mI: methods_inter){
            Type curMethod = mI.typeCheck(res,methodSymbols);
            Id nameOfMethod = mI.getName();
            if (methodName.contains(nameOfMethod.toString())){
                throw new SemanticError(mI.getLine(), mI.getColumn() ,"interface function already exists");
            }
            if (res.containsKey(nameOfMethod)) {
                throw new SemanticError(mI.getLine(), mI.getColumn() ,
                        "interface function already defined in different interface");
            }
            Type funcTypeInTable;
            if (mI.isRecord) {
                String recordName = mI.recordType.recordName;
                funcTypeInTable =  new Type(recordName, mI.fields, getLine(),getColumn(), true);
            } else {
                ArrayList<Type> inTypes = mI.getInputTypes();
                ArrayList<Type> outTypes = mI.getOutputtypes();
                funcTypeInTable = new Type(inTypes,outTypes);
            }
            useInterfaceMethods.add(nameOfMethod);
            methodSymbols.add(nameOfMethod,funcTypeInTable);
            res.put(nameOfMethod,funcTypeInTable);
            methodName.add(nameOfMethod.toString());
        }

        methodSymbols.exitScope();
        return res;
    }
}