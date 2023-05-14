package aar226_akc55_ayc62_ahl88.newast.stmt;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

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

    @Override
    public IRStmt accept(IRVisitor visitor) {
        return visitor.visit(this);
    }

    public ArrayList<Expr> getReturnArgList() {
        return returnArgList;
    }
}
