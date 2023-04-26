package aar226_akc55_ayc62_ahl88.cfg;

import aar226_akc55_ayc62_ahl88.src.polyglot.util.InternalCompilerError;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class CFGNode<T, U> {
    final private int FALLTHROUGH = 0;
    final private int JUMP = 1;

    T stmt;

    private ArrayList<CFGNode<T, U>> children;

    private ArrayList<CFGNode<T, U>> predecessors;

    private HashSet<U> in, out, def, use;

    public CFGNode(T stmt) {
        this.predecessors = new ArrayList<>();
        this.children = new ArrayList<>(2);
        this.children.add(null);
        this.children.add(null);

        this.stmt = stmt;

        in = new HashSet<>();
        out = new HashSet<>();
        def = new HashSet<>();
        use = new HashSet<>();
    }
//    @Override
//    public int hashCode() {
//        return Objects.hash(stmt, children, predecessors, in, out, def, use);
//    }

    public void addPredecessor(CFGNode<T, U> pred) {
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

    public ArrayList<CFGNode<T, U>> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<CFGNode<T, U>> children) {
        this.children = children;
    }

    public CFGNode<T, U> getFallThroughChild() {
        return children.get(FALLTHROUGH);
    }

    public void setFallThroughChild(CFGNode<T, U> fallThroughChild) {
        this.children.set(FALLTHROUGH, fallThroughChild) ;
    }

    public CFGNode<T, U> getJumpChild() {
        return children.get(JUMP);
    }

    public void setJumpChild(CFGNode<T, U> jumpChild) {
        children.set(JUMP, jumpChild);
    }

    public ArrayList<CFGNode<T, U>> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(ArrayList<CFGNode<T, U>> predecessors) {
        this.predecessors = predecessors;
    }

    public HashSet<U> getIn() {
        return in;
    }

    public void setIn(HashSet<U> in) {
        this.in = in;
    }

    public HashSet<U> getOut() {
        return out;
    }

    public void setOut(HashSet<U> out) {
        this.out = out;
    }

    public HashSet<U> getDef() {
        return def;
    }

    public void setDef(HashSet<U> def) {
        this.def = def;
    }

    public HashSet<U> getUse() {
        return use;
    }

    public void setUse(HashSet<U> use) {
        this.use = use;
    }

    @Override
    public String toString() {
        return stmt.toString();
    }

    public void removePredecessor(CFGNode<T, U> pred) {
        predecessors.remove(pred);
    }
}
