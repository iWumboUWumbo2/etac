package aar226_akc55_ayc62_ahl88.asm.Instructions;

import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.visit.RegisterAllocationTrivialVisitor;
import java.util.ArrayList;

/**
 * Class for 2 argument instructions
 */
public class ASMArg2 extends ASMInstruction {
    private ASMExpr left;
    private ASMExpr right;

    /**
     * @param op Opcode
     * @param arg1
     * @param arg2
     */
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
}
