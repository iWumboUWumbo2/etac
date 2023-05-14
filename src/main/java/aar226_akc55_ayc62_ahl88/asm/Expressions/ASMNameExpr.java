package aar226_akc55_ayc62_ahl88.asm.Expressions;

/**
 * Class for name expr
 */
public class ASMNameExpr extends ASMAbstractReg {
    private String name;

    /**
     * @param name
     */
    public ASMNameExpr(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ASMNameExpr that = (ASMNameExpr) o;

        return name.equals(that.name);
    }
}
