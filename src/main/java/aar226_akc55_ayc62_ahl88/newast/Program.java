package aar226_akc55_ayc62_ahl88.newast;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.Errors.SyntaxError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.definitions.*;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
import java.util.HashSet;

public class Program extends AstNode {
    private ArrayList<Use> useList;
    private ArrayList<Definition> definitions;


    public Program(ArrayList<Use> uses, ArrayList<Definition> definitions,int l, int c) {
        super(l,c);
        useList = uses;
        this.definitions = definitions;
        if (definitions.size() == 0){
            throw new SyntaxError(l,c,"no definitions");
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
                throw new SemanticError(u.getLine(), u.getColumn(), "use somehow not unit");
            }
        }
        HashSet<String> currentFileIds  = new HashSet<>();
        // FIRST PASS
        for (Definition d: definitions){         // table should be updated to hold all global decls and functions and interfaces
            d.firstPass(table,currentFileIds);
        }
//        table.printContext();
//        table.printContext();

        // SECOND PASS
        for (Definition d: definitions){
            d.typeCheck(table);
        }

        // Then go through Methods again to type check only their blocks
        table.exitScope();
        nodeType = new Type(Type.TypeCheckingType.UNIT);
        return nodeType;

    }
}