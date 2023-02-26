package aar226_akc55_ayc62_ahl88.newast.interfaceNodes;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.AstNode;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class EtiInterface extends AstNode {
    private ArrayList<Method_Interface> methods_inter;

    public EtiInterface(ArrayList<Method_Interface> mi,int l, int c) {
        super(l,c);
        this.methods_inter = mi;
        if (mi.size() == 0){
            throw new Error(1+":"+1+" error: no method definitions");
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

    public HashMap<Id,Type> firstPass() {
        HashMap<Id,Type> res = new HashMap<>();
        HashSet<String> methodName= new HashSet<>();
        SymbolTable<Type> methodSymbols = new SymbolTable<Type>();
        methodSymbols.enterScope();
        for (Method_Interface mI: methods_inter){
            Type curMethod = mI.typeCheck(res,methodSymbols);
            Id nameOfMethod = mI.getName();
            if (methodName.contains(nameOfMethod.toString())){
                throw new Error(mI.getLine()+":" + mI.getColumn() +" error: interface function already exists");
            }
            ArrayList<Type> inTypes = mI.getInputTypes();
            ArrayList<Type> outTypes = mI.getOutputtypes();
            Type funcTypeInTable = new Type(inTypes,outTypes);
            methodSymbols.add(nameOfMethod,funcTypeInTable);
            res.put(nameOfMethod,funcTypeInTable);
            methodName.add(nameOfMethod.toString());
        }

        methodSymbols.exitScope();
        return res;
    }
}