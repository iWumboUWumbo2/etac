package aar226_akc55_ayc62_ahl88.asm.Expressions;

public class ASMRegisterExpr extends ASMAbstractReg{
    private String registerName;
    public ASMRegisterExpr (String name) {
        this.registerName = name;
    }

    public String getRegisterName() {
        return registerName;
    }

    @Override
    public String toString() {
        return registerName;
    }
    @Override
    public int hashCode() {
        return registerName.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ASMRegisterExpr that = (ASMRegisterExpr) o;

        return registerName.equals(that.registerName);
    }
}
