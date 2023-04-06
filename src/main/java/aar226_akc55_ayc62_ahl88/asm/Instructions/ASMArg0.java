package aar226_akc55_ayc62_ahl88.asm.Instructions;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

import java.util.HashMap;

public abstract class ASMArg0 extends ASMInstruction{
    public ASMArg0(ASMOpCodes op){
        super(op);
    }
    @Override
    public void createPrint(HashMap<String, Integer> location) {
    }
    @Override
    public String toString(){
        return opCodeToString();
    }
}
