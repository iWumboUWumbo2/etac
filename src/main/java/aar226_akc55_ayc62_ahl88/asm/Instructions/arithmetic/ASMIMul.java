package aar226_akc55_ayc62_ahl88.asm.Instructions.arithmetic;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg3;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

/**
 * Class for multiplication instruction
 */
public class ASMIMul extends ASMArg3 {

    /**
     * @param dest destination
     * @param v1 left
     * @param v2 right
     * dest = v1 * v2
     */
    public ASMIMul(ASMExpr dest, ASMExpr v1, ASMExpr v2) {
        super(ASMOpCodes.IMUL, dest, v1, v2);
    }

    /**
     * @param dest destination
     * @param src source
     * dest = dest * src
     */
    //
    public ASMIMul(ASMExpr dest, ASMExpr src) {
        super(ASMOpCodes.IMUL, dest, src, null);
    }

    /**
     * @param src source
     * rax = rax * src
     */
    public ASMIMul(ASMExpr src) {
        super(ASMOpCodes.IMUL, src, null, null);
    }

    @Override
    public String toString(){
        if (getA2() == null && getA3() == null){
            return opCodeToString() + getA1();
        }else if (getA3() == null){
            return opCodeToString() + getA1()+ ", "+getA2();
        }else{
            return opCodeToString() + getA1()+ ", "+getA2() + ", "+getA3();
        }
    }
}
