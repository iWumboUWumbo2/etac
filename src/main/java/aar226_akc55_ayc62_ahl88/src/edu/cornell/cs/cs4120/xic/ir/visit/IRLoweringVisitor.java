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
        return String.format("tl%d", (tempCnt++));
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
//        System.out.println(node);
        ArrayList<IRStmt> flatten = new ArrayList<>();
        for (IRStmt stmt: node.stmts()){
            if (stmt instanceof IRSeq seq){
//                System.out.println("start");
//                System.out.println(node);
//                System.out.println(seq);
                flatten.addAll(seq.stmts());
            }else{
                flatten.add(stmt);
            }
        }
        return new IRSeq(flatten);
    }

    // Lower each return expressions then add Return
    private IRNode canon(IRReturn node) {
        ArrayList<IRStmt> stmts = new ArrayList<>();
        ArrayList<String> temps_strs = new ArrayList<>();

        for (IRExpr expr : node.rets()) {
            String ti = nxtTemp();
            temps_strs.add(ti);
            if (expr instanceof IRESeq eseq) {
                stmts.add(eseq.stmt());
                stmts.add(new IRMove(new IRTemp(ti),eseq.expr()));
            }else{
                stmts.add(new IRMove(new IRTemp(ti),expr));
            }
        }
        List<IRExpr> temps = new ArrayList<>();
        for (String tmp : temps_strs) {
            temps.add(new IRTemp(tmp));
        }

        stmts.add(new IRReturn(temps));
        return new IRSeq(stmts);
    }

    // Lower Move be very careful look at slides
    private IRNode canon(IRMove node) {
        IRExpr target = node.target();
        IRExpr source = node.source();
//
        if (target instanceof IRTemp){
            return tempMove(node);
        }else if (target instanceof IRMem mem){
            return moveCommute(node) ? moveNaive(mem,source) : moveGeneral(mem,source);
        }

        return node;
    }

    private IRNode tempMove(IRMove node){
        if (node.source() instanceof IRESeq ires){
            return new IRSeq(ires.stmt(),new IRMove(node.target(),ires.expr()));
        }
        return node;
    }
    // to do
    private boolean moveCommute(IRMove node){
        return false;
    }
    private IRStmt moveNaive(IRMem targ, IRExpr src){
        ArrayList<IRStmt> stmts = new ArrayList<>();
        IRExpr e1 = targ.expr();
        IRExpr e2 = src;
        if (targ.expr() instanceof IRESeq ires){
            stmts.add(ires.stmt());
            e1 = ires.expr();
        }
        if (src instanceof  IRESeq ires2){
            stmts.add(ires2.stmt());
            e2 = ires2.expr();
        }
        if (stmts.size() != 0){
            stmts.add(new IRMove(new IRMem(e1),e2));
            return new IRSeq(stmts);
        }
        return new IRMove(targ,src);
    }

    private IRStmt moveGeneral(IRMem targ, IRExpr src){
        ArrayList<IRStmt> stmts = new ArrayList<>();
        IRExpr e1 = targ.expr();
        IRExpr e2 = src;
        if (targ.expr() instanceof IRESeq ires){
            stmts.add(ires.stmt());
            e1 = ires.expr();
        }
        String t = nxtTemp();
        stmts.add(new IRMove(new IRTemp(t),e1));
        if (src instanceof  IRESeq ires2){
            stmts.add(ires2.stmt());
            e2 = ires2.expr();
        }
        stmts.add(new IRMove(new IRMem(new IRTemp(t)),e2));
        return new IRSeq(stmts);
    }

    // Create Basic Blocks And reorder all the body
    private IRNode canon(IRFuncDecl node) {
//        if (node.name().equals("_IAck_iii")) {
//            System.out.println(node.body());
//        }
        return node;
    }


    // Lift Statement that is it
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
            temps_strs.add(ti);
            if (expr instanceof IRESeq eseq) {
                stmts.add(eseq.stmt());
                stmts.add(new IRMove(new IRTemp(ti),eseq.expr()));
            }else{
                stmts.add(new IRMove(new IRTemp(ti),expr));
            }
        }

        List<IRExpr> temps = new ArrayList<>();
        for (String tmp : temps_strs) {
            temps.add(new IRTemp(tmp));
        }

        stmts.add(new IRCallStmt(node.target(), node.n_returns(), temps));
//        stmts.add(new IRMove(new IRTemp(t), new IRTemp("_RV1")));
//        System.out.println(new IRSeq(stmts));
        return new IRSeq(stmts);
    }
    // Lower each Expr we never call this lol?
    private IRNode canon(IRCall node) {
        return node;
    }

    // if commute do that otherwise do normal
    private IRNode canon(IRBinOp node) {
        return doesBinopCommunte(node) ? commuteBinop(node) : defaultBinop(node);
    }

    // to do
    private boolean doesBinopCommunte(IRBinOp node){
        return false;
    }

    private IRNode commuteBinop(IRBinOp node){
        ArrayList<IRStmt> hoisted = new ArrayList<>();
        IRExpr e1 = node.left();
        IRExpr e2 = node.right();
        if (node.left() instanceof IRESeq ires1){
            hoisted.add(ires1.stmt());
            e1 = ires1.expr();
        }
        if (node.right() instanceof  IRESeq ires2){
            hoisted.add(ires2.stmt());
            e2 = ires2.expr();
        }
        if (hoisted.size() != 0){
            return new IRESeq(new IRSeq(hoisted),new IRBinOp(node.opType(),e1,e2));
        }
        return node;
    }
    private IRNode defaultBinop(IRBinOp node){
        ArrayList<IRStmt> stmts = new ArrayList<>();
        String t1 = nxtTemp();
        if (node.left() instanceof IRESeq ires1){
            stmts.add(ires1.stmt());
            stmts.add(new IRMove(new IRTemp(t1), ires1.expr()));
        }else{
            stmts.add(new IRMove(new IRTemp(t1), node.left()));
        }
        if (node.right() instanceof IRESeq ires2){
            stmts.add(ires2.stmt());
            return new IRESeq(new IRSeq(stmts),new IRBinOp(node.opType(),new IRTemp(t1),ires2.expr()));
        }else{
            
        }
        return node;
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
