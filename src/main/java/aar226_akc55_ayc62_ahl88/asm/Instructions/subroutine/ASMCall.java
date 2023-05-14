package aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg1;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

/**
 * Class for function call instruction
 */
public class ASMCall extends ASMArg1 {

    public long numParams;
    public long numReturns;

    /**
     * @param label Label of function
     * @param param Number of parameters
     * @param rets Number of returns
     */
    public ASMCall(ASMExpr label,long param, long rets) {
        super(ASMOpCodes.CALL, label);
        numParams = param;
        numReturns = rets;
    }
}