package aar226_akc55_ayc62_ahl88.newast.stmt;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;


/**
 * Class for Procedure Calls
 */
public class ProcedureCall extends Stmt {
    Id identifier;
    ArrayList<Expr> paramList;

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
    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        identifier.prettyPrint(p);
        paramList.forEach(e -> e.prettyPrint(p));
        p.endList();
    }

    @Override
    public Type typeCheck(SymbolTable<Type> table) {

        Type functionType = table.lookup(identifier);

        if (functionType.getType() != Type.TypeCheckingType.FUNC) {
            throw new Error(identifier.getLine() + ":" + identifier.getLine() + " Semantic error: identifier isn't function");
        }
        if (functionType.outputTypes.size() != 0) {
            throw new Error(identifier.getLine() + ":" + identifier.getLine() + " Semantic error: function is not procedure");
        }
        ArrayList<Type> procedureInputs = functionType.inputTypes;
        if (paramList.size() != procedureInputs.size()) {
            throw new Error(identifier.getLine() + ":" + identifier.getLine() + " Semantic error: number of procedure inputs doesn't match call");
        }
        for (int i = 0; i < paramList.size(); i++){
            Type paramType = paramList.get(i).typeCheck(table);
            if (paramType.getType() == Type.TypeCheckingType.FUNC){
                if (paramType.outputTypes.size() != 1){
                    throw new Error(identifier.getLine() + ":" + identifier.getLine() + " error: function has more than one output");
                }
                Type oneOut = paramType.outputTypes.get(0);
                Type procedureInputType = procedureInputs.get(i);
                if (!oneOut.sameType(procedureInputType)){
                    throw new Error(identifier.getLine() + ":" + identifier.getLine() + " Semantic error: procedure input doesn't match type");
                }
            }else{
                Type procedureInputType = procedureInputs.get(i);
                if (!paramType.sameType(procedureInputType)){
                    throw new Error(identifier.getLine() + ":" + identifier.getLine() + " Semantic error: procedure input doesn't match type");
                }
            }
        }

        return new Type(Type.TypeCheckingType.UNIT);

    }


}