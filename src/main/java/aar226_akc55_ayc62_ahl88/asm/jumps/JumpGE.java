package aar226_akc55_ayc62_ahl88.asm.jumps;

import aar226_akc55_ayc62_ahl88.asm.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public class JumpGE extends AbstractJump{

    public JumpGE(ASMExpr arg1){
        super(ASMOpCodes.JGE, arg1);
    }
}