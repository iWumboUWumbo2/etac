package aar226_akc55_ayc62_ahl88.asm.jumps;

import aar226_akc55_ayc62_ahl88.asm.ASMArg1;
import aar226_akc55_ayc62_ahl88.asm.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public abstract class AbstractJump extends ASMArg1 {


    public AbstractJump(ASMOpCodes op, ASMExpr arg1){
        super(op,arg1);
    }

}
