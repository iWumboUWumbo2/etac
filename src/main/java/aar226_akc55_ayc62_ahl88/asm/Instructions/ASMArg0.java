package aar226_akc55_ayc62_ahl88.asm.Instructions;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.visit.RegisterAllocationTrivialVisitor;
import java.util.ArrayList;

/**
 * Class for 0 argument instructions
 */
public class ASMArg0 extends ASMInstruction{

    /**
     * @param op Opcode
     */
    public ASMArg0(ASMOpCodes op){
        super(op);
    }
    @Override
    public String toString(){
        return opCodeToString();
    }
    @Override
    public ArrayList<ASMInstruction> accept(RegisterAllocationTrivialVisitor regVisitor) {
        return regVisitor.visit(this);
    }

}
