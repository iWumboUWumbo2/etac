package aar226_akc55_ayc62_ahl88.cfg.optimizations.ir;

import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRTemp;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

public class BackwardIRDataflow<T> {

    private BiFunction<CFGNode<IRStmt>,T,T> transferFunction;

    private BinaryOperator<T> meetOperator;

    private CFGGraph<IRStmt> graph;
    private Supplier<T> accum;


    private HashMap<CFGNode<IRStmt>,T> inMapping;

    private HashMap<CFGNode<IRStmt>,T> outMapping;

    public BackwardIRDataflow(CFGGraph<IRStmt> g,
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
    private T meetBeforeTransfer(CFGNode<IRStmt> node){
        ArrayList<CFGNode<IRStmt>> succs = node.getChildren();
        T outN = accum.get();
        for (var succ : succs){
            outN = meetOperator.apply(outN,inMapping.get(succ));
        }
        return outN;
    }

    private T applyTransfer(CFGNode<IRStmt> node) {
        T applied = transferFunction.apply(node, meetBeforeTransfer(node));
        inMapping.put(node, applied);
        return applied;
    }

    private void worklist() {
        HashSet<CFGNode<IRStmt>> set = new HashSet<>(graph.getNodes());
        Queue<CFGNode<IRStmt>> queue = new ArrayDeque<>(graph.getNodes());

        while (!queue.isEmpty()) {
            var node = queue.poll();
            set.remove(node);

            T oldIn = inMapping.get(node);
            T newIn = applyTransfer(node);

            if (!oldIn.equals(newIn)) {
                for (var pred : node.getPredecessors()) {
                    if (!set.contains(pred)) {
                        set.add(pred);
                        queue.add(pred);
                    }
                }
            }
        }
    }
}
