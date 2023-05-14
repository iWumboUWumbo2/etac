package aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.InternalCompilerError;

import java.util.HashMap;

public class ReplaceTempsAndLabelVisitor extends IRVisitor{

    HashMap<String,String> tempMapping;
    HashMap<String,String> labelMapping;
    String function;
    public ReplaceTempsAndLabelVisitor(IRNodeFactory inf, HashMap<String,String> tempMapping, HashMap<String,String> labelMapping,String funcName) {
        super(inf);
        this.tempMapping = tempMapping;
        this.labelMapping = labelMapping;
        function = funcName;
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
        if (n_ instanceof IRMove irmove) return replaceTemp(irmove);
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

    private IRNode replaceTemp(IRPhi phi) {
        return phi;
    }

    private IRNode replaceTemp(IRTemp irtem) {
        if (!tempMapping.containsKey(irtem.name())){
            if (irtem.name().startsWith("_RV") || irtem.name().startsWith("_ARG") || (function.equals("_Imain_paai") && irtem.name().equals("args"))){
                return irtem;
            }else{
                throw new InternalCompilerError("temp isn't mapping: " + irtem);
            }
        }
        return new IRTemp(tempMapping.get(irtem.name()));
    }

    private IRNode replaceTemp(IRSeq irseq) {
        return irseq;
    }

    private IRNode replaceTemp(IRReturn irret) {
        return irret;
    }

    private IRNode replaceTemp(IRName irname) {
        if (labelMapping.containsKey(irname.name())){
            return new IRName(labelMapping.get(irname.name()));
        }
        return irname;
    }

    private IRNode replaceTemp(IRMove irmove) {
        return irmove;
    }

    private IRNode replaceTemp(IRMem irmem) {
        return irmem;
    }

    private IRNode replaceTemp(IRLabel irl) {
        if (!labelMapping.containsKey(irl.name())){
            throw new InternalCompilerError("label not found in mapping");
        }
        return new IRLabel(labelMapping.get(irl.name()));
    }

    private IRNode replaceTemp(IRJump irj) {
        return irj;
    }

    private IRNode replaceTemp(IRFuncDecl irfunc) {

        return irfunc;
    }

    private IRNode replaceTemp(IRExp irexp) {
        return irexp;
    }

    private IRNode replaceTemp(IRESeq ireseq) {
        return ireseq;
    }

    private IRNode replaceTemp(IRConst ic) {
        return ic;
    }

    private IRNode replaceTemp(IRCompUnit icu) {
        return icu;
    }

    private IRNode replaceTemp(IRCJump ircj) {
        if (labelMapping.containsKey(ircj.trueLabel())){
            return new IRCJump(ircj.cond(),labelMapping.get(ircj.trueLabel()));
        }
        return ircj;
    }

    private IRNode replaceTemp(IRCallStmt ircstmt) {
        return ircstmt;
    }

    private IRNode replaceTemp(IRCall irc) {
        return irc;
    }

    private IRNode replaceTemp(IRBinOp irbin) {
        return irbin;
    }


}
