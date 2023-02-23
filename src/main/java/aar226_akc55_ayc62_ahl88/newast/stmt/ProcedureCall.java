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
            Type procedureInputType = procedureInputs.get(i);
            if (!compareType(paramType,procedureInputType)){
                throw new Error(identifier.getLine() + ":" + identifier.getLine() + " Semantic error: procedure input doesn't match type");
            }
        }

        return new Type(Type.TypeCheckingType.UNIT);
    }

    private boolean isArray(Type t) {
        return t.getType() == Type.TypeCheckingType.INTARRAY ||
                t.getType() == Type.TypeCheckingType.BOOLARRAY;
    }

    private boolean compareType(Type paramType, Type procedureInputType) {
        if (paramType.getType() != procedureInputType.getType()) {
            return false;
        }
        // check if param is array and make sure procedure input is also array. Then compare dimensions
        if (isArray(paramType)) {
            return paramType.dimensions.equalsDimension(procedureInputType.dimensions);
        }


        return true;
    }

}