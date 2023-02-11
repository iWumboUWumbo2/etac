package aar226_akc55_ayc62_ahl88.ast;

public class Return extends Stmt{
    public Return (Expr condition, Node[] stmts){
        super("while", condition, stmts);
    }
}
