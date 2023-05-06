package aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;

import java.util.HashMap;

public class CopyPropReplaceVisitor extends IRVisitor{

    HashMap<String,String> tempMapping;
    public CopyPropReplaceVisitor(IRNodeFactory inf, HashMap<String,String> tempMapping) {
        super(inf);
        this.tempMapping = tempMapping;
    }
    @Override
    protected IRNode leave(IRNode parent, IRNode n, IRNode n_, IRVisitor v_) {
        if (n_ instanceof IRBinOp irbin) return replaceTemp(irbin);
        if (n_ instanceof IRCall irc) return replaceTemp(irc);
        if (n_ instanceof IRCallStmt ircstmt) return replaceTemp(ircstmt);
        if (n_ instanceof IRCJump ircj) return replaceTemp(ircj);
        if (n_ instanceof IRCompUnit icu) return replaceTemp(icu);
        if (n_ instanceof IRConst ic) return replaceTemp(ic);
        if (n_ instanceof IRESeq ireseq) return replaceTemp(ireseq);
        if (n_ instanceof IRExp irexp) return  replaceTemp(irexp);
        if (n_ instanceof IRFuncDecl irfunc) return replaceTemp(irfunc);
        if (n_ instanceof IRJump irj) return replaceTemp(irj);
        if (n_ instanceof IRLabel irl) return replaceTemp(irl);
        if (n_ instanceof IRMem irmem) return replaceTemp(irmem);
        if (n_ instanceof IRMove irmove) return replaceTemp(irmove, (IRMove) n);
        if (n_ instanceof IRName irname) return replaceTemp(irname);
        if (n_ instanceof IRReturn irret) return replaceTemp(irret);
        if (n_ instanceof IRSeq irseq) return replaceTemp(irseq);
        if (n_ instanceof IRTemp irtem) return replaceTemp(irtem);
        if (n_ instanceof IRPhi phi) return replaceTemp(phi);
        if (n_ instanceof IRdud dud) return replaceTemp(dud);
        throw new Error("Why is node not found");
    }

    private IRNode replaceTemp(IRdud dud) {
        return dud;
    }

    private IRNode replaceTemp(IRBinOp irbin) {
        return irbin;
    }
    private IRNode replaceTemp(IRCall irc) {
        return irc;
    }
    private IRNode replaceTemp(IRCallStmt ircstmt) {
        return ircstmt;
    }
    private IRNode replaceTemp(IRCJump ircj){
        return ircj;
    }
    private IRNode replaceTemp(IRCompUnit icu){
        return icu;
    }
    private IRNode replaceTemp(IRConst ic){
        return ic;
    }
    private IRNode replaceTemp(IRESeq ireseq){
        return ireseq;
    }
    private IRNode replaceTemp(IRFuncDecl irfunc){
        return irfunc;
    }
    private IRNode replaceTemp(IRExp irexp){
        return irexp;
    }
    private IRNode replaceTemp(IRJump irj){
        return irj;
    }
    private  IRNode replaceTemp(IRLabel irl){
        return irl;
    }
    private IRNode replaceTemp(IRMem irmem){
        return irmem;
    }
    private IRNode replaceTemp(IRMove irmove, IRMove oldIrMove){
        return new IRMove(oldIrMove.target(),irmove.source());
    }
    private IRNode replaceTemp(IRName irname){
        return irname;
    }
    private IRNode replaceTemp(IRReturn irret){
        return irret;
    }
    private IRNode replaceTemp(IRSeq irseq){
        return irseq;
    }
    private IRNode replaceTemp(IRTemp irtem){
        if (tempMapping.containsKey(irtem.name())){
            return new IRTemp(tempMapping.get(irtem.name()));
        }
        return irtem;
    }
    private IRNode replaceTemp(IRPhi phi){
        return phi;
    }

}
