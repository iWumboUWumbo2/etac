package aar226_akc55_ayc62_ahl88.cfg.optimizations.BasicBlocks;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;

import java.util.ArrayList;
import java.util.HashSet;

public class BasicBlockCFG {

    final private int FALLTHROUGH = 0;
    final private int JUMP = 1;


    HashSet<String> originLabels;

    HashSet<String> destLabels;
    ArrayList<IRStmt> body;

    private ArrayList<BasicBlockCFG> children;

    private ArrayList<BasicBlockCFG> predecessors;

    public BasicBlockCFG(){
        this.originLabels = new HashSet<>();
        this.destLabels = new HashSet<>();
        this.predecessors = new ArrayList<>();
        this.children = new ArrayList<>(2);
        this.children.add(null);
        this.children.add(null);
        this.body = new ArrayList<>();
    }

    public ArrayList<IRStmt> getBody() {return body; }

    public BasicBlockCFG getFallThroughChild() {
        return children.get(FALLTHROUGH);
    }

    public void setFallThroughChild(BasicBlockCFG fallThroughChild) {
        this.children.set(FALLTHROUGH, fallThroughChild) ;
    }

    public BasicBlockCFG getJumpChild() {
        return children.get(JUMP);
    }

    public void setJumpChild(BasicBlockCFG jumpChild) {
        children.set(JUMP, jumpChild);
    }
    public ArrayList<BasicBlockCFG> getChildren() {
        return children;
    }

    public ArrayList<BasicBlockCFG> getPredecessors() {
        return predecessors;
    }
    public void removePredecessor(BasicBlockCFG pred) {
        predecessors.remove(pred);
    }
    public void addPredecessor(BasicBlockCFG pred) {
        if (predecessors.contains(pred)) {
//            System.out.println("predecessor already again");
            return;
        }
        predecessors.add(pred);
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (IRStmt stmt: body){
            String escape = stmt.toString().replaceAll("\n","");
            builder.append(escape).append('\n');
        }
        return builder.toString();
    }
}
