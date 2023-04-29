package aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.ir.FunctionInliningVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.SExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.AbstractASMVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.AggregateVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.CheckCanonicalIRVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IRPhi extends IRStmt{

    protected IRExpr target;

    protected List<IRExpr> args;

    public IRPhi(IRExpr target, List<IRExpr> src) {
        this.target =  target;
        this.args = src;
    }

    public IRExpr getTarget() {
        return target;
    }

    public List<IRExpr> getArgs() {
        return args;
    }
    @Override
    public String label() {
        return "PHI";
    }
    @Override
    public IRNode visitChildren(IRVisitor v) {
        boolean modified = false;

        IRExpr target = (IRExpr) v.visit(this, this.target);
        if (target != this.target) modified = true;

        List<IRExpr> results = new ArrayList<>(args.size());
        for (IRExpr arg : args) {
            IRExpr newExpr = (IRExpr) v.visit(this, arg);
            if (newExpr != arg) modified = true;
            results.add(newExpr);
        }

        if (modified) return v.nodeFactory().IRPhi(target,results);

        return this;
    }
    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(target));
        for (IRExpr arg : args) result = v.bind(result, v.visit(arg));
        return result;
    }
    @Override
    public boolean isCanonical(CheckCanonicalIRVisitor v) {
        return !v.inExpr();
    }
    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("PHI");
        target.printSExp(p);
        for (IRExpr arg : args) arg.printSExp(p);
        p.endList();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IRPhi irPhi = (IRPhi) o;
        return Objects.equals(target, irPhi.target) && Objects.equals(args, irPhi.args);
    }
    @Override
    public int hashCode() {
        return Objects.hash(target, args);
    }
    @Override
    public IRNode accept(FunctionInliningVisitor v) {
        return v.visit(this);
    }
    @Override
    public ArrayList<ASMInstruction> accept(AbstractASMVisitor v) {
        return v.visit(this);
    }
}
