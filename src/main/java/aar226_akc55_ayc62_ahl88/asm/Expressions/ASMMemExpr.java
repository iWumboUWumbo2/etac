package aar226_akc55_ayc62_ahl88.asm.Expressions;

import aar226_akc55_ayc62_ahl88.src.polyglot.util.InternalCompilerError;

public class ASMMemExpr extends ASMExpr{
    public ASMExpr mem;
    public ASMMemExpr(ASMExpr mem) {
        if (mem instanceof ASMMemExpr){
            throw new InternalCompilerError("memory operand inside of memory");
        }
        this.mem = mem;
    }

    public ASMExpr getMem() {
        return mem;
    }

    @Override
    public String toString() {
        if (mem == null) {
            throw new RuntimeException("mem should not be null");
        }
        return "QWORD PTR [ " + mem.toString() +" ]";
    }
}
