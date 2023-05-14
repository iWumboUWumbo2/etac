package aar226_akc55_ayc62_ahl88.asm.Expressions;

import aar226_akc55_ayc62_ahl88.src.polyglot.util.InternalCompilerError;

/**
 * Class for mem expr.
 */
public class ASMMemExpr extends ASMExpr{
    public ASMExpr mem;

    /**
     * @param mem Mem expr
     */
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
        if (mem instanceof  ASMNameExpr name){
            return "QWORD PTR " + name + "[rip] ";
        }
        return "QWORD PTR [ " + mem +" ]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ASMMemExpr that = (ASMMemExpr) o;

        return mem.equals(that.mem);
    }

    @Override
    public int hashCode() {
        return mem.hashCode();
    }
}
