package aar226_akc55_ayc62_ahl88.newast.stmt;


import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
/**
 * Class for Block Ast Node
 */
public class Block extends Stmt {
    private ArrayList<Stmt> statementList;


    /**
     * @param inStmtList statement list inside our block
     * @param l line number
     * @param c column number
     */
    public Block(ArrayList<Stmt> inStmtList, int l, int c){
        super(l,c);
        statementList = inStmtList;
    }

    public void prettyPrint(CodeWriterSExpPrinter p) {

        p.startUnifiedList();
        statementList.forEach(e -> e.prettyPrint(p));
        p.endList();

    }

    @Override
    public Type typeCheck(SymbolTable<Type> table) {
        table.enterScope(); // Entering Block
        if (statementList.size() == 0) {
            table.exitScope(); // exiting BLOCK
            return new Type(Type.TypeCheckingType.UNIT); // NO STATEMENTs
        }



        for (int i = 0; i < statementList.size() - 1; i++) { // every one but the last
            Stmt curStmt = statementList.get(i);
            if (curStmt instanceof ProcedureCall){
                ProcedureCall temp = (ProcedureCall) curStmt;
                System.out.println("In Procedure");
//                System.out.println(temp.identifier);
            }
            Type stmtType = curStmt.typeCheck(table);
            if (stmtType.getType() != Type.TypeCheckingType.UNIT) {
                throw new SemanticError(curStmt.getLine() , curStmt.getLine(),"Only last stmt in block can be void");
            }
        }

        Stmt lastStmt = statementList.get(statementList.size()-1);
        Type lastType = lastStmt.typeCheck(table);

//        System.out.println("BLOCK CONTEXT: \n");
//        table.printContext();
//        System.out.println("\nEND BLOCK CONTEXT. \n");

        table.exitScope(); // exiting Block
        return new Type(
                lastType.getType() == Type.TypeCheckingType.UNIT
                        ? Type.TypeCheckingType.UNIT
                        : Type.TypeCheckingType.VOID
        );

        // unit if no return
        // void if return
    }
}

