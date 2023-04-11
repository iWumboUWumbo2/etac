package aar226_akc55_ayc62_ahl88.asm.Instructions.arithmetic;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg3;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMExpr;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;

public class ASMIMul extends ASMArg3 {

    // dest = v1 * v2
    public ASMIMul(ASMExpr dest, ASMExpr v1, ASMExpr v2) {
        super(ASMOpCodes.IMUL, dest, v1, v2);
    }

    // dest = dest * src
    public ASMIMul(ASMExpr dest, ASMExpr src) {
        super(ASMOpCodes.IMUL, dest, src, null);
    }

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
