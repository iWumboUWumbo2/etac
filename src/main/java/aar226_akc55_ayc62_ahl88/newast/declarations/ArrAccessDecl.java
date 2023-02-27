package aar226_akc55_ayc62_ahl88.newast.declarations;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Dimension;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import org.apache.tools.ant.util.OutputStreamFunneler;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Class for Array Access Reassignment
 */
public class ArrAccessDecl extends Decl{

    private ArrayList<Expr> indices;
    private ArrayList<Expr> funcParams;

    /**
     * @param id id
     * @param indices indices
     * @param l line number
     * @param c column number
     */
    public ArrAccessDecl(Id id, ArrayList<Expr> indices, int l, int c) {
        super(id,l, c);
        this.indices = indices;
    }
    public ArrAccessDecl(Id id,ArrayList<Expr> funcs, ArrayList<Expr> indices, int l, int c) {
        super(id,l, c);
        funcParams = funcs;
        this.indices = indices;
    }

    public void prettyPrint(CodeWriterSExpPrinter p) {
        if (funcParams == null) {
            for (int i = 0; i < indices.size(); i++) {
                p.startList();
                p.printAtom("[]");
            }
            identifier.prettyPrint(p);

            for (int i = 0; i < indices.size(); i++) {
                indices.get(i).prettyPrint(p);
                p.endList();
            }
        }else{
            for (int i = 0; i < indices.size(); i++) {
                p.startList();
                p.printAtom("[]");
            }
            p.startList();
            identifier.prettyPrint(p);
            for (int i = 0; i < funcParams.size();i++){
                funcParams.get(i).prettyPrint(p);
            }
            p.endList();
            for (int i = 0; i < indices.size(); i++) {
                indices.get(i).prettyPrint(p);
                p.endList();
            }
        }
    }

    @Override
    public Type typeCheck(SymbolTable<Type> table) {
        Type identifierType = table.lookup(identifier);
        if (identifierType.getType() != Type.TypeCheckingType.FUNC) {
            if (!identifierType.isArray()) {
                throw new SemanticError(getLine(), getColumn(), "variable is not an array");
            }

            for (Expr e : indices) {
                Type exprType = e.typeCheck(table);
                if (exprType.getType() != Type.TypeCheckingType.INT) {
                    throw new SemanticError(getLine(), getColumn(), "array access is not an int");
                }
            }

            if (indices.size() > identifierType.dimensions.getDim()) {
                throw new SemanticError(getLine(), getColumn(), "more indices than expected");
            }

            Dimension d = identifierType.dimensions;
            Dimension newDim = new Dimension(d.getDim() - indices.size(), d.getLine(), d.getColumn());
            if (newDim.getDim() == 0) {
                if (identifierType.getType() == Type.TypeCheckingType.INTARRAY) {
                    return new Type(Type.TypeCheckingType.INT);
                } else if (identifierType.getType() == Type.TypeCheckingType.BOOLARRAY) {
                    return new Type(Type.TypeCheckingType.BOOL);
                } else {
                    throw new SemanticError(getLine(), getColumn(), "somehow not an array");
                }
            }

            return new Type(identifierType.getType(), newDim);
        }else{
            if (identifierType.outputTypes.size() != 1){
                throw new SemanticError(identifier.getLine(), identifierType.getColumn(), "Function has more than one type for array access");
            }
            if (!identifierType.outputTypes.get(0).isArray()){
                throw new SemanticError(getLine(), getColumn(), "Function output for array access is not an array");
            }

            Type funcOutType = identifierType.outputTypes.get(0);
            for (Expr e : indices) {
                Type exprType = e.typeCheck(table);
                if (exprType.getType() != Type.TypeCheckingType.INT) {
                    throw new SemanticError(getLine(), getColumn(), "array access is not an int");
                }
            }

            if (indices.size() > funcOutType.dimensions.getDim()) {
                throw new SemanticError(getLine(), getColumn(), "more indices than expected");
            }

            Dimension d = funcOutType.dimensions;
            Dimension newDim = new Dimension(d.getDim() - indices.size(), d.getLine(), d.getColumn());
            if (newDim.getDim() == 0) {
                if (funcOutType.getType() == Type.TypeCheckingType.INTARRAY) {
                    return new Type(Type.TypeCheckingType.INT);
                } else if (funcOutType.getType() == Type.TypeCheckingType.BOOLARRAY) {
                    return new Type(Type.TypeCheckingType.BOOL);
                } else {
                    throw new SemanticError(getLine(), getColumn(), "somehow not an array");
                }
            }
            return new Type(funcOutType.getType(), newDim);
        }
    }
}
