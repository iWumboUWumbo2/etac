package aar226_akc55_ayc62_ahl88.asm.Expressions;


public class ASMTempExpr extends ASMExpr {

    private String name;
    public ASMTempExpr(String tempLabel){
        name = tempLabel;
    }

    public String getName() {
        return name;
    }
}
