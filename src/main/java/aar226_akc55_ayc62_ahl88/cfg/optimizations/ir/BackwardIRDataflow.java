package aar226_akc55_ayc62_ahl88.cfg.optimizations.ir;

import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRTemp;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

public class BackwardIRDataflow<T,U> {

    private BiFunction<CFGNode<U>,T,T> transferFunction;

    private BinaryOperator<T> meetOperator;

    private CFGGraph<U> graph;
    private Supplier<T> accum;


    private HashMap<CFGNode<U>,T> inMapping;

    private HashMap<CFGNode<U>,T> outMapping;

    public BackwardIRDataflow(CFGGraph<U> g,
                              BiFunction<CFGNode<U>,T, T> transfer,
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

    public BiFunction<CFGNode<U>, T, T> getTransferFunction() {
        return transferFunction;
    }

    public BinaryOperator<T> getMeetOperator() {
        return meetOperator;
    }

    public CFGGraph<U> getGraph() {
        return graph;
    }

    public Supplier<T> getAccum() {
        return accum;
    }

    public HashMap<CFGNode<U>, T> getInMapping() {
        return inMapping;
    }

    public HashMap<CFGNode<U>, T> getOutMapping() {
        return outMapping;
    }

    private T meetBeforeTransfer(CFGNode<U> node){
        ArrayList<CFGNode<U>> succs = node.getChildren();
        T outN = accum.get();
        for (CFGNode<U> succ : succs){
            if (succ != null) {
                outN = meetOperator.apply(outN, inMapping.get(succ));
            }
        }
        outMapping.put(node, outN);
        return outN;
    }

    private T applyTransfer(CFGNode<U> node) {
        T applied = transferFunction.apply(node, meetBeforeTransfer(node));
        inMapping.put(node, applied);
        return applied;
    }

    public void workList() {
        HashSet<CFGNode<U>> set = new HashSet<>(graph.getNodes());
        ArrayDeque<CFGNode<U>> queue = new ArrayDeque<>(graph.getNodes());
        while (!queue.isEmpty()) {
            CFGNode<U> node = queue.pollLast();
            set.remove(node);

            T oldIn = inMapping.get(node);
            T newIn = applyTransfer(node);

            if (!oldIn.equals(newIn)) {
                for (CFGNode<U> pred : node.getPredecessors()) {
                    if (!set.contains(pred)) {
                        set.add(pred);
                        queue.addFirst(pred);
                    }
                }
            }
        }
    }
}