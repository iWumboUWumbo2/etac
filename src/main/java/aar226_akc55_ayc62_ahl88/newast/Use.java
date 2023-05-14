package aar226_akc55_ayc62_ahl88.newast;

import aar226_akc55_ayc62_ahl88.*;
import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.expr.*;
import aar226_akc55_ayc62_ahl88.newast.interfaceNodes.EtiInterface;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;
import java_cup.runtime.lr_parser;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class for use statements.
 */
public class Use extends AstNode{
    public final Id id;
    public EtiInterface eI;

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

    /**
    add record to context
    */
    public void zeroPass(SymbolTable<Type> table, String zhenFilename, HashMap<Id,Type> zeroPass,
                          ArrayList<String> visitedInterfaces, boolean useOriginalName) {

        String libpathDir = aar226_akc55_ayc62_ahl88.Main.libpathDirectory;
        String filename = (useOriginalName) ? zhenFilename
                : Paths.get(libpathDir, id.toString() + (".ri")).toString();

        try (FileReader fileReader = new FileReader(filename)) {

        } catch (Exception e) {
            // try again with eti
            filename = Paths.get(libpathDir, id.toString() + ".eti").toString();
        }
        try (FileReader fileReader = new FileReader(filename)) {
            lr_parser pi = new RiParser(new RhoLex(fileReader));
            eI = (EtiInterface) pi.parse().value;

            visitedInterfaces.add(this.id.toString());

            eI.zeroPass(zhenFilename, zeroPass, visitedInterfaces); // modifies zeroPass if needed
            for (HashMap.Entry<Id,Type> entry : zeroPass.entrySet()){
                if (table.contains(entry.getKey())) {
                    throw new SemanticError(getLine(), getColumn(), " Duplicate Name in interface");
                }else {
                    table.add(entry.getKey(), entry.getValue());

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
    }

    private void checkRecordInTable(SymbolTable<Type> table, String id, int l, int c) {
        if (!table.contains(id))
            throw new SemanticError(l, c,
                    "record type not defined in scope");
    }

    /**
     * @param table symbol table
     * checks that all record types are in scope
     */
    private void checkRecordTypes(SymbolTable<Type> table, HashMap.Entry<Id,Type> entryv1, HashMap.Entry<String,Type> entryv2) {
        Type t;
        if (entryv1 != null) t = entryv1.getValue();
        else t = entryv2.getValue();

            if (t.isRecord()) {
                checkRecordInTable(table, t.recordName, t.getLine(), t.getColumn());
                for (String field : t.recordFieldToIndex.keySet()) {
                    int index = t.recordFieldToIndex.get(field);
                    Type fieldType = t.recordFieldTypes.get(index);
                    if (fieldType.isRecord() || fieldType.isRecordArray()) {
                        checkRecordInTable(table, fieldType.recordName,
                                t.getLine(), t.getColumn());
                    }
                }
            } else if (t.isFunc()) {
                for (Type inputType : t.inputTypes) {
                    if (inputType.isRecord() || inputType.isRecordArray()) {
                        checkRecordInTable(table, inputType.recordName,
                                inputType.getLine(), inputType.getColumn());
                    }
                }
                for (Type outputType : t.outputTypes) {
                    if (outputType.isRecord() || outputType.isRecordArray()) {
                        checkRecordInTable(table, outputType.recordName,
                                t.getLine(), t.getColumn());
                    }
                }
            }
        }

    public Type typeCheck(SymbolTable<Type> table, String zhenFilename, HashMap<Id,Type> firstPass,
                          ArrayList<String> visitedInterfaces, boolean useOriginalName) {
        String libpathDir = aar226_akc55_ayc62_ahl88.Main.libpathDirectory;
        boolean isRho = Main.isRho;

        String filename = (useOriginalName) ? zhenFilename
                : Paths.get(libpathDir, id.toString() + (isRho ? ".ri" : ".eti")).toString();
        try (FileReader fileReader = new FileReader(filename)) {


        }catch (Exception e) {
            isRho = false;
            filename = Paths.get(libpathDir, id.toString() + ".eti").toString();
        }
        try (FileReader fileReader = new FileReader(filename)) {

            if (eI == null) {
                lr_parser pi = isRho ? new RiParser(new RhoLex(fileReader)) : new EtiParser(new EtaLex(fileReader));
                eI = (EtiInterface) pi.parse().value;
            }
            ArrayList<Id> useInterfaceMethods = new ArrayList<>();
            visitedInterfaces.add(this.id.toString());

            HashMap<String,Type> flattened_table = table.flatten();
            for (HashMap.Entry<String,Type> entry : flattened_table.entrySet()) {
                checkRecordTypes(table, null, entry); // check record types are all in scope from zero pass
            }

            firstPass.putAll(eI.firstPass(zhenFilename, firstPass, useInterfaceMethods, visitedInterfaces, table));
            for (HashMap.Entry<Id,Type> entry : firstPass.entrySet()){
                checkRecordTypes(table, entry, null); // check record types are all in scope
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