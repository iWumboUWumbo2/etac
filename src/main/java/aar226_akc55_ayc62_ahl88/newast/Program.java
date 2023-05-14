package aar226_akc55_ayc62_ahl88.newast;

import aar226_akc55_ayc62_ahl88.*;
import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.Errors.SyntaxError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.definitions.*;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.newast.interfaceNodes.EtiInterface;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;
import java_cup.runtime.lr_parser;

import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Class for the program.
 */
public class Program extends AstNode {
    private ArrayList<Use> useList;
    private ArrayList<Definition> definitions;

    public List<Type> getMethodSigs() {
        return methodSigs;
    }
    private List<Type> methodSigs;


    public Program(ArrayList<Use> uses, ArrayList<Definition> definitions,int l, int c) {
        super(l,c);
        useList = uses;
        this.definitions = definitions;
        if (definitions.size() == 0){
            throw new SyntaxError(l,c,"no definitions");
        }
        else {
            methodSigs = definitions.stream()
                    .filter(defn -> defn instanceof Method)
                    .map(m -> ((Method) m).getFunctionSig())
                    .toList();
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

    private boolean containsUse(String useName) {
        for (Use u : useList) {
            if (useName.equals(u.id.toString())) {
                return true;
            }
        }
        return false;
    }

    public Type typeCheck(SymbolTable<Type> table, String zhenFile){
        table.enterScope();

        // check implicit use
        Path path = Paths.get(zhenFile);
        String fileNameString = path.getFileName().toString();
        fileNameString = fileNameString.substring(0, fileNameString.length() - 3);

        if (Main.isRho && !containsUse(fileNameString)) {
            // create object of Path
            // call getFileName() and get FileName path object
            String libpathDir = aar226_akc55_ayc62_ahl88.Main.libpathDirectory;
            String fileDirString = libpathDir+"/"+fileNameString+".ri";

            try (FileReader fileReader = new FileReader(fileDirString)) {
                useList.add(0, new Use(fileNameString, -1,-1));
            } catch (Error e) {
            } catch (Exception e) {
            }
        }

        // zero pass to add record types for intefaces
        if (Main.isRho) {
            ArrayList<String> visitedInterfaces = new ArrayList<>();
            for (Use u: useList){
                if (!visitedInterfaces.contains(u.id.toString())) {
                    u.zeroPass(table, zhenFile, new HashMap<>(), visitedInterfaces);
                }
            }
        }


        // first pass to add all Interfaces and Definitions
        HashMap<Id,Type> globTypes = new HashMap<>();
        ArrayList<String> visitedInterfaces = new ArrayList<>();
        for (Use u: useList){
            if (!visitedInterfaces.contains(u.id.toString())) {
                Type useType = u.typeCheck(table,zhenFile, globTypes, visitedInterfaces);
                if (useType.getType() != Type.TypeCheckingType.UNIT){
                    throw new SemanticError(u.getLine(), u.getColumn(), "use somehow not unit");
                }
            }
        }

        HashSet<String> currentFileIds  = new HashSet<>();

        // ZERO PASS
        for (Definition d: definitions){         // table should be updated to hold all global decls and functions and interfaces
            if ((d instanceof RecordDef rd)) rd.zeroPass(table,currentFileIds);
        }

        // FIRST PASS
        for (Definition d: definitions){         // table should be updated to hold all global decls and functions and interfaces
            d.firstPass(table,currentFileIds);
        }

        if (Main.isRho) {
            table.isNecessaryVisited();
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

    public IRCompUnit accept(IRVisitor visitor) {
        return visitor.visit(this);
    }

    public ArrayList<Use> getUseList() {
        return useList;
    }

    public ArrayList<Definition> getDefinitions() {
        return definitions;
    }

    public ArrayList<String> getGlobalsID(){
        ArrayList<String> res = new ArrayList<>();
        for (Definition d: definitions){
            if (d instanceof Globdecl gd){
                res.add(gd.getDecl().getIdentifier().toString());
            }else if (d instanceof MultiGlobalDecl mgd){
                mgd.getDecls().forEach(e -> res.add(e.getIdentifier().toString()));
            }
            // no need for methods
        }
        return res;
    }
}