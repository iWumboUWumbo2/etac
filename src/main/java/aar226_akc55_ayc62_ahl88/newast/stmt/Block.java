package aar226_akc55_ayc62_ahl88.newast.stmt;


import aar226_akc55_ayc62_ahl88.Errors.SemanticError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRName;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;

import java.util.ArrayList;
/**
 * Class for Block Ast Node
 */
public class Block extends Stmt {
    private ArrayList<Stmt> statementList;
    public ArrayList<Stmt> getStatementList(){
        return statementList;
    }

    /**
     * @param inStmtList statement list inside our block
     * @param l line number
     * @param c column number
     */
    public Block(ArrayList<Stmt> inStmtList, int l, int c){
        super(l,c);
        statementList = inStmtList;
    }

    @Override
    public Type typeCheck(SymbolTable<Type> table) {
        table.enterScope(); // Entering Block
        if (statementList.size() == 0) {
            table.exitScope(); // exiting BLOCK
            nodeType = new Type(Type.TypeCheckingType.UNIT);
            return nodeType; // NO STATEMENTs
        }

        for (int i = 0; i < statementList.size() - 1; i++) { // every one but the last
            Stmt curStmt = statementList.get(i);
            if (curStmt instanceof ProcedureCall){
                ProcedureCall temp = (ProcedureCall) curStmt;
            }
            Type stmtType = curStmt.typeCheck(table);
            if (stmtType.getType() != Type.TypeCheckingType.UNIT) {
                throw new SemanticError(curStmt.getLine() , curStmt.getLine(),"Only last stmt in block can be void");
            }
        }

        Stmt lastStmt = statementList.get(statementList.size()-1);
        Type lastType = lastStmt.typeCheck(table);

        table.exitScope(); // exiting Block
        nodeType = new Type(
                lastType.getType() == Type.TypeCheckingType.UNIT
                        ? Type.TypeCheckingType.UNIT
                        : Type.TypeCheckingType.VOID
        );
        // unit if no return
        // void if return
        return nodeType;
    }

    @Override
    public IRStmt accept(IRVisitor visitor) {
        return visitor.visit(this);
    }

    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startUnifiedList();
        statementList.forEach(e -> e.prettyPrint(p));
        p.endList();
    }
}

