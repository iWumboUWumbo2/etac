package aar226_akc55_ayc62_ahl88.asm.Opts;

import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMAbstractReg;

import java.util.Objects;

public class GraphColoringTempNode {

    public ASMAbstractReg regName;

    public WorkListEnum member;

    public GraphColoringTempNode(ASMAbstractReg reg){
        regName = reg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GraphColoringTempNode that = (GraphColoringTempNode) o;

        if (!Objects.equals(regName, that.regName)) return false;
        return member == that.member;
    }

    @Override
    public int hashCode() {
        int result = regName != null ? regName.hashCode() : 0;
        result = 31 * result + (member != null ? member.hashCode() : 0);
        return result;
    }
}
