package aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.ir.FunctionInliningVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.SExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.AbstractASMVisitor;

import java.util.ArrayList;
import java.util.Objects;

/** An intermediate representation for named memory address NAME(n) */
public class IRName extends IRExpr_c {
    private String name;

    /** @param name name of this memory address */
    public IRName(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public String label() {
        return "NAME(" + name + ")";
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("NAME");
        p.printAtom(name);
        p.endList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IRName irName = (IRName) o;
        return Objects.equals(name, irName.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public ArrayList<ASMInstruction> accept(AbstractASMVisitor v) {
        return v.visit(this);
    }
    @Override
    public IRExpr accept(FunctionInliningVisitor v){
        return v.visit(this);
    }
}
