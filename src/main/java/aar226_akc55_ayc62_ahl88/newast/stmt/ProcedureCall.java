package aar226_akc55_ayc62_ahl88.newast.stmt;

import aar226_akc55_ayc62_ahl88.newast.expr.Expr;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
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
}