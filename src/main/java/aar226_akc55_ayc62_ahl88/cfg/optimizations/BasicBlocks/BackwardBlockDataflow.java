package aar226_akc55_ayc62_ahl88.cfg.optimizations.BasicBlocks;

import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

public class BackwardBlockDataflow<T> {

    protected BiFunction<BasicBlockCFG,T,T> transferFunction;

    protected BinaryOperator<T> meetOperator;

    protected CFGGraphBasicBlock graph;
    protected Supplier<T> accum;


    private HashMap<BasicBlockCFG,T> inMapping;

    private HashMap<BasicBlockCFG,T> outMapping;

    public BackwardBlockDataflow(CFGGraphBasicBlock g,
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
        for (BasicBlockCFG node : g.getNodes()){
            inMapping.put(node,topElement);
            outMapping.put(node,topElement);
        }
    }

    public BiFunction<BasicBlockCFG, T, T> getTransferFunction() {
        return transferFunction;
    }

    public BinaryOperator<T> getMeetOperator() {
        return meetOperator;
    }

    public CFGGraphBasicBlock getGraph() {
        return graph;
    }

    public Supplier<T> getAccum() {
        return accum;
    }

    public HashMap<BasicBlockCFG, T> getInMapping() {
        return inMapping;
    }

    public HashMap<BasicBlockCFG, T> getOutMapping() {
        return outMapping;
    }

    private T meetBeforeTransfer(BasicBlockCFG node){
        ArrayList<BasicBlockCFG> succs = node.getChildren();
        T outN = accum.get();
        for (BasicBlockCFG succ : succs){
            if (succ != null) {
                outN = meetOperator.apply(outN, inMapping.get(succ));
            }
        }
        outMapping.put(node, outN);
        return outN;
    }

    private T applyTransfer(BasicBlockCFG node) {
        T applied = transferFunction.apply(node, meetBeforeTransfer(node));
        inMapping.put(node, applied);
        return applied;
    }

    public void workList() {
        HashSet<BasicBlockCFG> set = new HashSet<>(graph.getNodes());
        ArrayDeque<BasicBlockCFG> queue = new ArrayDeque<>(graph.getNodes());
        while (!queue.isEmpty()) {
            BasicBlockCFG node = queue.pollLast();
            set.remove(node);

            T oldIn = inMapping.get(node);
            T newIn = applyTransfer(node);

            if (!oldIn.equals(newIn)) {
                for (BasicBlockCFG pred : node.getPredecessors()) {
                    if (!set.contains(pred)) {
                        set.add(pred);
                        queue.addFirst(pred);
                    }
                }
            }
        }
    }
}