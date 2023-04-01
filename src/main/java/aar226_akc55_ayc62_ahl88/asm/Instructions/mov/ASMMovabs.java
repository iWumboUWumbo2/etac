package aar226_akc55_ayc62_ahl88.asm.Instructions.mov;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg2;

public class ASMMovabs extends ASMArg2 {
    public ASMMovabs(ASMExpr left, ASMExpr right) {
        super(ASMOpCodes.MOVABS, left, right);
    }
}