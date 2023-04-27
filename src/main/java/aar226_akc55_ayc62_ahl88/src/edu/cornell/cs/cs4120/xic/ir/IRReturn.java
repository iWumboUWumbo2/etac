package aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.ir.FunctionInliningVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.SExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.AbstractASMVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.AggregateVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/** RETURN statement */
public class IRReturn extends IRStmt {
    protected List<IRExpr> rets;

    public IRReturn() {
        this(new ArrayList<>());
    }

    /** @param rets values to return */
    public IRReturn(IRExpr... rets) {
        this(Arrays.asList(rets));
    }

    /** @param rets values to return */
    public IRReturn(List<IRExpr> rets) {
        this.rets = rets;
    }

    public List<IRExpr> rets() {
        return rets;
    }

    @Override
    public String label() {
        return "RETURN";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        boolean modified = false;

        List<IRExpr> results = new ArrayList<>(rets.size());

        for (IRExpr ret : rets) {
            IRExpr newExpr = (IRExpr) v.visit(this, ret);
            if (newExpr != ret) modified = true;
            results.add(newExpr);
        }

        if (modified) return v.nodeFactory().IRReturn(results);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        for (IRExpr ret : rets) result = v.bind(result, v.visit(ret));
        return result;
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("RETURN");
        for (IRExpr ret : rets) ret.printSExp(p);
        p.endList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IRReturn irReturn = (IRReturn) o;
        return Objects.equals(rets, irReturn.rets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rets);
    }

    @Override
    public IRStmt accept(FunctionInliningVisitor v) {
        return v.visit(this);
    }

    @Override
    public ArrayList<ASMInstruction> accept(AbstractASMVisitor v) {
        return v.visit(this);
    }
}
