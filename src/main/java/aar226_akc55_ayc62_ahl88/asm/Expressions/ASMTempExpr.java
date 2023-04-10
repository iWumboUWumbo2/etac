package aar226_akc55_ayc62_ahl88.asm.Expressions;


public class ASMTempExpr extends ASMAbstractReg{

    private String name;
    public ASMTempExpr(String tempLabel){
        name = tempLabel;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
