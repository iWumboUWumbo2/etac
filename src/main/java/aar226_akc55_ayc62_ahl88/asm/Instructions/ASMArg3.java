package aar226_akc55_ayc62_ahl88.asm.Instructions;

import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.visit.RegisterAllocationTrivialVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ASMArg3 extends ASMInstruction {
    private ASMExpr a1;
    private ASMExpr a2;
    private ASMExpr a3;
    public ASMArg3(ASMOpCodes op, ASMExpr arg1, ASMExpr arg2, ASMExpr arg3){
        super(op);
        a1 = arg1;
        a2 = arg2;
        a3 = arg3;
    }

    public ASMExpr getA1() {
        return a1;
    }

    public ASMExpr getA2() {
        return a2;
    }

    public ASMExpr getA3() {
        return a3;
    }

    @Override
    public String toString(){
        return opCodeToString() +a1 + ", "+a2 + ", "+a3;
    }
    @Override
    public ArrayList<ASMInstruction> accept(RegisterAllocationTrivialVisitor regVisitor) {
        return regVisitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ASMArg3 asmArg3 = (ASMArg3) o;

        if (!a1.equals(asmArg3.a1)) return false;
        if (!Objects.equals(a2, asmArg3.a2)) return false;
        return Objects.equals(a3, asmArg3.a3);
    }

    @Override
    public int hashCode() {
        int result = a1.hashCode();
        result = 31 * result + (a2 != null ? a2.hashCode() : 0);
        result = 31 * result + (a3 != null ? a3.hashCode() : 0);
        return result;
    }
}
