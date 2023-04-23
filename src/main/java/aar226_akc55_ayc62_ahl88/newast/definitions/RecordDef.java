package aar226_akc55_ayc62_ahl88.newast.definitions;

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
        return null;
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
