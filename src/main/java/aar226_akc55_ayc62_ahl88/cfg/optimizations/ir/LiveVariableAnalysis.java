package aar226_akc55_ayc62_ahl88.cfg.optimizations.ir;

import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.FlattenIrVisitor;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;

public class LiveVariableAnalysis {
    public void setDefLVA(CFGNode<IRStmt, IRTemp> node) {
        HashSet<IRTemp> def = new HashSet<>();
        if (node.getStmt() instanceof IRMove irmove && irmove.target() instanceof IRTemp irtemp) {
            def.add(irtemp);
            node.setDef(def);
        }
    }

    private HashSet<IRTemp> usedTempsLVA(ArrayList<IRNode> nodes){
        HashSet<IRTemp> res = new HashSet<>();
        for (IRNode node : nodes){
            if (node instanceof IRTemp temp){
                res.add(temp);
            }
        }
        return res;
    }

    public void setUseLVA(CFGNode<IRStmt, IRTemp> node) {
        IRStmt stmt = node.getStmt();

        ArrayList<IRNode> flattened;
        // if [mov temp, expr] then don't add temp
        if (stmt instanceof IRMove irmove && irmove.target() instanceof IRTemp) {
            flattened = irmove.source().aggregateChildren(new FlattenIrVisitor());
        }

        // MEM
        else if (stmt instanceof IRMove irmove && irmove.target() instanceof IRMem) {
            flattened = stmt.aggregateChildren(new FlattenIrVisitor());
        }

        // JUMP
        else if (stmt instanceof IRCJump cjmp) {
            flattened = cjmp.cond().aggregateChildren(new FlattenIrVisitor());
        }
        // Return
        else if (stmt instanceof IRReturn ret){
            flattened = new ArrayList<>();
            for (IRExpr e: ret.rets()){
                flattened.addAll(e.aggregateChildren(new FlattenIrVisitor()));
            }
        }else if (stmt instanceof IRCallStmt call){
            flattened = new ArrayList<>();
            for (IRExpr e : call.args()){
                flattened.addAll(e.aggregateChildren(new FlattenIrVisitor()));
            }
        }

        else{
            flattened = new ArrayList<>();
        }
        HashSet<IRTemp> tempsInNode = usedTempsLVA(flattened);
        HashSet<IRTemp> use = new HashSet<>(tempsInNode);
        node.setUse(use);
        node.setUse(use);
    }

    public void setInLVA(CFGNode<IRStmt, IRTemp> node) {
        HashSet<IRTemp> in = new HashSet<>();
        HashSet<IRTemp> out = node.getOut();
        HashSet<IRTemp> def = node.getDef();

        HashSet<IRTemp> temp = new HashSet<>(out);
        temp.removeAll(def);

        temp.addAll(node.getUse());
        in = temp;

        node.setIn(in);
    }

    public void setOutLVA(CFGNode<IRStmt, IRTemp> node) {
        HashSet<IRTemp> out = new HashSet<>();

        CFGNode<IRStmt, IRTemp> fallThroughChild = node.getFallThroughChild();
        CFGNode<IRStmt, IRTemp> jumpChild = node.getJumpChild();

        if (fallThroughChild != null) {
            out.addAll(fallThroughChild.getIn());
        }

        if (jumpChild != null) {
            out.addAll(jumpChild.getIn());
        }

        node.setOut(out);
    }

    public void runLVA(CFGGraph<IRStmt, IRTemp> graph) {
        initLVA(graph);
        ArrayDeque<CFGNode<IRStmt,IRTemp>> queue = new ArrayDeque<>(graph.getNodes());
        while (!queue.isEmpty()){
            CFGNode<IRStmt,IRTemp> front = queue.poll();
            int oldInSize = front.getIn().size();
            setOutLVA(front);
            setInLVA(front);
            if (oldInSize != front.getIn().size()){
                queue.addAll(front.getPredecessors());
            }
        }
    }

    public void initLVA(CFGGraph<IRStmt, IRTemp> graph){
        for (CFGNode<IRStmt,IRTemp> node : graph.getNodes()){
            setDefLVA(node);
            setUseLVA(node);
            node.setIn(new HashSet<>());
        }
    }
}
