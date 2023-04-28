package aar226_akc55_ayc62_ahl88.cfg.optimizations.ir;

import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.cfg.HashSetInf;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRLabel;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRTemp;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

public class DominatorAnalysis extends ForwardIRDataflow<HashSetInf<CFGNode<IRStmt>>>{

    HashMap<CFGNode<IRStmt>,HashSet<CFGNode<IRStmt>>> dominatorTree;

    HashMap<CFGNode<IRStmt>,CFGNode<IRStmt>> immediateDominator;

    HashMap<CFGNode<IRStmt>,HashSet<CFGNode<IRStmt>>> dominanceFrontier;
    public DominatorAnalysis(CFGGraph<IRStmt> graph) {
        super(graph,
                (n,inN)->{
                    if (inN.isInfSize()){
                        return inN;
                    }
                    HashSetInf<CFGNode<IRStmt>> outN = new HashSetInf<>(false);
                    outN.addAll(inN);
                    outN.add(n);
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
                    res.retainAll(l2);
                    return res;
                },
                () -> new HashSetInf<>(true),
                new HashSetInf<>(true));
        HashSetInf<CFGNode<IRStmt>> startOutN = new HashSetInf<>(false);
        CFGNode<IRStmt> startNode = graph.getNodes().get(0);
        startOutN.add(startNode);
        getOutMapping().put(startNode,startOutN);
        dominatorTree = new HashMap<>();
        immediateDominator = new HashMap<>();
        dominanceFrontier = new HashMap<>();
    }
    @Override
    public void worklist() {
        HashSet<CFGNode<IRStmt>> set = new HashSet<>(graph.getNodes());
        Queue<CFGNode<IRStmt>> queue = new ArrayDeque<>(graph.reversePostorder());
        // Don't execute first start element Brittle
        queue.poll();
        while (!queue.isEmpty()) {
            CFGNode<IRStmt> node = queue.poll();
            set.remove(node);

            HashSetInf<CFGNode<IRStmt>> oldOut = outMapping.get(node);
            HashSetInf<CFGNode<IRStmt>> newOut = applyTransfer(node);

//            System.out.println("working on: " + node.toString().replaceAll("\n",""));
//            System.out.println("oldOut: " + oldOut);
//            System.out.println("newOut: " + newOut);

            if (!oldOut.equals(newOut)) {
                for (CFGNode<IRStmt> succ : node.getChildren()) {
                    if (succ != null && !set.contains(succ)) {
                        set.add(succ);
                        queue.add(succ);
                    }
                }
            }
        }
    }

    public HashMap<CFGNode<IRStmt>, HashSet<CFGNode<IRStmt>>> getDominatorTree() {
        return dominatorTree;
    }

    public HashMap<CFGNode<IRStmt>, CFGNode<IRStmt>> getImmediateDominator() {
        return immediateDominator;
    }

    public void createDominatorTreeAndImmediate(){
        HashMap<CFGNode<IRStmt>,HashSetInf<CFGNode<IRStmt>>> copyOfIndegree = new HashMap<>();
        for (CFGNode<IRStmt> node : outMapping.keySet()){
            copyOfIndegree.put(node,new HashSetInf<>(outMapping.get(node)));
        }
        // Remove yourself
        for (CFGNode<IRStmt> node: copyOfIndegree.keySet()){
            copyOfIndegree.get(node).remove(node);
            dominatorTree.put(node,new HashSet<>());
        }
        HashSet<CFGNode<IRStmt>> visited = new HashSet<>();
        Queue<CFGNode<IRStmt>> queue = new ArrayDeque<>();
        queue.add(graph.getNodes().get(0));
        immediateDominator.put(graph.getNodes().get(0),null);
        visited.add(graph.getNodes().get(0));
        while (!queue.isEmpty()){
            CFGNode<IRStmt> top = queue.remove();
            for (CFGNode<IRStmt> node: copyOfIndegree.keySet()){
                copyOfIndegree.get(node).remove(top);
                if (!visited.contains(node) && copyOfIndegree.get(node).size() == 0){
                    visited.add(node);
                    queue.add(node);
                    immediateDominator.put(node,top);
                    dominatorTree.get(top).add(node);
                }
            }
        }
    }

    public HashMap<CFGNode<IRStmt>, HashSet<CFGNode<IRStmt>>> getDominanceFrontier() {
        return dominanceFrontier;
    }

    public void constructDF(){
        computeDF(graph.getNodes().get(0));
    }
    private void computeDF(CFGNode<IRStmt> node){
        HashSet<CFGNode<IRStmt>> S = new HashSet<>();
        for (CFGNode<IRStmt> succ : node.getChildren()){
            if (succ != null && immediateDominator.get(succ) != node){
                S.add(succ);
            }
        }
        for (CFGNode<IRStmt> domChild: dominatorTree.get(node)){
            computeDF(domChild);
            for (CFGNode<IRStmt> w : dominanceFrontier.get(domChild)){
                if (!(dominatorTree.get(node).contains(w) || node == w)){
                    S.add(w);
                }
            }
        }
        dominanceFrontier.put(node,S);
    }

//    public void placePhiFunctions(){
//        for ()
//    }
}
