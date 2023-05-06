package aar226_akc55_ayc62_ahl88.asm.Instructions;

import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.visit.RegisterAllocationTrivialVisitor;

import java.util.ArrayList;
import java.util.HashMap;

public class ASMArg1 extends ASMInstruction {

    private ASMExpr left;
    public ASMArg1(ASMOpCodes op, ASMExpr arg1){
        super(op);
        left = arg1;
    }
    @Override
    public String toString(){
        return opCodeToString() + left;
    }
    public ASMExpr getLeft() {
        return left;
    }
    @Override
    public ArrayList<ASMInstruction> accept(RegisterAllocationTrivialVisitor regVisitor) {
        return regVisitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ASMArg1 asmArg1 = (ASMArg1) o;

        return left.equals(asmArg1.left);
    }

    @Override
    public int hashCode() {
        return left.hashCode();
    }
}
