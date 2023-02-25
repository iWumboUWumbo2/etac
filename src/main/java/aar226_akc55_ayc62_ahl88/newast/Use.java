package aar226_akc55_ayc62_ahl88.newast;

import aar226_akc55_ayc62_ahl88.EtiParser;
import aar226_akc55_ayc62_ahl88.Lexer;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.expr.*;
import aar226_akc55_ayc62_ahl88.newast.interfaceNodes.EtiInterface;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java.io.File;

import java_cup.runtime.Symbol;

import java.io.FileReader;
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
    public Type typeCheck(SymbolTable<Type> table,String zhenFilename){
        File myFile = new File( zhenFilename );
        String myDir = myFile.getParent();
        String pathSeparator = File.separator;
//        System.out.println(myFile);
//        System.out.println(myDir);
//        System.out.println(pathSeparator);
        String filename = myDir + pathSeparator + id.toString() + ".eti";


        System.out.println(filename);
        try {
            EtiParser pi = new EtiParser(new Lexer(new FileReader(filename)));
            EtiInterface res = (EtiInterface) pi.parse().value;
            HashMap<Id,Type> firstPass = res.firstPass();
        }
        catch (Exception e){
            e.printStackTrace();
            throw new Error(getLine() + ":" + getColumn()+ " File without that name found" +filename);
        }
        try (FileReader fileReader = new FileReader(filename)) {
            EtiParser pi = new EtiParser(new Lexer(fileReader));
            EtiInterface eI = (EtiInterface) pi.parse().value;
            HashMap<Id,Type> firstPass = eI.firstPass(); // to do need to fail in interface
            for (HashMap.Entry<Id,Type> entry : firstPass.entrySet()){
                if (table.contains(entry.getKey())){
                    throw new Error(getLine()+":" + getColumn() + " error: function from interface already exists");
                }
                table.add(entry.getKey(),entry.getValue());
            }
        } catch (Error e) {
            e.getMessage();
            throw new Error(
                    "Faulty interface file " + filename
            );
        } catch (Exception e) {
            e.printStackTrace();
            //this would get thrown the file existed but was parsed as
            // a program file for some reason
            throw new Error(
                    "Could not find interface ");
        }
        return new Type(Type.TypeCheckingType.UNIT);
    }

}
