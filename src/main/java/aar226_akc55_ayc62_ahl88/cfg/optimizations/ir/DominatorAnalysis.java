package aar226_akc55_ayc62_ahl88.cfg.optimizations.ir;

import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.cfg.HashSetInf;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRTemp;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

public class DominatorAnalysis extends ForwardIRDataflow<HashSetInf<CFGNode<IRStmt>>>{
    public DominatorAnalysis(CFGGraph<IRStmt> graph) {
        super(graph,
                (n,inN)->{
                    if (inN.isInfSize()){
                        return inN;
                    }
                    HashSetInf<CFGNode<IRStmt>> outN = new HashSetInf<>(false);
                    outN.add(n);
                    outN.addAll(inN);
                    return outN;
                },
                (l1,l2) -> {
                    if (l1.isInfSize()){
                        return l2;
                    }
                    if (l2.isInfSize()){
                        return l1;
                    }
                    HashSetInf<CFGNode<IRStmt>> res = new HashSetInf<>(l1);
                    res.retainAll(l1);
                    return res;
                },
                () -> new HashSetInf<>(true),
                new HashSetInf<>(true));
        HashSetInf<CFGNode<IRStmt>> startOutN = new HashSetInf<>(false);
        CFGNode<IRStmt> startNode = graph.getNodes().get(0);
        startOutN.add(startNode);
        getOutMapping().put(startNode,startOutN);
    }
    @Override
    protected void worklist() {
        HashSet<CFGNode<IRStmt>> set = new HashSet<>(graph.getNodes());
        Queue<CFGNode<IRStmt>> queue = new ArrayDeque<>(graph.reversePostorder());
        // Don't execute first start element Brittle
        queue.poll();
        while (!queue.isEmpty()) {
            CFGNode<IRStmt> node = queue.poll();
            set.remove(node);

            HashSetInf<CFGNode<IRStmt>> oldOut = outMapping.get(node);
            HashSetInf<CFGNode<IRStmt>> newOut = applyTransfer(node);

            if (!oldOut.equals(newOut)) {
                for (var succ : node.getChildren()) {
                    if (!set.contains(succ)) {
                        set.add(succ);
                        queue.add(succ);
                    }
                }
            }
        }
    }
}
