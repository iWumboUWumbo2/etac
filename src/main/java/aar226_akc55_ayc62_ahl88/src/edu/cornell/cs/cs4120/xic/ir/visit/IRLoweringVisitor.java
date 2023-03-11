package aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;

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
        return node;
    }

    // Lower each return expressions then add Return
    private IRNode canon(IRReturn node) {
        return node;
    }

    // Lower Move be very careful look at slides
    private IRNode canon(IRMove node) {
        return node;
    }

    // Create Basic Blocks And reorder all the body
    private IRNode canon(IRFuncDecl node) {
        return node;
    }


    // Lift Statement thats it
    private IRNode canon(IRExp node) {
        return node;
    }

    // do nothing
    private IRNode canon(IRCompUnit node) {
        return node;
    }

    // Lower each Expr
    private IRNode canon(IRCallStmt node) {
        return node;
    }
    // Lower each Expr
    private IRNode canon(IRCall node) {
        return node;
    }

    // if commute do that otherwise do normal
    private IRNode canon(IRBinOp node) {
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
        return null;
    }
    // Conditional Jump
    private IRNode canon(IRCJump node){

        return node;
    }
    // Jump
    private IRNode canon(IRJump node){

        return node;
    }
    // ESEQ
    private IRNode canon(IRESeq node){
        return node;
    }
}
