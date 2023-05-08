package aar226_akc55_ayc62_ahl88.asm.Instructions.mov;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg2;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public class ASMMov extends ASMAbstractMov {
    public ASMMov(ASMExpr left, ASMExpr right) {
        super(ASMOpCodes.MOV, left, right);
    }
}
