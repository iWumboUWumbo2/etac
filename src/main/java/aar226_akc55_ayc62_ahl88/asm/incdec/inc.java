package aar226_akc55_ayc62_ahl88.asm.incdec;

import aar226_akc55_ayc62_ahl88.asm.ASMArg1;
import aar226_akc55_ayc62_ahl88.asm.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public class inc extends ASMArg1 {
    public inc(ASMExpr arg1){
        super(ASMOpCodes.INC, arg1);
    }
}
