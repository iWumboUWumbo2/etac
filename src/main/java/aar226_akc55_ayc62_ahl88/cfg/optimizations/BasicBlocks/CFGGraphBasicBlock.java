package aar226_akc55_ayc62_ahl88.cfg.optimizations.BasicBlocks;

import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.InternalCompilerError;
import org.apache.commons.text.StringEscapeUtils;

import java.util.*;
import java.util.stream.Collectors;

public class CFGGraphBasicBlock {
    private ArrayList<BasicBlockCFG> nodes;
    private HashMap<String, Integer> labelMap;
    private HashMap<Integer, BasicBlockCFG> indToBasicBlock;
    private HashMap<String,Long> labelToNumber;
    private boolean stop(IRStmt stmt){
        return (stmt instanceof IRJump ||
                stmt instanceof IRCJump ||
                stmt instanceof IRReturn ||
                stmt instanceof IRLabel);
    }

    public CFGGraphBasicBlock(ArrayList<IRStmt> stmts){
        nodes = new ArrayList<>();
        labelMap = new HashMap<>();
        indToBasicBlock = new HashMap<>();
        labelToNumber = new HashMap<>();
        int ind = 0;
        BasicBlockCFG curBlock = new BasicBlockCFG();
        for (IRStmt stmt: stmts){
            if (stop(stmt)){
                if (stmt instanceof IRJump jmp){
                    String destName = ((IRName) jmp.target()).name();
                    if (labelToNumber.containsKey(destName)) {
                        labelToNumber.put(destName, labelToNumber.get(destName) + 1);
                    }else{
                        labelToNumber.put(destName,1L);
                    }
                    curBlock.originLabels.add(destName);
                    curBlock.body.add(new CFGNode<>(stmt));
                } else if (stmt instanceof IRCJump cjmp) {
                    curBlock.originLabels.add(cjmp.trueLabel());
                    if (labelToNumber.containsKey(cjmp.trueLabel())) {
                        labelToNumber.put(cjmp.trueLabel(), labelToNumber.get(cjmp.trueLabel()) + 1);
                    } else {
                        labelToNumber.put(cjmp.trueLabel(), 1L);
                    }
//                    curBlock.originLabels.add(cjmp.falseLabel());
//                    if (labelToNumber.containsKey(cjmp.falseLabel())) {
//                        labelToNumber.put(cjmp.falseLabel(), labelToNumber.get(cjmp.falseLabel()) + 1);
//                    } else {
//                        labelToNumber.put(cjmp.falseLabel(), 1L);
//                    }
                    curBlock.body.add(new CFGNode<>(stmt));
                }else if (stmt instanceof IRReturn irr){
                    curBlock.body.add(new CFGNode<>(irr));
                }
                if (curBlock.body.size() != 0){ // stuff here
                    nodes.add(curBlock);
                    indToBasicBlock.put(ind,curBlock);
                    ind++;
                    curBlock = new BasicBlockCFG();
                    if (stmt instanceof IRLabel il){
                        curBlock.destLabels.add(il.name());
                        curBlock.body.add(new CFGNode<>(il));
                        labelMap.put(il.name(),ind);
                        if (!labelToNumber.containsKey(il.name())) {
                            labelToNumber.put(il.name(),0L);
                        }
                    }
                }else if (stmt instanceof IRLabel il){
                    curBlock.body.add(new CFGNode<>(il));
                    curBlock.destLabels.add(il.name());
                    labelMap.put(il.name(),ind);
                    if (!labelToNumber.containsKey(il.name())) {
                        labelToNumber.put(il.name(),0L);
                    }
                }else{
                    throw new InternalCompilerError("BRUH"); // pls try to get
                }
            }else{
                curBlock.body.add(new CFGNode<>(stmt));
            }
        }
        if (curBlock.body.size() != 0){
            nodes.add(curBlock);
            indToBasicBlock.put(ind,curBlock);
            ind++;
        }
        buildDependencies();
    }

