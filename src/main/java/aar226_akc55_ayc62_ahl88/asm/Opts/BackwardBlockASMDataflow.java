package aar226_akc55_ayc62_ahl88.asm.Opts;


import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.BasicBlocks.BasicBlockCFG;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.BasicBlocks.CFGGraphBasicBlock;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

public class BackwardBlockASMDataflow<T> {

    protected BiFunction<BasicBlockASMCFG,T,T> transferFunction;

    protected BinaryOperator<T> meetOperator;

    protected CFGGraphBasicBlockASM graph;
    protected Supplier<T> accum;


    private HashMap<BasicBlockASMCFG,T> inMapping;

    private HashMap<BasicBlockASMCFG,T> outMapping;

    public BackwardBlockASMDataflow(CFGGraphBasicBlockASM g,
                                 BiFunction<BasicBlockASMCFG,T, T> transfer,
                                 BinaryOperator<T> meet,
                                 Supplier<T> acc,
                                 T topElement){
        graph = g;
        transferFunction = transfer;
        meetOperator = meet;
        accum = acc;
        inMapping = new HashMap<>();
        outMapping = new HashMap<>();
        for (BasicBlockASMCFG node : g.getNodes()){
            inMapping.put(node,topElement);
            outMapping.put(node,topElement);
        }
    }

    public BiFunction<BasicBlockASMCFG, T, T> getTransferFunction() {
        return transferFunction;
    }

    public BinaryOperator<T> getMeetOperator() {
        return meetOperator;
    }

    public CFGGraphBasicBlockASM getGraph() {
        return graph;
    }

    public Supplier<T> getAccum() {
        return accum;
    }

    public HashMap<BasicBlockASMCFG, T> getInMapping() {
        return inMapping;
    }

    public HashMap<BasicBlockASMCFG, T> getOutMapping() {
        return outMapping;
    }

    private T meetBeforeTransfer(BasicBlockASMCFG node){
        ArrayList<BasicBlockASMCFG> succs = node.getChildren();
        T outN = accum.get();
        for (BasicBlockASMCFG succ : succs){
            if (succ != null) {
                outN = meetOperator.apply(outN, inMapping.get(succ));
            }
        }
        outMapping.put(node, outN);
        return outN;
    }

    private T applyTransfer(BasicBlockASMCFG node) {
        T applied = transferFunction.apply(node, meetBeforeTransfer(node));
        inMapping.put(node, applied);
        return applied;
    }

    public void workList() {
        HashSet<BasicBlockASMCFG> set = new HashSet<>(graph.getNodes());
        ArrayDeque<BasicBlockASMCFG> queue = new ArrayDeque<>(graph.getNodes());
        while (!queue.isEmpty()) {
            BasicBlockASMCFG node = queue.pollLast();
            set.remove(node);

            T oldIn = inMapping.get(node);
            T newIn = applyTransfer(node);

            if (!oldIn.equals(newIn)) {
                for (BasicBlockASMCFG pred : node.getPredecessors()) {
                    if (!set.contains(pred)) {
                        set.add(pred);
                        queue.addFirst(pred);
                    }
                }
            }
        }
    }
}