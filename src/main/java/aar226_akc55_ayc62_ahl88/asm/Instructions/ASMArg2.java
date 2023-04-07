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
    private String leftPrint;
    private String rightPrint;
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

    public String getLeftPrint() {
        return leftPrint;
    }

    public String getRightPrint() {
        return rightPrint;
    }

    @Override
    public void createPrint(HashMap<String, Integer> location) {
        leftPrint = exprASMToString(left,location);
        rightPrint = exprASMToString(right, location);
    }

    @Override
    public ArrayList<ASMInstruction> accept(RegisterAllocationTrivialVisitor regVisitor) {
        return regVisitor.visit(this);
    }


    @Override
    public String toString(){
        return opCodeToString() + left +", " + right;
    }

}