    private void buildDependencies(){
        for (int i = 0 ;i< nodes.size();i++){
            BasicBlockCFG cfgnode = nodes.get(i);
            IRStmt lastStmtCurBlock = cfgnode.body.get(cfgnode.getBody().size()-1).getStmt();
            if (i != 0 && !((nodes.get(i-1).body.get(nodes.get(i-1).body.size()-1).getStmt()) instanceof IRReturn)
             && !((nodes.get(i-1).body.get(nodes.get(i-1).body.size()-1).getStmt()) instanceof IRJump)){
                cfgnode.addPredecessor(nodes.get(i-1));
            }
            if (i != nodes.size()-1 && !(cfgnode.body.get(cfgnode.body.size()-1).getStmt() instanceof IRReturn)
                    && !(cfgnode.body.get(cfgnode.body.size()-1).getStmt() instanceof IRJump)){
                cfgnode.setFallThroughChild(nodes.get(i+1));
            }
            String irname;
            if (lastStmtCurBlock instanceof IRJump irjump){
                irname = ((IRName) irjump.target()).name();
                int blockWithLabel = labelMap.get(irname);
                cfgnode.setJumpChild(nodes.get(blockWithLabel));
                indToBasicBlock.get(blockWithLabel).addPredecessor(cfgnode);
            }
            if (lastStmtCurBlock instanceof IRCJump ircJump){
                irname = ircJump.trueLabel();
                int blockWithLabel = labelMap.get(irname);
                cfgnode.setJumpChild(nodes.get(blockWithLabel));
                indToBasicBlock.get(blockWithLabel).addPredecessor(cfgnode);
            }
        }
    }
    private void dfsWalk(BasicBlockCFG node, ArrayList<BasicBlockCFG> order, HashSet<BasicBlockCFG> visited) {
        visited.add(node);

        for (BasicBlockCFG succ : node.getChildren()) {
            if (succ != null && !visited.contains(succ)) {
                dfsWalk(succ, order, visited);
            }
        }

        order.add(node);
    }

    public ArrayList<BasicBlockCFG> reversePostorder() {
        HashSet<BasicBlockCFG> visited = new HashSet<>();
        ArrayList<BasicBlockCFG> order = new ArrayList<>();

        dfsWalk(nodes.get(0), order, visited);
        Collections.reverse(order);
        return order;
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


        HashMap<BasicBlockCFG, Integer> visitedIDs = new HashMap<>();
        HashSet<BasicBlockCFG> visited = new HashSet<>();

        Stack<BasicBlockCFG> stack = new Stack<>();
        stack.push(nodes.get(0));

        while (!stack.isEmpty()) {
            BasicBlockCFG popped = stack.pop();

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

            for (BasicBlockCFG child : popped.getChildren()) {
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

    public ArrayList<CFGNode<IRStmt>> getBackIR(){
        ArrayList<CFGNode<IRStmt>> stmts = new ArrayList<>();
        for (BasicBlockCFG bb : nodes) {
            stmts.addAll(bb.getBody());
        }
        return stmts;
    }

    public ArrayList<BasicBlockCFG> getNodes() {
        return nodes;
    }

    public void edgeSplit(BasicBlockCFG nodeA, BasicBlockCFG nodeB) {
        if (!(nodeA.getChildren().size() > 1 || nodeB.getPredecessors().size() > 1)) {
            return;
        }

        int nodeBIndexInNodeA = nodeA.getChildren().indexOf(nodeB); // A -> B
        int nodeAIndexInNodeB = nodeB.getPredecessors().indexOf(nodeA); // B -> A

        if (nodeBIndexInNodeA == -1 || nodeAIndexInNodeB == -1) {
            throw new InternalCompilerError("There is no edge between nodes A and B");
        }

        BasicBlockCFG splitNode = new BasicBlockCFG();

        nodeA.getChildren().set(nodeBIndexInNodeA, splitNode);
        nodeB.getPredecessors().set(nodeAIndexInNodeB, splitNode);
    }

    public void removeDeletedNodes() {
        for (BasicBlockCFG bb : getNodes()) {
            ArrayList<CFGNode<IRStmt>> newBody = new ArrayList<>();
            for (CFGNode<IRStmt> node : bb.getBody()) {
                if (!node.isDeleted) {
                    newBody.add(node);
                }
            }
            bb.body = newBody;
        }
    }

}
