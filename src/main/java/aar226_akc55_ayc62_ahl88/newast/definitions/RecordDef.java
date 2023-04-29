package aar226_akc55_ayc62_ahl88.newast.definitions;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.declarations.AnnotatedTypeDecl;
import aar226_akc55_ayc62_ahl88.newast.declarations.Decl;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

import java.util.ArrayList;
import java.util.HashSet;

public class RecordDef extends Definition {
    Id recordName;
    ArrayList<AnnotatedTypeDecl> recordTypes;

    public RecordDef(Id recordName, ArrayList<AnnotatedTypeDecl> recordTypes, int l, int c){
        super(l, c);
        this.recordName = recordName;
        this.recordTypes = recordTypes;
    }

    public Type typeCheck(SymbolTable<Type> table) {
        return null;
    }
    public IRNode accept(IRVisitor visitor) {
        return null;
    }

    public Type firstPass(SymbolTable<Type> table, HashSet<String> currentFile) {
        // table keeps track of var names and methods
        // recordTable keeps track of record names
        // currentFile keeps track of

        // Check if fields have the same names or same as record name.
        table.enterScope();
        Id old = table.currentParentFunction;
        table.currentParentFunction = recordName;
        for (AnnotatedTypeDecl atd : recordTypes) {
            if (recordName.toString().equals(atd.identifier.toString())) {
                throw new SemanticError(getLine(), getColumn(), "field same name as record");
            }
            if (table.contains(atd.identifier)) {
                throw new SemanticError(getLine(), getColumn(), "field already present");
            }
            table.add(atd.identifier, atd.type);
        }
        table.currentParentFunction = old;
        table.exitScope();

        if (currentFile.contains(recordName.toString())) {
            throw new SemanticError(getLine(), getColumn(), "Current File has same identifier");
        }
        currentFile.add(recordName.toString());

        Type recordType = new Type(recordName.toString(), recordTypes, getLine(), getColumn());
        if (table.contains(recordName)) {
            Type rhs = table.lookup(recordName);
            if (rhs.getType() != Type.TypeCheckingType.RECORD) {
                throw new SemanticError(getLine(), getColumn(), "Another declaration in table that isn't record");
            }
            if (!recordType.isSameRecord(rhs)) {
                throw new SemanticError(getLine(), getColumn(), "Duplicate record not exact same");
            }
        } else {
            table.add(recordName, recordType);
        }

        nodeType = new Type(Type.TypeCheckingType.UNIT);
        return nodeType;
    }

    public ArrayList<Type> getInputTypes(){
        ArrayList<Type> inputTypes = new ArrayList<>();
        for (AnnotatedTypeDecl atd: recordTypes){
            inputTypes.add(atd.type);
        }
        return inputTypes;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startUnifiedList();
        recordName.prettyPrint(p);

        p.startList();
        for (Decl d : recordTypes) d.prettyPrint(p);
        p.endList();

        p.endList();
    }
}
