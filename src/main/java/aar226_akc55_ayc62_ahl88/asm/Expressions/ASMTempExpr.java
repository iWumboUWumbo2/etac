package aar226_akc55_ayc62_ahl88.asm.Expressions;

/**
 * Class for temporary expr
 */
public class ASMTempExpr extends ASMAbstractReg{

    private String name;

    /**
     * @param tempLabel Label
     */
    public ASMTempExpr(String tempLabel){
        name = tempLabel.replaceAll("'", "__prime__");
    }

    public String getName() {
        return name;
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

        ASMTempExpr that = (ASMTempExpr) o;

        return name.equals(that.name);
    }
}
