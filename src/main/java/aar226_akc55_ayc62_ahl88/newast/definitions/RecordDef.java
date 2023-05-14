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

/**
 * Class for record definitions.
 */
public class RecordDef extends Definition {

    public Id recordName;
    ArrayList<AnnotatedTypeDecl> recordTypes;

    /**
     * @param recordName
     * @param recordTypes
     * @param l
     * @param c
     */
    public RecordDef(Id recordName, ArrayList<AnnotatedTypeDecl> recordTypes, int l, int c){
        super(l, c);
        this.recordName = recordName;
        this.recordTypes = recordTypes;
    }

    /**
     * @param table
     * @param currentFile
     */
    public void zeroPass(SymbolTable<Type> table, HashSet<String> currentFile) {
        // Check if fields have the same names or same as record name.
        Type recordType = new Type(recordName.toString(), recordTypes, getLine(), getColumn(), true);
        if (table.contains(recordName)) {
            Type rhs = table.lookup(recordName);
            if (rhs.getType() != Type.TypeCheckingType.RECORD) {
                throw new SemanticError(getLine(), getColumn(), "Another declaration in table that isn't record");
            }
            if (!recordType.isSameRecord(rhs)) {
                throw new SemanticError(getLine(), getColumn(), "Duplicate record not exact same");
            }
            table.replace(recordName, recordType);
            table.allRecordTypes.put(recordName.toString(), recordType);
        } else {
            table.add(recordName, recordType);
            table.allRecordTypes.put(recordName.toString(), recordType);
        }
    }

    /**
     * @param table
     * @param currentFile
     * @return
     */
    public Type firstPass(SymbolTable<Type> table, HashSet<String> currentFile) {
        // Check if fields have the same names or same as record name.
        table.enterScope();
        Id old = table.currentParentFunction;
        table.currentParentFunction = recordName;
        for (AnnotatedTypeDecl atd : recordTypes) {
            if (recordName.toString().equals(atd.identifier.toString())) {
                throw new SemanticError(getLine(), getColumn(), "field same name as record");
            }
        }
        table.currentParentFunction = old;
        table.exitScope();

        if (currentFile.contains(recordName.toString())) {
            throw new SemanticError(getLine(), getColumn(), "Current File has same identifier");
        }
        currentFile.add(recordName.toString());

        Type recordType = new Type(recordName.toString(), recordTypes, getLine(), getColumn(), true);
        if (table.contains(recordName)) {
            Type rhs = table.lookup(recordName);
            if (rhs.getType() != Type.TypeCheckingType.RECORD) {
                throw new SemanticError(getLine(), getColumn(), "Another declaration in table that isn't record");
            }
            if (!recordType.isSameRecord(rhs)) {
                throw new SemanticError(getLine(), getColumn(), "Duplicate record not exact same");
            }
            table.replace(recordName, recordType);
        } else {
            table.add(recordName, recordType);
            table.allRecordTypes.put(recordName.toString(), recordType);
        }

        nodeType = new Type(Type.TypeCheckingType.UNIT);
        table.addVisitedDef(this.recordName);
        return nodeType;
    }

    public Type typeCheck(SymbolTable<Type> table) {
        return nodeType;
    }
    public IRNode accept(IRVisitor visitor) {
        return null;
    }

    /**
     * @return
     * Gets types of all fields in order
     */
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
