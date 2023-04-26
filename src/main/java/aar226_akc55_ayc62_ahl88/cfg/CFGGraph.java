package aar226_akc55_ayc62_ahl88.cfg;

import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMNameExpr;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMLabel;
import aar226_akc55_ayc62_ahl88.asm.Instructions.jumps.ASMAbstractJump;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import org.apache.commons.text.StringEscapeUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

public class CFGGraph<T, U> {
    private ArrayList<CFGNode<T, U>> nodes;
    private HashMap<String, Integer> labelMap;

    public CFGGraph(ArrayList<T> stmts) {
        nodes = new ArrayList<>();
        labelMap = new HashMap<>();

        // first pass: add nodes to graph
        for (int i = 0; i < stmts.size(); i++) {
            T stmt = stmts.get(i);

            CFGNode<T, U> node = new CFGNode<T, U>(stmt);

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

            CFGNode<T, U> cfgnode = nodes.get(i);
            T stmt = cfgnode.getStmt();

            if (i != 0) {
                cfgnode.addPredecessor(nodes.get(i - 1));
            }

            if (i != stmts.size() - 1) {
                cfgnode.setFallThroughChild(nodes.get(i + 1));
            }

            String irname;
            if (stmt instanceof IRJump irjump) {
                irname = ((IRName) irjump.target()).name();
                cfgnode.setJumpChild( nodes.get(labelMap.get(irname)) );
            }

            if (stmt instanceof IRCJump ircjump) {
                irname = ircjump.trueLabel();
                cfgnode.setJumpChild( nodes.get(labelMap.get(irname)));
            }

            if (stmt instanceof ASMAbstractJump asmjump) {
                String asmname = asmjump.getLeft().toString();
                cfgnode.setJumpChild( nodes.get(labelMap.get(asmname)));
            }
        }
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


        HashMap<CFGNode<T, U>, Integer> visitedIDs = new HashMap<>();
        HashSet<CFGNode<T, U>> visited = new HashSet<>();

        Stack<CFGNode<T, U>> stack = new Stack<>();
        stack.push(nodes.get(0));

        while (!stack.isEmpty()) {
            CFGNode<T, U> popped = stack.pop();

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

            for (CFGNode<T, U> child : popped.getChildren()) {
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

    public ArrayList<CFGNode<T, U>> getNodes() {
        return nodes;
    }
}
