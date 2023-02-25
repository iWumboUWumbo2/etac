package aar226_akc55_ayc62_ahl88.newast.expr;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

/**
 * Class for function call expressions
 */
public class FunctionCallExpr extends Expr {
    Id id;
    ArrayList<Expr> args;

    /**
     * @param i function name
     * @param inArgs function arguments
     * @param l line number
     * @param c column number
     */
    public FunctionCallExpr(Id i, ArrayList<Expr> inArgs, int l, int c) {
        super (l, c);
        id = i;
        args = inArgs;
    }

    @Override
    public Type typeCheck(SymbolTable<Type> table) {
        System.out.println("In Function");
        System.out.println(id.toString());
        Type functionType = table.lookup(id);

        if (functionType.getType() != Type.TypeCheckingType.FUNC) {
            throw new Error(id.getLine() + ":" + id.getLine() + " Semantic error: identifier isn't function");
        }
        if (functionType.outputTypes.size() == 0) {
            throw new Error(id.getLine() + ":" + id.getLine() + " Semantic error: function is not function");
        }

        ArrayList<Type> procedureInputs = functionType.inputTypes;
        if (args.size() != procedureInputs.size()) {
            throw new Error(id.getLine() + ":" + id.getLine() + " Semantic error: number of procedure inputs doesn't match call");
        }
        for (int i = 0; i < args.size(); i++){
            Type paramType = args.get(i).typeCheck(table);
            Type procedureInputType = procedureInputs.get(i);
            if (!paramType.sameType(procedureInputType)){
                throw new Error(id.getLine() + ":" + id.getLine() + " Semantic error: procedure input doesn't match type");
            }
        }

        if (functionType.outputTypes.size() == 1) {
            return functionType.outputTypes.get(0);
        } else {
            return new Type(functionType.outputTypes);
        }
    }

    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        id.prettyPrint(p);
        args.forEach(e -> e.prettyPrint(p));
        p.endList();

    }
}
