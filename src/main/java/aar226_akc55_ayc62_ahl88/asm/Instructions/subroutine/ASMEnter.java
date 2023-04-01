package aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg2;

public class ASMEnter extends ASMArg2 {

    public ASMEnter(ASMExpr siz, ASMExpr offset){
        super(ASMOpCodes.ENTER, siz,offset);
    }
}
