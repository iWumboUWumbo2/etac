package aar226_akc55_ayc62_ahl88.asm.jumps;

import aar226_akc55_ayc62_ahl88.asm.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public class JumpLT extends AbstractJump{

    public JumpLT(ASMExpr arg1){
        super(ASMOpCodes.JL,arg1);
    }
}