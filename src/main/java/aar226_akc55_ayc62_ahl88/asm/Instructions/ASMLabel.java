package aar226_akc55_ayc62_ahl88.asm.Instructions;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

import java.util.HashMap;

public class ASMLabel extends ASMInstruction {
    String label;

    public ASMLabel(String label) {
        super(ASMOpCodes.LABEL);
        this.label = label;
    }
    @Override
    public void createPrint(HashMap<String, Integer> location) {
    }
    @Override
    public String toString(){
        return opCodeToString() + label+":";
    }
}
