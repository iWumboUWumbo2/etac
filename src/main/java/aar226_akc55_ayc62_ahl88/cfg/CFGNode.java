package aar226_akc55_ayc62_ahl88.cfg;

import aar226_akc55_ayc62_ahl88.src.polyglot.util.InternalCompilerError;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class CFGNode<T> {
    T stmt;
    private CFGNode<T> fallThroughChild, jumpChild;



    private ArrayList<CFGNode<T>> predecessors;

    private HashSet<T> in, out, def, use;

    public CFGNode(T stmt) {
        this.predecessors = new ArrayList<>();

        this.stmt = stmt;
        fallThroughChild = null;
        jumpChild = null;
        in = new HashSet<>();
        out = new HashSet<>();
        def = new HashSet<>();
        use = new HashSet<>();
    }
    @Override
    public int hashCode() {
        return Objects.hash(stmt, fallThroughChild, jumpChild, predecessors, in, out, def, use);
    }

    public void addPredecessor(CFGNode<T> pred) {
        if (predecessors.contains(pred)) {
            throw new InternalCompilerError("UR TRYING TO ADD PRED THAT ALREADY EXISTS");
        }
        predecessors.add(pred);
    }

    public T getStmt() {
        return stmt;
    }

    public void setStmt(T stmt) {
        this.stmt = stmt;
    }

    public CFGNode<T> getFallThroughChild() {
        return fallThroughChild;
    }

    public void setFallThroughChild(CFGNode<T> fallThroughChild) {
        this.fallThroughChild = fallThroughChild;
    }

    public CFGNode<T> getJumpChild() {
        return jumpChild;
    }

    public void setJumpChild(CFGNode<T> jumpChild) {
        this.jumpChild = jumpChild;
    }

    public ArrayList<CFGNode<T>> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(ArrayList<CFGNode<T>> predecessors) {
        this.predecessors = predecessors;
    }

    public HashSet<T> getIn() {
        return in;
    }

    public void setIn(HashSet<T> in) {
        this.in = in;
    }

    public HashSet<T> getOut() {
        return out;
    }

    public void setOut(HashSet<T> out) {
        this.out = out;
    }

    public HashSet<T> getDef() {
        return def;
    }

    public void setDef(HashSet<T> def) {
        this.def = def;
    }

    public HashSet<T> getUse() {
        return use;
    }

    public void setUse(HashSet<T> use) {
        this.use = use;
    }

    @Override
    public String toString() {
        return stmt.toString();
    }
}
