package aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg0;

/**
 * Class for leave instruction
 */
public class ASMLeave extends ASMArg0 {

    /**
     * Exit stack frame
     */
    public ASMLeave(){
        super(ASMOpCodes.LEAVE);
    }
}
