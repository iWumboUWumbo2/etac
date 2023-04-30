package aar226_akc55_ayc62_ahl88.cfg.optimizations.BasicBlocks;

import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

public class ForwardBlockDataflow<T> {

    protected BiFunction<BasicBlockCFG,T,T> transferFunction;

    protected BinaryOperator<T> meetOperator;

    protected CFGGraphBasicBlock graph;
    protected Supplier<T> accum;


    protected HashMap<BasicBlockCFG,T> inMapping;

    protected HashMap<BasicBlockCFG,T> outMapping;

    public ForwardBlockDataflow(CFGGraphBasicBlock g,
                             BiFunction<BasicBlockCFG,T, T> transfer,
                             BinaryOperator<T> meet,
                             Supplier<T> acc,
                             T topElement){
        graph = g;
        transferFunction = transfer;
        meetOperator = meet;
        accum = acc;
        inMapping = new HashMap<>();
        outMapping = new HashMap<>();
        for (var node : g.getNodes()){
            inMapping.put(node,topElement);
            outMapping.put(node,topElement);
        }
    }

    public HashMap<BasicBlockCFG, T> getInMapping() {
        return inMapping;
    }

    public HashMap<BasicBlockCFG, T> getOutMapping() {
        return outMapping;
    }

    public CFGGraphBasicBlock getGraph() {
        return graph;
    }

    public void setGraph(CFGGraphBasicBlock graph) {
        this.graph = graph;
    }

    protected T meetBeforeTransfer(BasicBlockCFG node){
        ArrayList<BasicBlockCFG> preds = node.getPredecessors();
        T inN = accum.get();
        for (var pred : preds){
            if (pred != null) {
                inN = meetOperator.apply(inN, outMapping.get(pred));
            }
        }
        inMapping.put(node, inN);
        return inN;
    }

    protected T applyTransfer(BasicBlockCFG node) {
        T applied = transferFunction.apply(node, meetBeforeTransfer(node));
        outMapping.put(node, applied);
        return applied;
    }

    public void worklist() {
        HashSet<BasicBlockCFG> set = new HashSet<>(graph.getNodes());
        Queue<BasicBlockCFG> queue = new ArrayDeque<>(graph.reversePostorder());

        while (!queue.isEmpty()) {
            BasicBlockCFG node = queue.poll();
            set.remove(node);

            T oldOut = outMapping.get(node);
            T newOut = applyTransfer(node);

            if (!oldOut.equals(newOut)) {
                for (BasicBlockCFG succ : node.getChildren()) {
                    if (!set.contains(succ)) {
                        set.add(succ);
                        queue.add(succ);
                    }
                }
            }
        }
    }
}
