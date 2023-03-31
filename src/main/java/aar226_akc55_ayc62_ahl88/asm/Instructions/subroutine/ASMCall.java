package aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg1;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public class ASMCall extends ASMArg1 {
    public ASMCall(ASMExpr label) {
        super(ASMOpCodes.CALL, label);
    }
}