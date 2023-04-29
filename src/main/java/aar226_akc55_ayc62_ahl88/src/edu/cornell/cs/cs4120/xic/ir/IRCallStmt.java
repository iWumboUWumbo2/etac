package aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.ir.FunctionInliningVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.SExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.AbstractASMVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.AggregateVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.CheckCanonicalIRVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * An intermediate representation for a call statement. t_1, t_2, _, t_4 = CALL(e_target, e_1, ...,
 * e_n) where n = n_returns.
 */
public class IRCallStmt extends IRStmt {
    protected IRExpr target;
    protected List<IRExpr> args;
    protected Long n_returns;

    /**
     * @param target address of the code for this function call
     * @param args arguments of this function call
     */
    public IRCallStmt(IRExpr target, Long n_returns, IRExpr... args) {
        this(target, n_returns, Arrays.asList(args));
    }

    /**
     * @param target address of the code for this function call
     * @param args arguments of this function call
     */
    public IRCallStmt(IRExpr target, Long n_returns, List<IRExpr> args) {
        this.target = target;
        this.args = args;
        this.n_returns = n_returns;
    }

    public IRExpr target() {
        return target;
    }

    public List<IRExpr> args() {
        return args;
    }

    public Long n_returns() {
        return n_returns;
    }

    @Override
    public String label() {
        return "CALL_STMT";
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

        if (modified) return v.nodeFactory().IRCallStmt(target, n_returns, results);

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
        p.printAtom("CALL_STMT");
        p.printAtom(Long.toString(n_returns));
        target.printSExp(p);
        for (IRExpr arg : args) arg.printSExp(p);
        p.endList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IRCallStmt that = (IRCallStmt) o;
        return Objects.equals(target, that.target) && Objects.equals(args, that.args) && Objects.equals(n_returns, that.n_returns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, args, n_returns);
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
