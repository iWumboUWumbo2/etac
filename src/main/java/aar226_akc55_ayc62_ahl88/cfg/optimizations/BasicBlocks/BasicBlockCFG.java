package aar226_akc55_ayc62_ahl88.cfg.optimizations.BasicBlocks;

import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.FlattenIrVisitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
        builder.append(" Block : ");
        for (IRStmt stmt: body){
            String escape = stmt.toString().replaceAll("\n","");
            builder.append(escape).append('\n');
        }
        return builder.toString();
    }

    public Set<IRTemp> dataflowDef(){
        HashSet<IRTemp> defSet = new HashSet<>();
        for (IRStmt stmt: body){
            if (stmt instanceof IRMove move && move.target() instanceof IRTemp temp){
                defSet.add(temp);
            }else if (stmt instanceof IRCallStmt call){
                for (int i = 1; i<= call.n_returns();i++){
                    defSet.add(new IRTemp("_RV" + i));
                }
            }else if (stmt instanceof IRPhi phi){
                defSet.add((IRTemp) phi.getTarget());
            }
        }
        return defSet;
    }

    public Set<IRTemp> dataflowUse(){
        HashSet<IRTemp> res = new HashSet<>();
        for (IRStmt stmt : body){
            res.addAll(singleStmtUse(stmt));
        }
        return res;
    }

    private Set<IRTemp> singleStmtUse(IRStmt stmt) {

        ArrayList<IRNode> flattened;
        // if [mov temp, expr] then don't add temp
        if (stmt instanceof IRMove irmove && irmove.target() instanceof IRTemp) {
            flattened = new FlattenIrVisitor().visit(irmove.source());
        }

        // MEM
        else if (stmt instanceof IRMove irmove && irmove.target() instanceof IRMem) {
            flattened = new FlattenIrVisitor().visit(stmt);
        }
        // JUMP
        else if (stmt instanceof IRCJump cjmp) {
            flattened = new FlattenIrVisitor().visit(cjmp.cond());
        }
        // Return
        else if (stmt instanceof IRReturn ret){
            flattened = new ArrayList<>();
            for (IRExpr e: ret.rets()){
                flattened.addAll(new FlattenIrVisitor().visit(e));
            }
        }else if (stmt instanceof IRCallStmt call){
            flattened = new ArrayList<>();
            for (IRExpr e : call.args()){
                flattened.addAll(new FlattenIrVisitor().visit(e));
            }
        }else if (stmt instanceof IRPhi phi){
            flattened = new ArrayList<>();
            for (IRExpr e : phi.getArgs()){
                flattened.addAll(new FlattenIrVisitor().visit(e));
            }
        }
        else{
            flattened = new ArrayList<>();
        }
        return usedTempsDataFlow(flattened);
    }

    public static Set<IRTemp> usedTempsDataFlow(ArrayList<IRNode> nodes){
        HashSet<IRTemp> res = new HashSet<>();
        for (IRNode node : nodes){
            if (node instanceof IRTemp temp){
                res.add(temp);
            }
        }
        return res;
    }

}
