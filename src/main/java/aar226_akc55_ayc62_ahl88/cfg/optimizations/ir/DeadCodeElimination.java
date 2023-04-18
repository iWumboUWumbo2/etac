package aar226_akc55_ayc62_ahl88.cfg.optimizations.ir;

import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;

import java.util.LinkedList;
import java.util.Queue;

public class DeadCodeElimination<T> {

    private Queue<CFGNode<T>> queue;

    public DeadCodeElimination(CFGGraph<T> graph) {
        queue = new LinkedList<>();
        queue.addAll(graph.getNodes());

        while (!queue.isEmpty()) {
            CFGNode<T> node = queue.poll();
            eliminate(node);
        }
    }

    private void eliminate(CFGNode<T> node) {
        for (T temp : node.getDef()) {
            boolean a = node.getFallThroughChild() != null && node.getFallThroughChild().getIn().contains(temp);
            boolean b = node.getJumpChild() != null && node.getJumpChild().getIn().contains(temp);

            if (!(a || b)) {
                if (node.getJumpChild() == null) {
                    for (CFGNode<T> pred : node.getPredecessors()) {
                        if (pred.getFallThroughChild() == node) {
                            pred.setFallThroughChild(node.getFallThroughChild());
                            node.getFallThroughChild().addPredecessor(pred);
                            node.getFallThroughChild().removePredecessor(pred);
                        } else if (pred.getJumpChild() == node) {
                            pred.setJumpChild(node.getJumpChild());
                            node.getJumpChild().addPredecessor(pred);
                            node.getJumpChild().removePredecessor(pred);
                        }
                    }
                }
            }
        }
    }
}
