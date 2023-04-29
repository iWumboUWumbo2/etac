package aar226_akc55_ayc62_ahl88.newast.expr;

import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.arrayliteral.ArrayValueLiteral;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRExpr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

import java.util.ArrayList;

/**
 * Class for function call expressions
 */
public class FunctionCallExpr extends Expr {
    Id id;
    ArrayList<Expr> args;
    Type functionSig;

    public Type getFunctionSig() {
        return functionSig;
    }

    public Id getId() {
        return id;
    }

    public ArrayList<Expr> getArgs() {
        return args;
    }

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
        System.out.println(i);
    }

    @Override
    public Type typeCheck(SymbolTable<Type> table) {
//        System.out.println("In Function");
//        System.out.println(id.toString());
        Type functionType = table.lookup(id);
        functionSig = functionType;

        // Type check record
        if (functionType.getType() == Type.TypeCheckingType.RECORD) {
            ArrayList<Type> recordFieldTypes = functionType.recordFieldTypes;
            if (args.size() != recordFieldTypes.size()) {
                throw new SemanticError(id.getLine(), id.getColumn(), "record field size does not match input size");
            }

            for (int i = 0; i < args.size(); i ++) {
                Expr field_i = args.get(i);
                Type fieldInputType = field_i.typeCheck(table);
                if (!fieldInputType.sameType(recordFieldTypes.get(i))) {
                    throw new SemanticError(field_i.getLine(), field_i.getColumn(), "incorrect record field input type");
                }
            }

            return new Type(id.toString(), recordFieldTypes, getLine(), getColumn());
        }

        // Type check function call
        if (functionType.getType() != Type.TypeCheckingType.FUNC) {
            throw new SemanticError(id.getLine(), id.getColumn(), "identifier isn't function");
        }
        if (functionType.outputTypes.size() == 0) {
            throw new SemanticError(id.getLine(), id.getColumn() ,id.toString() + " is not function");
        }

        ArrayList<Type> functionInputs = functionType.inputTypes;
        if (args.size() != functionInputs.size()) {
            throw new SemanticError(id.getLine(), id.getColumn(),"number of procedure inputs doesn't match call");
        }

        for (int i = 0; i < args.size(); i++){
            Expr param_i = args.get(i);
            Type paramType = param_i.typeCheck(table);

            // if multireturn, then throw error
            if (paramType.getType() == Type.TypeCheckingType.MULTIRETURN){
                throw new SemanticError(param_i.getLine() , param_i.getColumn(),"function has more than one output");
            }

            // otherwise, param is one element type
            Type functionInputTypes = functionInputs.get(i);
            if (!paramType.sameType(functionInputTypes)){
                throw new SemanticError(param_i.getLine() , param_i.getColumn(),"procedure input doesn't match type");
            }
        }

        if (functionType.outputTypes.size() == 1) {
            nodeType = functionType.outputTypes.get(0);
        } else {
            nodeType = new Type(functionType.outputTypes);
        }
        return nodeType;
    }

    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        id.prettyPrint(p);
        args.forEach(e -> e.prettyPrint(p));
        p.endList();
    }

    @Override
    public IRExpr accept(IRVisitor visitor) {
        return visitor.visit(this);
    }
}
