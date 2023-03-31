package aar226_akc55_ayc62_ahl88.asm.Instructions;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public abstract class ASMInstruction {
    private ASMOpCodes opCode;


    public ASMInstruction(ASMOpCodes op){
        opCode = op;
    }
}
