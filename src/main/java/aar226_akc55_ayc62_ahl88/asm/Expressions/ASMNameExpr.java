package aar226_akc55_ayc62_ahl88.asm.Expressions;

public class ASMNameExpr extends ASMAbstractReg {
    private String name;
    public ASMNameExpr(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
