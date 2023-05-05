package aar226_akc55_ayc62_ahl88.cfg.optimizations.ir;

import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.InternalCompilerError;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

public class ForwardIRDataflow<T> {

    protected BiFunction<CFGNode<IRStmt>,T,T> transferFunction;

    protected BinaryOperator<T> meetOperator;

    protected CFGGraph<IRStmt> graph;
    protected Supplier<T> accum;


    protected HashMap<CFGNode<IRStmt>,T> inMapping;

    protected HashMap<CFGNode<IRStmt>,T> outMapping;

    public ForwardIRDataflow(CFGGraph<IRStmt> g,
                             BiFunction<CFGNode<IRStmt>,T, T> transfer,
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

    public HashMap<CFGNode<IRStmt>, T> getInMapping() {
        return inMapping;
    }

    public HashMap<CFGNode<IRStmt>, T> getOutMapping() {
        return outMapping;
    }

    public CFGGraph<IRStmt> getGraph() {
        return graph;
    }

    public void setGraph(CFGGraph<IRStmt> graph) {
        this.graph = graph;
    }

    protected T meetBeforeTransfer(CFGNode<IRStmt> node){
        ArrayList<CFGNode<IRStmt>> preds = node.getPredecessors();
        T inN = accum.get();
        for (var pred : preds){
            if (pred != null) {
                inN = meetOperator.apply(inN, outMapping.get(pred));
            }
        }
        inMapping.put(node, inN);
        return inN;
    }

    protected T applyTransfer(CFGNode<IRStmt> node) {
        T applied = transferFunction.apply(node, meetBeforeTransfer(node));
        outMapping.put(node, applied);
        return applied;
    }

    public void worklist() {
        HashSet<CFGNode<IRStmt>> set = new HashSet<>(graph.getNodes());
        Queue<CFGNode<IRStmt>> queue = new ArrayDeque<>(graph.reversePostorder());

        while (!queue.isEmpty()) {
            var node = queue.poll();
            set.remove(node);

            T oldOut = outMapping.get(node);
            T newOut = applyTransfer(node);

            if (!oldOut.equals(newOut)) {
                for (var succ : node.getChildren()) {
                    if (succ != null && !set.contains(succ)) {
                        set.add(succ);
                        queue.add(succ);
                    }
                }
            }
        }
    }
}
