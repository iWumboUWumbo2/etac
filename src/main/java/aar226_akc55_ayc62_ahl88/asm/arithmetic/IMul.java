package aar226_akc55_ayc62_ahl88.asm.arithmetic;

import aar226_akc55_ayc62_ahl88.asm.ASMArg3;
import aar226_akc55_ayc62_ahl88.asm.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public class IMul extends ASMArg3 {

    // dest = v1 * v2
    public IMul(ASMExpr dest, ASMExpr v1, ASMExpr v2) {
        super(ASMOpCodes.IMUL, dest, v1, v2);
    }

    // dest = dest * src
    public IMul(ASMExpr dest, ASMExpr src) {
        super(ASMOpCodes.IMUL, dest, dest, src);
    }


}
