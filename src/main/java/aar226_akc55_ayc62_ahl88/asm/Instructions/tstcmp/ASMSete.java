package aar226_akc55_ayc62_ahl88.asm.Instructions.tstcmp;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg1;

public class ASMSete extends ASMArg1 {
    public ASMSete(ASMExpr arg){
        super(ASMOpCodes.SETE,arg);
    }
}
