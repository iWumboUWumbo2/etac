package aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg0;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

/**
 * Class for return instruction
 */
public class ASMRet extends ASMArg0 {

    public long rets;

    /**
     * @param numRets Number of returns
     */
    public ASMRet(long numRets) {
        super(ASMOpCodes.RET);
        rets = numRets;
    }
}
