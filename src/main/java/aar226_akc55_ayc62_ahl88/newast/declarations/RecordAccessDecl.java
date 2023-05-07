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

    public RecordAccessDecl(ArrayList<Decl> decls, int l, int c) {
        super(decls.get(0).identifier, l,c);
        this.decls = decls;
        for (int i = 0; i < decls.size(); i ++) {
            System.out.println("decls: " + decls.get(i));
        }
    }
    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
//        Expr accessExpr = new RecordAcessBinop(leftExpr,
//                new Id(rightId, getLine(), getColumn()), getLine(), getColumn());
//        accessExpr.prettyPrint(p);
    }

    @Override
    public Type typeCheck(SymbolTable<Type> table) {
        ArrayList<Type> declTypes =  new ArrayList<Type>();
        int size = decls.size();
//        for (int i = 0; i < size; i++) {
//            declTypes.add(decls.get(i).typeCheck(table));
//        }
//
//
//        if (!(decls.get(size-1) instanceof NoTypeDecl)) {
//            throw new SemanticError(declTypes.get(0).getLine(), declTypes.get(0).getColumn(), "statements block must be of type record at");
//        }
        Type accessType = decls.get(0).typeCheck(table);
        for (int i = 0; i < size-1; i++) {
            Decl nextDecl = decls.get(i+1);

            if (accessType.getType() != Type.TypeCheckingType.RECORD) {
                throw new SemanticError(accessType.getLine(), accessType.getColumn(), "statements block must be of type record at");
            }

            if (accessType.getType() == Type.TypeCheckingType.RECORD) {
                System.out.println("record name: " + accessType.recordName);
                System.out.println("record name: " + accessType.recordName);
                if (accessType.recordFieldToIndex == null) System.out.println("why be it null");
            }


            String rightId = nextDecl.identifier.toString();
            if (nextDecl instanceof NoTypeDecl) {
                System.out.println(accessType.recordFieldToIndex);
                if (!accessType.recordFieldToIndex.containsKey(rightId)) {
                    throw new SemanticError(nextDecl.getLine(), nextDecl.getColumn(), "Invalid field at ");
                }

                int index = accessType.recordFieldToIndex.get(rightId);
                accessType =  accessType.recordFieldTypes.get(index);


            //arr[4].a[4].x
            } else if (nextDecl instanceof ArrAccessDecl arracc) {
                if (!accessType.recordFieldToIndex.containsKey(rightId)) {
                    throw new SemanticError(arracc.getLine(), nextDecl.getColumn(), "Invalid field at ");
                }

                int index = accessType.recordFieldToIndex.get(rightId);
                Type arrType =  accessType.recordFieldTypes.get(index);

                if (!arrType.isArray()) {
                    throw new SemanticError(getLine(), getColumn(), "variable is not an array");
                }
                ArrayList<Expr> indices = arracc.getIndices();
                for (Expr e : indices) {
                    Type exprType = e.typeCheck(table);
                    if (exprType.getType() != Type.TypeCheckingType.INT) {
                        throw new SemanticError(getLine(), getColumn(), "array access is not an int");
                    }
                }

                if (indices.size() > arrType.dimensions.getDim()) {
                    throw new SemanticError(getLine(), getColumn(), "more indices than expected");
                }

                Dimension d = arrType.dimensions;
                Dimension newDim = new Dimension(d.getDim() - indices.size(), d.getLine(), d.getColumn());
                if (newDim.getDim() == 0) {
                    if (arrType.getType() == Type.TypeCheckingType.INTARRAY) {
                        nodeType = new Type(Type.TypeCheckingType.INT);
                        accessType =  nodeType;
                    } else if (arrType.getType() == Type.TypeCheckingType.BOOLARRAY) {
                        nodeType = new Type(Type.TypeCheckingType.BOOL);
                        accessType = nodeType;
                    } else if (arrType.getType() == Type.TypeCheckingType.RECORDARRAY) {
                        nodeType = new Type(Type.TypeCheckingType.RECORD);
                        accessType = nodeType;
                    } else {
                        throw new SemanticError(getLine(), getColumn(), "somehow not an array");
                    }
                } else {
                    accessType = new Type(arrType.getType(), newDim);
                }
            }
        }

        return accessType;
    }

    @Override
    public IRNode accept(IRVisitor visitor) {
        return null;
    }
}
