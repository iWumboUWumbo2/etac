package aar226_akc55_ayc62_ahl88.newast.stmt;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
/**
 * Return Class for Ast Nodes
 */
public class Return extends Stmt{
    private ArrayList<Expr> returnArgList;


    /**
     * @param inputreturnList list of Return
     * @param l line Number
     * @param c Column Number
     */
    public Return(ArrayList<Expr> inputreturnList, int l, int c){
        super(l,c);
        returnArgList = inputreturnList;
    }
    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("return");
        returnArgList.forEach(e -> e.prettyPrint(p));
        p.endList();
    }

    @Override
    public Type typeCheck(SymbolTable<Type> table) {
        Id functionName = table.getCurrentFunction();
        Type functionType = table.lookup(functionName);
        ArrayList<Type> functionOutputs = functionType.outputTypes;
        ArrayList<Type> returnResult = new ArrayList<>();

        if (returnArgList.size() == functionOutputs.size()) {
            for (Expr e : returnArgList) {
                Type res = e.typeCheck(table);
                if (res.getType() == Type.TypeCheckingType.FUNC){
                    if (res.outputTypes.size() != 1){
                        throw new SemanticError(getLine(),getColumn()," function output more than one type");
                    }else{
                        returnResult.addAll(res.outputTypes);
                    }
                }
                returnResult.add(res);
            }
        }
        else {
            if (returnArgList.size() != 1) {
                throw new SemanticError(getLine(),getColumn(),"Return list doesn't match function and not a single one for function");
            }
            else {
                Type res = returnArgList.get(0).typeCheck(table);
                if (res.getType() != Type.TypeCheckingType.FUNC){
                    throw new SemanticError(getLine(),getColumn(),"one arg is not func");
                }
                returnResult.addAll(res.outputTypes);
            }
        }

//        for (Expr e : returnArgList) {
//            Type res = e.typeCheck(table);
//            if (res.getType() == Type.TypeCheckingType.FUNC) {
//                returnResult.addAll(res.outputTypes);
//            }
//            else {
//                returnResult.add(res);
//            }
//        }

//        if (returnArgList.size() == 1) {
//            Type res = returnArgList.get(0).typeCheck(table);
//            if (res.getType() == Type.TypeCheckingType.FUNC) {
//                returnResult.addAll(res.outputTypes);
//            }
//        }
//
//        for (Expr e : returnArgList) {
//            Type res = e.typeCheck(table);
//            if (res.getType() == Type.TypeCheckingType.FUNC) {
//                throw new Error();
//            }
//            else {
//                returnResult.add(res);
//            }
//        }

//        if (returnResult.size() != functionOutputs.size()) {
//            throw new Error(getLine() + ":" + getColumn() +
//                    " Semantic error:  Number of resulting outputs doesn't equal function");
//        }

        for (int i = 0; i < returnResult.size(); i++) {
            Type funcOut = functionOutputs.get(i);
            Type resOut = returnResult.get(i);
            if (!funcOut.sameType(resOut)){
                throw new SemanticError(getLine() , getColumn(),"Function output type doesn't match return");
            }
        }

        nodeType = new Type(Type.TypeCheckingType.VOID);
        return nodeType;
    }
}
