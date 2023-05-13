package aar226_akc55_ayc62_ahl88.newast.declarations;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Dimension;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopEnum;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.RecordAcessBinop;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

import javax.lang.model.type.NoType;
import java.util.ArrayList;

public class RecordAccessDecl extends Decl {

    public ArrayList<Decl> decls;

    public ArrayList<Type> types;

    public RecordAccessDecl(ArrayList<Decl> decls, int l, int c) {
        super(decls.get(0).identifier, l,c);
        this.decls = decls;
    }
    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        int size = decls.size();
        for (int i = 0; i < size; i++) {
            p.startList();
            p.printAtom(".");
        }
        decls.get(0).prettyPrint(p);

        for (int i = 1; i < size; i++) {
            decls.get(i).prettyPrint(p);
            p.endList();
        }
        p.endList();

    }

    private void checkIndices(ArrayList<Expr> indices, long maxDim, SymbolTable<Type> table) {
        for (Expr e : indices) {
            Type exprType = e.typeCheck(table);
            if (exprType.getType() != Type.TypeCheckingType.INT) {
                throw new SemanticError(getLine(), getColumn(), "array access is not an int");
            }
        }

        if (indices.size() > maxDim) {
            throw new SemanticError(getLine(), getColumn(), "more indices than expected");
        }
    }

    @Override
    public Type typeCheck(SymbolTable<Type> table) {
        ArrayList<Type> declTypes =  new ArrayList<Type>();
        int size = decls.size();

        Type accessType = decls.get(0).typeCheck(table);
        declTypes.add(accessType);
        for (int i = 0; i < size-1; i++) {
            Decl nextDecl = decls.get(i+1);

            if (accessType.getType() != Type.TypeCheckingType.RECORD) {
                throw new SemanticError(accessType.getLine(), accessType.getColumn(), "statements block must be of type record at");
            }

            accessType = table.lookup(new Id(accessType.recordName,0,0));

            String rightId = nextDecl.identifier.toString();
            if (nextDecl instanceof NoTypeDecl) {

                if (!accessType.recordFieldToIndex.containsKey(rightId)) {
                    throw new SemanticError(nextDecl.getLine(), nextDecl.getColumn(), "Invalid field at ");
                }

                int index = accessType.recordFieldToIndex.get(rightId);
                Type temp =  accessType.recordFieldTypes.get(index);
                accessType = correctType(temp, new Dimension(0, getLine(), getColumn()), table);
                declTypes.add(accessType);
            //arr[4].a[4].x
            } else if (nextDecl instanceof ArrAccessDecl arracc) {
                if (!accessType.recordFieldToIndex.containsKey(rightId)) {
                    throw new SemanticError(arracc.getLine(), nextDecl.getColumn(), "Invalid field at ");
                }

                int index = accessType.recordFieldToIndex.get(rightId);
                Type arrType =  accessType.recordFieldTypes.get(index);

                if (!arrType.isArray()) {
                    throw new SemanticError(getLine(), getColumn(), "record field is not an array");
                }

                ArrayList<Expr> indices = arracc.getIndices();
                checkIndices(indices, arrType.dimensions.getDim(), table);

                Dimension d = arrType.dimensions;
                Dimension newDim = new Dimension(d.getDim() - indices.size(), d.getLine(), d.getColumn());

                if (arrType.isRecordArray() || arrType.isBoolArray() || arrType.isIntArray()) {
                    accessType = correctType(arrType, newDim, table);
                    declTypes.add(accessType);
                } else {
                    throw new SemanticError(getLine(), getColumn(), "somehow not an array");
                }
            }
        }

        nodeType = accessType;
        types = declTypes;
        return accessType;
    }

    @Override
    public IRNode accept(IRVisitor visitor) {
        return null;
    }
}
