package aar226_akc55_ayc62_ahl88.asm.Opts;

import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMTempExpr;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BasicBlockASMCFG {
    final private int FALLTHROUGH = 0;
    final private int JUMP = 1;

    public boolean start;
    HashSet<String> originLabels;

    HashSet<String> destLabels;
    ArrayList<CFGNode<ASMInstruction>> body;

    private ArrayList<BasicBlockASMCFG> children;

    private ArrayList<BasicBlockASMCFG> predecessors;

    public BasicBlockASMCFG(){
        start = false;
        this.originLabels = new HashSet<>();
        this.destLabels = new HashSet<>();
        this.predecessors = new ArrayList<>();
        this.children = new ArrayList<>(2);
        this.children.add(null);
        this.children.add(null);
        this.body = new ArrayList<>();
    }
    public ArrayList<CFGNode<ASMInstruction>> getBody() { return body; }

    public BasicBlockASMCFG getFallThroughChild() {
        return children.get(FALLTHROUGH);
    }

    public void setFallThroughChild(BasicBlockASMCFG fallThroughChild) {
        this.children.set(FALLTHROUGH, fallThroughChild) ;
    }
    public BasicBlockASMCFG getJumpChild() {
        return children.get(JUMP);
    }

    public void setJumpChild(BasicBlockASMCFG jumpChild) {
        children.set(JUMP, jumpChild);
    }
    public ArrayList<BasicBlockASMCFG> getChildren() {
        return children;
    }

    public ArrayList<BasicBlockASMCFG> getPredecessors() {
        return predecessors;
    }
    public void removePredecessor(BasicBlockASMCFG pred) {
        predecessors.remove(pred);
    }
    public void addPredecessor(BasicBlockASMCFG pred) {
        if (predecessors.contains(pred)) {
//            System.out.println("predecessor already again");
            return;
        }
        predecessors.add(pred);
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(" Block : ");
        for (CFGNode<ASMInstruction> node: body){
            ASMInstruction stmt = node.getStmt();
            String escape = stmt.toString();
            builder.append(escape).append('\n');
        }
        return builder.toString();
    }

    private Set<ASMTempExpr> singleStmtUse(ASMInstruction stmt) {
        return new HashSet<>();
    }
}
