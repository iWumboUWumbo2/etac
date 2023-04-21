package aar226_akc55_ayc62_ahl88.cfg.optimizations.ir;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;

public class FunctionInliningVisitor {
    public boolean isRecursive(IRFuncDecl funcDecl) {
        String funcName = funcDecl.name();

        if (funcDecl.body() instanceof IRSeq funcSeq) {
            for (IRStmt stmt : funcSeq.stmts()) {
                if (stmt instanceof IRCallStmt call &&
                        call.target() instanceof IRName callName && callName.equals(funcName)) {
                    return true;
                }
            }
        }

        return false;
    }
}
