package aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit;

import aar226_akc55_ayc62_ahl88.newast.stmt.Stmt;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IRLoweringVisitor extends IRVisitor {
    private static final int WORD_BYTES = 8;
    private static final String OUT_OF_BOUNDS = "_xi_out_of_bounds";
    private int labelCnt;
    private int tempCnt;
    private String nxtLabel() {
        return String.format("l%d", (labelCnt++));
    }

    private String nxtTemp() {
        return String.format("t%d", (tempCnt++));
    }
    public IRLoweringVisitor (IRNodeFactory inf) {
        super(inf);
        labelCnt = 0;
        tempCnt = 0;
    }

    private class BasicBlock {} // Need to think about what to add

    @Override
    protected IRNode leave(IRNode parent, IRNode n, IRNode n_, IRVisitor v_) {
        if (n_ instanceof IRBinOp irbin) return canon(irbin);
        if (n_ instanceof IRCall irc) return canon(irc);
        if (n_ instanceof IRCallStmt ircstmt) return canon(ircstmt);
        if (n_ instanceof IRCJump ircj) return canon(ircj);
        if (n_ instanceof IRCompUnit icu) return canon(icu);
        if (n_ instanceof IRConst ic) return canon(ic);
        if (n_ instanceof IRESeq ireseq) return canon(ireseq);
        if (n_ instanceof IRExp irexp) return  canon(irexp);
        if (n_ instanceof IRFuncDecl irfunc) return canon(irfunc);
        if (n_ instanceof IRJump irj) return canon(irj);
        if (n_ instanceof IRLabel irl) return canon(irl);
        if (n_ instanceof IRMem irmem) return canon(irmem);
        if (n_ instanceof IRMove irmove) return canon(irmove);
        if (n_ instanceof IRName irname) return canon(irname);
        if (n_ instanceof IRReturn irret) return canon(irret);
        if (n_ instanceof IRSeq irseq) return canon(irseq);
        if (n_ instanceof IRTemp irtem) return canon(irtem);

        throw new Error("Why is node not found");
    }


    // Lower each statment then flatten all sequences
    private IRNode canon(IRSeq node) {
        ArrayList<IRStmt> flatten = new ArrayList<>();
        for (IRStmt stmt: node.stmts()){
            if (stmt instanceof IRSeq seq){
                flatten.addAll(seq.stmts());
            }else{
                flatten.add(stmt);
            }
        }
        return new IRSeq(flatten);
    }

    // Lower each return expressions then add Return
    private IRNode canon(IRReturn node) {
//        ArrayList<IRStmt> stmts = new ArrayList<>();
//        ArrayList<IRExpr> exprs = new ArrayList<>();
//
//        for (IRExpr expr : node.rets()) {
//
//        }
        return node;
    }

    // Lower Move be very careful look at slides
    private IRNode canon(IRMove node) {
//        IRExpr target = node.target();
//        IRExpr source = node.source();
//
//        if (target instanceof )

        return node;
    }

    // Create Basic Blocks And reorder all the body
    private IRNode canon(IRFuncDecl node) {
        return node;
    }


    // Lift Statement thats it
    private IRNode canon(IRExp node) {
        if (node.expr() instanceof IRESeq ire) {
            return ire.stmt();
        }
        return new IRSeq();
    }

    // do nothing
    private IRNode canon(IRCompUnit node) {
        return node;
    }

    // Lower each Expr
    private IRNode canon(IRCallStmt node) {
        ArrayList<IRStmt> stmts = new ArrayList<>();
        ArrayList<String> temps_strs = new ArrayList<>();

        for (IRExpr expr : node.args()) {
            String ti = nxtTemp();
            if (expr instanceof IRESeq eseq) {
                stmts.add(eseq.stmt());
                temps_strs.add(ti);
                stmts.add(new IRMove(new IRTemp(ti),eseq.expr()));
            }else{
                stmts.add(new IRMove(new IRTemp(ti),expr));
            }
        }

        String t = nxtTemp();

        List<IRExpr> temps = new ArrayList<>();
        for (String tmp : temps_strs) {
            temps.add(new IRTemp(tmp));
        }

        stmts.add(new IRCallStmt(node.target(), node.n_returns(), temps));
        stmts.add(new IRMove(new IRTemp(t), new IRTemp("_RV1")));

        return new IRESeq(new IRSeq(stmts), new IRTemp(t));
    }
    // Lower each Expr
    private IRNode canon(IRCall node) {
        return node;
    }

    // TODO
    private boolean doesCommute(IRExpr expr1, IRExpr expr2) {
        return true;
    }

    // if commute do that otherwise do normal
    private IRNode canon(IRBinOp node) {
        IRExpr left = node.left();
        IRExpr right = node.right();

        if (!(left instanceof IRESeq) && !(right instanceof IRESeq)) {
            return node;
        }

        IRExpr e1, e2;
        IRStmt s1, s2;

        s1 = s2 = null;

        if (left instanceof IRESeq lseq) {
            e1 = lseq.expr();
            s1 = lseq.stmt();
        } else {
            e1 = left;
        }

        if (right instanceof IRESeq rseq) {
            e2 = rseq.expr();
            s2 = rseq.stmt();
        } else {
            e2 = right;
        }

        if (doesCommute(left, right)) {
            return new IRESeq(new IRSeq(s1, s2), new IRBinOp(node.opType(), e1, e2));
        }
        else {
            String t1 = nxtTemp();
            return new IRESeq(new IRSeq(s1, new IRMove(new IRTemp(t1), e1), s2),
                    new IRBinOp(node.opType(), new IRTemp(t1), e2));
        }
    }

    // canonical
    private IRNode canon(IRConst node){
        return node;
    }
    // canonical
    private IRNode canon(IRLabel node){
        return node;
    }
    // canonical
    private IRNode canon(IRName node){
        return node;
    }
    // canonical
    private IRNode canon(IRTemp node) {
        return node;
    }
    // MEM
    private IRNode canon(IRMem node){
        if (node.expr() instanceof IRESeq ireseq) { // lift expression
            IRStmt svec = ireseq.stmt();
            IRExpr ire = ireseq.expr();
            return new IRESeq(svec, new IRMem(ire));
        }
        return node;
    }
    // Conditional Jump
    private IRNode canon(IRCJump node){
        if (node.cond() instanceof IRESeq ireseq) {
            IRStmt svec = ireseq.stmt();
            IRExpr ire = ireseq.expr();
            return new IRSeq(svec, new IRCJump(ire, node.trueLabel(), node.falseLabel()));
        }
        return node;
    }
    // Jump
    private IRNode canon(IRJump node){
        if (node.target() instanceof IRESeq ireseq) {
            IRStmt svec = ireseq.stmt();
            IRExpr ire = ireseq.expr();
            return new IRSeq(svec, new IRJump(ire));
        }
        return node;
    }
    // ESEQ
    private IRNode canon(IRESeq node){
        if (node.expr() instanceof IRESeq ireseq) {
            IRStmt svec = ireseq.stmt();
            IRExpr ire = ireseq.expr();
            return new IRESeq(new IRSeq(node.stmt(), svec), ire);
        }
        return node;
    }
}
