package aar226_akc55_ayc62_ahl88.newast.expr.recordaccessexpr;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Dimension;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.declarations.AnnotatedTypeDecl;
import aar226_akc55_ayc62_ahl88.newast.expr.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRExpr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

import java.util.ArrayList;
/**
 * Class for Array Accessing
 */
public class RecordAccess extends Expr {

    // Read This Left to Right
    ArrayList<Id> idList;

    public RecordAccess(ArrayList<Id> recordAccess, int l, int c){
        super (l, c);
        idList = recordAccess;
    }
    private void typeCheckIndices(SymbolTable s) throws Error {
//        for (Expr e : idList) {
//            if (e.typeCheck(s).getType() != Type.TypeCheckingType.INT){
//                throw new SemanticError(e.getLine(), e.getColumn(), "array index type not int");
//            }
//        }
    }
    @Override
    public Type typeCheck(SymbolTable s) throws Error {
//        Type e = orgArray.typeCheck(s);
//
//        // check if indices are all ints
//        typeCheckIndices(s);
////        System.out.println("DIMENSION:");
////        System.out.println(e.dimensions.getDim());
//
//        // allow array accesses to unknown
//        if (e.getType() == Type.TypeCheckingType.UNKNOWN) {
//            nodeType = new Type(Type.TypeCheckingType.UNKNOWN);
//            return nodeType;
//        }
//
//        // throw error if arg is not array
//        if (!e.isArray()) {
//            throw new SemanticError(orgArray.getLine(), orgArray.getColumn(), "type is not indexable");
//        } else {
//            long return_dim = e.dimensions.getDim() - idList.size();
////            System.out.println("RETURN DIM: " + return_dim);
//            // throw error if length of access indices > arg dimension
//            if (return_dim < 0) {
//                // allow infinite access to unknown array
//                if (e.getType() == Type.TypeCheckingType.UNKNOWNARRAY) {
//                    nodeType = new Type(Type.TypeCheckingType.UNKNOWN);
//                    return nodeType;
//                }
//                throw new SemanticError(orgArray.getLine(), orgArray.getColumn(), "array index size mismatch");
//            } else if (return_dim == 0) {   // if return dim == arg dim, return literal type
//                if (e.getType() == Type.TypeCheckingType.BOOLARRAY)
//                    nodeType = new Type(Type.TypeCheckingType.BOOL);
//                else if (e.getType() == Type.TypeCheckingType.INTARRAY)
//                    nodeType = new Type(Type.TypeCheckingType.INT);
//                else
//                    nodeType = new Type(Type.TypeCheckingType.UNKNOWN);
//                return nodeType;
//            } else {    // otherwise, return array type
//                nodeType = new Type(e.getType(), new Dimension(return_dim, getLine(), getColumn()));
//                return nodeType;
//            }
//        }
        return null;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        for (int i = 0; i < idList.size();i++){
            p.startList();
            if (i != idList.size()-2) p.printAtom(".");
        }
        for (int i = 0; i< idList.size(); i++){
            idList.get(i).prettyPrint(p);
            p.endList();
        }
    }
    public ArrayList<Id> getidList() {
        return idList;
    }
    @Override
    public IRExpr accept(IRVisitor visitor) {
        return visitor.visit(this);
    }
}
