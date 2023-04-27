package aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.ir.FunctionInliningVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.InternalCompilerError;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.SExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.AbstractASMVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.AggregateVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;

import java.util.ArrayList;
import java.util.Objects;

/** An intermediate representation for a memory location MEM(e) */
public class IRMem extends IRExpr_c {
    @Override
    public ArrayList<ASMInstruction> accept(AbstractASMVisitor v) {
        return v.visit(this);
    }

    public enum MemType {
        NORMAL,
        IMMUTABLE;

        @Override
        public String toString() {
            switch (this) {
                case NORMAL:
                    return "MEM";
                case IMMUTABLE:
                    return "MEM_I";
            }
            throw new InternalCompilerError("Unknown mem type!");
        }
    };

    private IRExpr expr;
    private MemType memType;

    /** @param expr the address of this memory location */
    public IRMem(IRExpr expr) {
        this(expr, MemType.NORMAL);
    }

    public IRMem(IRExpr expr, MemType memType) {
        this.expr = expr;
        this.memType = memType;
    }

    public IRExpr expr() {
        return expr;
    }

    public MemType memType() {
        return memType;
    }

    @Override
    public String label() {
        return memType.toString();
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRExpr expr = (IRExpr) v.visit(this, this.expr);

        if (expr != this.expr) return v.nodeFactory().IRMem(expr);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(expr));
        return result;
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom(memType.toString());
        expr.printSExp(p);
        p.endList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IRMem irMem = (IRMem) o;
        return Objects.equals(expr, irMem.expr) && memType == irMem.memType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(expr, memType);
    }

    @Override
    public IRExpr accept(FunctionInliningVisitor v){
        return v.visit(this);
    }
}
