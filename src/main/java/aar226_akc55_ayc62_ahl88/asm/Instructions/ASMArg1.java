package aar226_akc55_ayc62_ahl88.asm.Instructions;

import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

import java.util.HashMap;

public abstract class ASMArg1 extends ASMInstruction {

    private ASMExpr left;
    private String leftPrint;
    public ASMArg1(ASMOpCodes op, ASMExpr arg1){
        super(op);
        left = arg1;
    }
    @Override
    public void createPrint(HashMap<String, Integer> location) {
        leftPrint = exprASMToString(left,location);
    }
    @Override
    public String toString(){
        return opCodeToString() + leftPrint;
    }
    public ASMExpr getLeft() {
        return left;
    }
}
