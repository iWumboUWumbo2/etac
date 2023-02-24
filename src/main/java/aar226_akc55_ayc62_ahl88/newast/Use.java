package aar226_akc55_ayc62_ahl88.newast;

import aar226_akc55_ayc62_ahl88.EtaParser;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.expr.*;
import aar226_akc55_ayc62_ahl88.newast.interfaceNodes.EtiInterface;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java_cup.Lexer;

import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class Use extends AstNode{
    private Id id;

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
    public Type typeCheck(SymbolTable<Type> table,String inputDirectory, boolean specified){
        String filename = id.toString() + ".eti";
        String zhenFilename =
                (specified) ? Paths.get(inputDirectory, filename).toString() : filename;
        try (FileReader fileReader = new FileReader(zhenFilename)) {
            EtaParser p = new EtaParser(new Lexer(fileReader));
            EtiInterface eI = (EtiInterface) p.parse().value;
            HashMap<Id,Type> firstPass = eI.firstPass(); // to do need to fail in interface
            for (HashMap.Entry<Id,Type> entry : firstPass.entrySet()){
                table.add(entry.getKey(),entry.getValue());
            }
        } catch (Error e) {
            e.getMessage();
            throw new Error(
                    "Faulty interface file " + filename
            );
        } catch (Exception e) {
            //this would get thrown the file existed but was parsed as
            // a program file for some reason
            throw new Error(
                    "Could not find interface ");
        }
        return new Type(Type.TypeCheckingType.UNIT);
    }

}
