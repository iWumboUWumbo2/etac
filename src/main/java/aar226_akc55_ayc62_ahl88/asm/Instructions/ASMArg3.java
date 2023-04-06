package aar226_akc55_ayc62_ahl88.asm.Instructions;

import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

import java.util.HashMap;

public abstract class ASMArg3 extends ASMInstruction {
    private ASMExpr a1;
    private ASMExpr a2;
    private ASMExpr a3;

    private String a1Print;
    private String a2Print;
    private String a3Print;
    public ASMArg3(ASMOpCodes op, ASMExpr arg1, ASMExpr arg2, ASMExpr arg3){
        super(op);
        a1 = arg1;
        a2 = arg2;
        a3 = arg3;
    }

    @Override
    public void createPrint(HashMap<String, Integer> location) {
        a1Print = exprASMToString(a1,location);
        a2Print = exprASMToString(a2, location);
        a3Print = exprASMToString(a3, location);
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
        return opCodeToString() +a1Print + ", "+a2Print + ", "+a3Print;
    }
}
