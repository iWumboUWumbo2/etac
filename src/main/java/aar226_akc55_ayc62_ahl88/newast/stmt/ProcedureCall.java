package aar226_akc55_ayc62_ahl88.newast.stmt;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;
import aar226_akc55_ayc62_ahl88.visitors.ContainsBreakVisitor;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;
import java.util.ArrayList;

/**
 * Class for Procedure Calls
 */
public class ProcedureCall extends Stmt {
    Id identifier;
    ArrayList<Expr> paramList;
    Type functionSig;
    public ArrayList<Expr> getParamList() {
        return paramList;
    }
    public Id getIdentifier() {
        return identifier;
    }

    /**
     * @param id Identifer to Procedure
     * @param parameterList List of Parameters
     * @param l  line Number
     * @param c  Column Number
     */
    public ProcedureCall(Id id, ArrayList<Expr> parameterList, int l , int c) {
        super(l,c);
        identifier = id;
        paramList = parameterList;
    }

    public Type getFunctionSig() {
        return functionSig;
    }

    @Override
    public Type typeCheck(SymbolTable<Type> table) {
        Type functionType = table.lookup(identifier);
        functionSig = functionType;

        if (functionType.getType() != Type.TypeCheckingType.FUNC) {
            throw new SemanticError(identifier.getLine() , identifier.getColumn()
                    ,"identifier isn't function");
        }

        if (functionType.outputTypes.size() != 0) { // check if procedure
            throw new SemanticError(identifier.getLine() , identifier.getColumn()
                    ,"function is not procedure");
        }

        ArrayList<Type> procedureInputs = functionType.inputTypes;
        if (paramList.size() != procedureInputs.size()) {
            throw new SemanticError(identifier.getLine() ,identifier.getColumn()
                    ,"number of procedure inputs doesn't match call");
        }

        for (int i = 0; i < paramList.size(); i++){
            Expr param_i = paramList.get(i);
            Type paramType = param_i.typeCheck(table);

            // if multireturn, then throw error
            if (paramType.getType() == Type.TypeCheckingType.MULTIRETURN){
                    throw new SemanticError(param_i.getLine() , param_i.getColumn()
                            ,"function has more than one output");
                }

            // otherwise, param is one element type
            Type procedureInputType = procedureInputs.get(i);
            if (!paramType.sameType(procedureInputType)){
                throw new SemanticError(param_i.getLine(), param_i.getColumn()
                        ,"procedure input doesn't match type");
            }
        }

        nodeType = new Type(Type.TypeCheckingType.UNIT);
        return nodeType;
    }
    @Override
    public Boolean accept(ContainsBreakVisitor v) {
        return v.visit(this);
    }
    @Override
    public IRStmt accept(IRVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        identifier.prettyPrint(p);
        paramList.forEach(e -> e.prettyPrint(p));
        p.endList();
    }
}