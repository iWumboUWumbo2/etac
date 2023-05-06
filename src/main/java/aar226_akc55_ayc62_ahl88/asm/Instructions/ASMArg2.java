package aar226_akc55_ayc62_ahl88.asm.Instructions;

import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMRegisterExpr;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMTempExpr;
import aar226_akc55_ayc62_ahl88.asm.visit.RegisterAllocationTrivialVisitor;

import java.util.ArrayList;
import java.util.HashMap;

public class ASMArg2 extends ASMInstruction {

    private ASMExpr left;
    private ASMExpr right;
    public ASMArg2(ASMOpCodes op, ASMExpr arg1, ASMExpr arg2){
        super(op);
        left = arg1;
        right = arg2;
    }

    public ASMExpr getLeft() {
        return left;
    }

    public ASMExpr getRight() {
        return right;
    }

    @Override
    public ArrayList<ASMInstruction> accept(RegisterAllocationTrivialVisitor regVisitor) {
        return regVisitor.visit(this);
    }


    @Override
    public String toString(){
        return opCodeToString() + left +", " + right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ASMArg2 arg2 = (ASMArg2) o;

        if (!left.equals(arg2.left)) return false;
        return right.equals(arg2.right);
    }

    @Override
    public int hashCode() {
        int result = left.hashCode();
        result = 31 * result + right.hashCode();
        return result;
    }
}
