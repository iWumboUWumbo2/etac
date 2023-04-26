package aar226_akc55_ayc62_ahl88.cfg;

import aar226_akc55_ayc62_ahl88.src.polyglot.util.InternalCompilerError;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class CFGNode<T> { // T is IRSTMT U is SET IRTEMPS
    final private int FALLTHROUGH = 0;
    final private int JUMP = 1;

    T stmt;

    private ArrayList<CFGNode<T>> children;

    private ArrayList<CFGNode<T>> predecessors;

    public CFGNode(T stmt) {
        this.predecessors = new ArrayList<>();
        this.children = new ArrayList<>(2);
        this.children.add(null);
        this.children.add(null);

        this.stmt = stmt;

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

    public ArrayList<CFGNode<T>> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<CFGNode<T>> children) {
        this.children = children;
    }

    public CFGNode<T> getFallThroughChild() {
        return children.get(FALLTHROUGH);
    }

    public void setFallThroughChild(CFGNode<T> fallThroughChild) {
        this.children.set(FALLTHROUGH, fallThroughChild) ;
    }

    public CFGNode<T> getJumpChild() {
        return children.get(JUMP);
    }

    public void setJumpChild(CFGNode<T> jumpChild) {
        children.set(JUMP, jumpChild);
    }

    public ArrayList<CFGNode<T>> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(ArrayList<CFGNode<T>> predecessors) {
        this.predecessors = predecessors;
    }
    @Override
    public String toString() {
        return stmt.toString();
    }

    public void removePredecessor(CFGNode<T> pred) {
        predecessors.remove(pred);
    }
}
