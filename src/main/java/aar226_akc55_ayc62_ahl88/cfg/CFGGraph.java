package aar226_akc55_ayc62_ahl88.cfg;

import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMNameExpr;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMLabel;
import aar226_akc55_ayc62_ahl88.asm.Instructions.jumps.ASMAbstractJump;
import aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine.ASMCall;
import aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine.ASMRet;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.InternalCompilerError;
import org.apache.commons.text.StringEscapeUtils;

import java.lang.reflect.Array;
import java.util.*;

import static aar226_akc55_ayc62_ahl88.visitors.IRVisitor.OUT_OF_BOUNDS;

public class CFGGraph<T> {
    public static IRCallStmt outOfBounds = new IRCallStmt(new IRName(OUT_OF_BOUNDS), 0L,new ArrayList<>());

    private ArrayList<CFGNode<T>> nodes;
    private HashMap<String, Integer> labelMap;

    public CFGGraph(ArrayList<T> stmts) {
        nodes = new ArrayList<>();
        labelMap = new HashMap<>();

        // first pass: add nodes to graph
        for (int i = 0; i < stmts.size(); i++) {
            T stmt = stmts.get(i);

            CFGNode<T> node = new CFGNode<T>(stmt);

            if (node.getStmt() instanceof IRLabel irLabel) {
                labelMap.put(irLabel.name(), i);
            }

            if (node.getStmt() instanceof ASMLabel asmLabel) {
                labelMap.put(asmLabel.getLabel(), i);
            }

            nodes.add(node);
        }

        // second pass: add pred and succ
        for (int i = 0; i < stmts.size(); i++) {

            CFGNode<T> cfgnode = nodes.get(i);
            T stmt = cfgnode.getStmt();

            if (i != 0 && !(nodes.get(i-1).stmt instanceof IRReturn) &&
                    !(nodes.get(i-1).stmt instanceof IRJump) && !(nodes.get(i-1).stmt instanceof ASMRet)
                    && !(nodes.get(i-1).stmt.equals(outOfBounds))&& !(nodes.get(i-1).stmt instanceof ASMAbstractJump)) {
                cfgnode.addPredecessor(nodes.get(i - 1));
            }

            if (i != stmts.size() - 1 && !(nodes.get(i).stmt instanceof IRReturn) && !(nodes.get(i).stmt instanceof ASMRet)
            && !(nodes.get(i).stmt instanceof IRJump) &&!(nodes.get(i).stmt.equals(outOfBounds)) && !(nodes.get(i).stmt instanceof ASMAbstractJump)) {
                cfgnode.setFallThroughChild(nodes.get(i + 1));
            }

            String irname;
            if (stmt instanceof IRJump irjump) {
                irname = ((IRName) irjump.target()).name();
                cfgnode.setJumpChild( nodes.get(labelMap.get(irname)) );
                CFGNode<T> label = nodes.get(labelMap.get(irname));
                label.addPredecessor(cfgnode);
            }

            if (stmt instanceof IRCJump ircjump) {
                irname = ircjump.trueLabel();
                cfgnode.setJumpChild( nodes.get(labelMap.get(irname)));
                CFGNode<T> label = nodes.get(labelMap.get(irname));
                label.addPredecessor(cfgnode);

            }

            if (stmt instanceof ASMAbstractJump asmjump) {
                String asmname = asmjump.getLeft().toString();
                cfgnode.setJumpChild( nodes.get(labelMap.get(asmname)));
            }
        }

//        // set index
//        for (int i = 0; i < nodes.size(); i++) {
//            nodes.get(i).setIndex(i);
//        }
    }

    // https://eli.thegreenplace.net/2015/directed-graph-traversal-orderings-and-applications-to-data-flow-analysis/
    private void dfsWalk(CFGNode<T> node, ArrayList<CFGNode<T>> order, HashSet<CFGNode<T>> visited) {
        visited.add(node);

        for (CFGNode<T> succ : node.getChildren()) {
            if (succ != null && !visited.contains(succ)) {
                dfsWalk(succ, order, visited);
            }
        }

        order.add(node);
    }

    public ArrayList<CFGNode<T>> reversePostorder() {
        HashSet<CFGNode<T>> visited = new HashSet<>();
        ArrayList<CFGNode<T>> order = new ArrayList<>();

        dfsWalk(nodes.get(0), order, visited);
        Collections.reverse(order);
        return order;
    }

    public void removeUnreachable(){
        HashMap<CFGNode<T>, Integer> visitedIDs = new HashMap<>();
        HashSet<CFGNode<T>> visited = new HashSet<>();

        Stack<CFGNode<T>> stack = new Stack<>();
        stack.push(nodes.get(0));
        visited.add(nodes.get(0));
        while (!stack.isEmpty()) {
            CFGNode<T> popped = stack.pop();

            for (CFGNode<T> child : popped.getChildren()){
                if (child != null && !visited.contains(child)){
                    visited.add(child);
                    stack.push(child);
                }
            }
        }
        for (CFGNode<T> node : nodes) {
            if (!visited.contains(node)) { // remove this node
                for (CFGNode<T> child : node.getChildren()) {
                    if (child != null) {
                        child.removePredecessor(node);
                    }
                }
            }
        }
        ArrayList<CFGNode<T>> newGraph = new ArrayList<>();
        for (CFGNode<T> node : nodes){
            if (visited.contains(node)){
                newGraph.add(node);
            }
        }
        nodes = newGraph;
    }

    public ArrayList<T> getBackIR(){
        ArrayList<T> res = new ArrayList<>();
        for (CFGNode<T> node : nodes){
            res.add(node.stmt);
        }
        return res;
    }
    @Override
    public String toString() {
        return nodes.toString();
    }

    public String CFGtoDOT() {
        StringBuilder result = new StringBuilder();

        // Assume first node is start node
        if (nodes.size() == 0) {
            return "";
        }

        result.append("digraph g {").append("\n\t")
            .append("node [shape=record];").append("\n")
            .append("forcelabels=true;").append("\n");


        HashMap<CFGNode<T>, Integer> visitedIDs = new HashMap<>();
        HashSet<CFGNode<T>> visited = new HashSet<>();

        Stack<CFGNode<T>> stack = new Stack<>();
        stack.push(nodes.get(0));

        while (!stack.isEmpty()) {
            CFGNode<T> popped = stack.pop();

            if (visited.contains(popped)) {
                continue;
            }

            // Add node
            if (!visitedIDs.containsKey(popped)) {
                visitedIDs.put(popped, visitedIDs.size());
            }

            result.append("\t").append(visitedIDs.get(popped))
                .append("\t [ label=\"").append(StringEscapeUtils.escapeJava(popped.toString()))
                .append("\"]\n");

            for (CFGNode<T> child : popped.getChildren()) {
                if (child != null) {
                    if (!visitedIDs.containsKey(child)) {
                        visitedIDs.put(child, visitedIDs.size());
                    }

                    result.append("\t").append(visitedIDs.get(popped))
                            .append(" -> ").append(visitedIDs.get(child)).append("\n");
                    stack.add(child);
                }
            }

            visited.add(popped);
        }

        result.append("}");
        return result.toString();
    }

    public ArrayList<CFGNode<T>> getNodes() {
        return nodes;
    }
}
