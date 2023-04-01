package aar226_akc55_ayc62_ahl88.asm.Instructions.tstcmp;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg2;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public class ASMCmp extends ASMArg2 {
    public ASMCmp(ASMExpr arg1, ASMExpr arg2){
        super(ASMOpCodes.CMP,arg1,arg2);
    }
}