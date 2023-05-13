package aar226_akc55_ayc62_ahl88.newast;

import aar226_akc55_ayc62_ahl88.*;
import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.expr.*;
import aar226_akc55_ayc62_ahl88.newast.interfaceNodes.EtiInterface;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;
import java_cup.runtime.lr_parser;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Use extends AstNode{
    public final Id id;

    public Use(Id id, int l, int c) {
        super(l,c);
        this.id = id;
    }

    public Use(String s, int l, int c) {
        super(l,c);
        this.id = new Id(s, l, c);
    }

    public String toString(){
        return "(" + "use " + id.toString() + ")";
    }

    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("use");
        id.prettyPrint(p);
        p.endList();
    }

    private boolean isNecessaryUse(String zhenFilename) {
        Path path = Paths.get(zhenFilename);
        String fileNameString = path.getFileName().toString();
        fileNameString = fileNameString.substring(0, fileNameString.length() - 3);
        return id.toString().equals(fileNameString);
    }

    public Type typeCheck(SymbolTable<Type> table, String zhenFilename, HashMap<Id,Type> firstPass,
                          ArrayList<String> visitedInterfaces) {
        String libpathDir = aar226_akc55_ayc62_ahl88.Main.libpathDirectory;
        boolean isRho = Main.isRho;

        String filename = Paths.get(libpathDir, id.toString() + (isRho ? ".ri" : ".eti")).toString();

        try (FileReader fileReader = new FileReader(filename)) {
            lr_parser pi = isRho ? new RiParser(new RhoLex(fileReader)) : new EtiParser(new EtaLex(fileReader));
            EtiInterface eI = (EtiInterface) pi.parse().value;

            ArrayList<Id> useInterfaceMethods = new ArrayList<>();
            visitedInterfaces.add(this.id.toString());

            firstPass.putAll(eI.firstPass(zhenFilename, firstPass, useInterfaceMethods, visitedInterfaces)); // TODO
            for (HashMap.Entry<Id,Type> entry : firstPass.entrySet()){
                if (table.contains(entry.getKey())) {
                    if (!((table.lookup(entry.getKey()).getType() != Type.TypeCheckingType.FUNC &&
                            entry.getValue().getType() != Type.TypeCheckingType.FUNC) ||
                            (table.lookup(entry.getKey()).getType() != Type.TypeCheckingType.RECORD &&
                                    entry.getValue().getType() != Type.TypeCheckingType.RECORD))) {
                        throw new SemanticError(getLine(), getColumn(), " Duplicate Name is not a function");
                    } else {
                        if (!entry.getValue().isSameFunc(table.lookup(entry.getKey()))) {
                            throw new SemanticError(getLine(), getColumn(), " Duplicate Function not exact same");
                        }
                    }
                }else {
                    table.add(entry.getKey(), entry.getValue());
                    if (isNecessaryUse(zhenFilename)) {
                        table.necessaryDefs = new ArrayList(useInterfaceMethods);
                    }
                }
            }

        } catch (Error e) {
//            System.out.println(e.getMessage());
            throw new SemanticError(getLine() , getColumn(),"Faulty interface file " + filename + " " + e.getMessage());
        } catch (Exception e) {
//            e.printStackTrace();
            //this would get thrown the file existed but was parsed as
            // a program file for some reason
            throw new SemanticError(getLine(),getColumn(),"Could not find interface " + filename);
        }
        nodeType = new Type(Type.TypeCheckingType.UNIT);
        return nodeType;
    }

    public IRStmt accept(IRVisitor visitor) {
        return visitor.visit(this);
    }

}