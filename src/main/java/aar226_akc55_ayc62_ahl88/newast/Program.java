package aar226_akc55_ayc62_ahl88.newast;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.definitions.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

public class Program extends AstNode {
    private ArrayList<Use> useList;
    private ArrayList<Definition> definitions;


    public Program(ArrayList<Use> uses, ArrayList<Definition> definitions,int l, int c) {
        super(l,c);
        useList = uses;
        this.definitions = definitions;
        if (definitions.size() == 0){
            throw new Error(l+":"+c+" error: no definitions");
        }
    }

    // need pretty
    public String toString() {
        String build = "";
        build += useList.toString() + "\n";
        build += definitions.toString();
        return build;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startUnifiedList();

        p.startUnifiedList();
        useList.forEach(e -> e.prettyPrint(p));
        p.endList();

        p.startUnifiedList();
        definitions.forEach(d -> d.prettyPrint(p));
        p.endList();

        p.endList();
    }

    public Type typeCheck(SymbolTable<Type> table, String zhenFile){
//        System.out.println("in program typecheck");
        table.enterScope();
        // first pass to add all Interfaces and Definitions
        for (Use u: useList){
            Type useType = u.typeCheck(table,zhenFile);
            if (useType.getType() != Type.TypeCheckingType.UNIT){
                throw new Error("use somehow not unit");
            }
        }

        // FIRST PASS
        for (Definition d: definitions){         // table should be updated to hold all global decls and functions and interfaces
            d.firstPass(table);
        }
//        table.printContext();

        // SECOND PASS
        for (Definition d: definitions){
            d.typeCheck(table);
        }

        // Then go through Methods again to type check only their blocks
        table.exitScope();
        return new Type(Type.TypeCheckingType.UNIT);

    }
}