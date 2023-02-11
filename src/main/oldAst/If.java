package aar226_akc55_ayc62_ahl88.ast;

public class If extends Stmt {
    public If (Expr condition, Node[] stmts){
        super("if", condition, stmts);
    }
}
