package aar226_akc55_ayc62_ahl88.asm;


public class ASMTemp extends ASMExpr {

    private String name;
    public ASMTemp(String tempLabel){
        name = tempLabel;
    }

    public String getName() {
        return name;
    }
}
