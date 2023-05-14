package aar226_akc55_ayc62_ahl88.asm.Instructions;

import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.visit.RegisterAllocationTrivialVisitor;
import java.util.ArrayList;

/**
 * Class for 3 argument instructions
 */
public class ASMArg3 extends ASMInstruction {
    private ASMExpr a1;
    private ASMExpr a2;
    private ASMExpr a3;

    /**
     * @param op Opcode
     * @param arg1
     * @param arg2
     * @param arg3
     */
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
}
