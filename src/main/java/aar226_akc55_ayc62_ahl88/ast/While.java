package aar226_akc55_ayc62_ahl88.ast;

public class While extends Stmt{
    public While (Expr condition, Node[] stmts){
        super("while", condition, stmts);
    }
}
