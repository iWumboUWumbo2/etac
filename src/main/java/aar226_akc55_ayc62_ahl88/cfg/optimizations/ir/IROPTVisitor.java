package aar226_akc55_ayc62_ahl88.cfg.optimizations.ir;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;

public interface IROPTVisitor<T> {

    T visit(IRBinOp node);

    T visit(IRCall node);

    T visit(IRCallStmt node);

    T visit(IRCJump node);

    T visit(IRCompUnit node);

    T visit(IRConst node);

    T visit(IRESeq node);

    T visit(IRExp node);

    T visit(IRFuncDecl node);

    T visit(IRJump node);

    T visit(IRLabel node);

    T visit(IRMem node);

    T visit(IRMove node);

    T visit(IRName node);


    T visit(IRReturn node);

    T visit(IRSeq node);

    T visit(IRTemp node);



}
