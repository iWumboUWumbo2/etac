package aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit;

import aar226_akc55_ayc62_ahl88.Main;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.OptimizationType;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.InternalCompilerError;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.Pair;

public class ReplaceTempWithConstAndFold extends IRVisitor{


    Pair<IRTemp, IRConst> mapping;
    boolean folding;
    public ReplaceTempWithConstAndFold(IRNodeFactory inf,Pair<IRTemp,IRConst> replace) {
        super(inf);
        mapping = replace;
        folding = Main.opts.isSet(OptimizationType.CONSTANT_FOLDING);
    }

    @Override
    protected IRNode leave(IRNode parent, IRNode n, IRNode n_, IRVisitor v_) {
        if (n_ instanceof IRBinOp irbin) return replaceTempWithConst(irbin);
        if (n_ instanceof IRCall irc) return replaceTempWithConst(irc);
        if (n_ instanceof IRCallStmt ircstmt) return replaceTempWithConst(ircstmt);
        if (n_ instanceof IRCJump ircj) return replaceTempWithConst(ircj);
        if (n_ instanceof IRCompUnit icu) return replaceTempWithConst(icu);
        if (n_ instanceof IRConst ic) return replaceTempWithConst(ic);
        if (n_ instanceof IRESeq ireseq) return replaceTempWithConst(ireseq);
        if (n_ instanceof IRExp irexp) return  replaceTempWithConst(irexp);
        if (n_ instanceof IRFuncDecl irfunc) return replaceTempWithConst(irfunc);
        if (n_ instanceof IRJump irj) return replaceTempWithConst(irj);
        if (n_ instanceof IRLabel irl) return replaceTempWithConst(irl);
        if (n_ instanceof IRMem irmem) return replaceTempWithConst(irmem);
        if (n_ instanceof IRMove irmove) return replaceTempWithConst(irmove);
        if (n_ instanceof IRName irname) return replaceTempWithConst(irname);
        if (n_ instanceof IRReturn irret) return replaceTempWithConst(irret);
        if (n_ instanceof IRSeq irseq) return replaceTempWithConst(irseq);
        if (n_ instanceof IRTemp irtem) return replaceTempWithConst(irtem);
        if (n_ instanceof IRPhi phi) return replaceTempWithConst(phi);

        throw new Error("Why is node not found");
    }

    private IRNode replaceTempWithConst(IRBinOp irbin) {
        if (irbin.left().isConstant() && irbin.right().isConstant() && folding){
            try{
                long number = IRBinOp.eval(irbin.opType(),irbin.left().constant(),
                        irbin.right().constant());
                return new IRConst(number);
            }catch (InternalCompilerError ignored){
            }
        }
        return irbin;
    }
    private IRNode replaceTempWithConst(IRCall irc) {
        return irc;
    }
    private IRNode replaceTempWithConst(IRCallStmt ircstmt) {
        return ircstmt;
    }
    private IRNode replaceTempWithConst(IRCJump ircj){
        return ircj;
    }
    private IRNode replaceTempWithConst(IRCompUnit icu){
        return icu;
    }
    private IRNode replaceTempWithConst(IRConst ic){
        return ic;
    }
    private IRNode replaceTempWithConst(IRESeq ireseq){
        return ireseq;
    }
    private IRNode replaceTempWithConst(IRFuncDecl irfunc){
        return irfunc;
    }
    private IRNode replaceTempWithConst(IRExp irexp){
        return irexp;
    }
    private IRNode replaceTempWithConst(IRJump irj){
        return irj;
    }
    private  IRNode replaceTempWithConst(IRLabel irl){
        return irl;
    }
    private IRNode replaceTempWithConst(IRMem irmem){
        return irmem;
    }
    private IRNode replaceTempWithConst(IRMove irmove){
        return irmove;
    }
    private IRNode replaceTempWithConst(IRName irname){
        return irname;
    }
    private IRNode replaceTempWithConst(IRReturn irret){
        return irret;
    }
    private IRNode replaceTempWithConst(IRSeq irseq){
        return irseq;
    }
    private IRNode replaceTempWithConst(IRTemp irtem){
        if (mapping.part1().equals(irtem)){
            return mapping.part2();
        }
        return irtem;
    }
    private IRNode replaceTempWithConst(IRPhi phi){
        return phi;
    }

}
