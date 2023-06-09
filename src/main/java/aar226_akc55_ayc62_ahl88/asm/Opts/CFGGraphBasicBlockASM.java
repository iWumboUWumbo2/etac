package aar226_akc55_ayc62_ahl88.asm.Opts;

import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMAbstractReg;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMNameExpr;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMLabel;
import aar226_akc55_ayc62_ahl88.asm.Instructions.jumps.ASMAbstractJump;
import aar226_akc55_ayc62_ahl88.asm.Instructions.jumps.ASMJumpAlways;
import aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine.ASMCall;
import aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine.ASMRet;
import aar226_akc55_ayc62_ahl88.asm.Opts.BasicBlockASMCFG;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.BasicBlocks.BasicBlockCFG;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRTemp;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.InternalCompilerError;
import org.apache.commons.text.StringEscapeUtils;

import java.util.*;

import static aar226_akc55_ayc62_ahl88.visitors.IRVisitor.OUT_OF_BOUNDS;


/**
 * Class For Basic blocks for ASM instructions
 * Contains the construction , destruction and printing of the CFG
 */
public class CFGGraphBasicBlockASM {
    public static ASMNameExpr outOfBounds = new ASMNameExpr(OUT_OF_BOUNDS);
    private ArrayList<BasicBlockASMCFG> nodes;
    private HashMap<String, Integer> labelMap;
    private HashMap<Integer, BasicBlockASMCFG> indToBasicBlock;
    private HashMap<String,Long> labelToNumber;

    public String funcName;

    private boolean stop(ASMInstruction stmt){
        return (stmt instanceof ASMAbstractJump ||
                stmt instanceof ASMRet ||
                stmt instanceof ASMLabel);
    }

    public CFGGraphBasicBlockASM(ArrayList<ASMInstruction> stmts, String func){
        funcName = func;
        nodes = new ArrayList<>();
        labelMap = new HashMap<>();
        indToBasicBlock = new HashMap<>();
        labelToNumber = new HashMap<>();
        int ind = 0;
        BasicBlockASMCFG curBlock = new BasicBlockASMCFG(funcName);
        curBlock.start = true;
        for (ASMInstruction stmt: stmts){
            if (stop(stmt)){
                if (stmt instanceof ASMJumpAlways jmp){
                    String destName = jmp.getLeft().toString();
                    if (labelToNumber.containsKey(destName)){
                        labelToNumber.put(destName, labelToNumber.get(destName) + 1);
                    }else{
                        labelToNumber.put(destName,1L);
                    }
                    curBlock.originLabels.add(destName);
                    curBlock.body.add(new CFGNode<>(stmt));
                }else if (stmt instanceof ASMAbstractJump condJump){
                    String destName = condJump.getLeft().toString();
                    curBlock.originLabels.add(destName);
                    if (labelToNumber.containsKey(destName)){
                        labelToNumber.put(destName, labelToNumber.get(destName) + 1);
                    }else{
                        labelToNumber.put(destName,1L);
                    }
                    curBlock.body.add((new CFGNode<>(stmt)));
                }else if (stmt instanceof ASMRet ret){
                    curBlock.body.add((new CFGNode<>(ret)));
                }
                if (curBlock.body.size() != 0){
                    nodes.add(curBlock);
                    indToBasicBlock.put(ind,curBlock);
                    ind++;
                    curBlock = new BasicBlockASMCFG(funcName);
                    if (stmt instanceof ASMLabel il){
                        curBlock.destLabels.add(il.getLabel());
                        curBlock.body.add(new CFGNode<>(il));
                        labelMap.put(il.getLabel(),ind);
                        if (!labelToNumber.containsKey(il.getLabel())){
                            labelToNumber.put(il.getLabel(),0L);
                        }
                    }
                }else if (stmt instanceof ASMLabel il){
                    curBlock.body.add(new CFGNode<>(il));
                    curBlock.destLabels.add(il.getLabel());
                    labelMap.put(il.getLabel(),ind);
                    if (!labelToNumber.containsKey(il.getLabel())){
                        labelToNumber.put(il.getLabel(),0L);
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
        for (int i = 0; i< nodes.size();i++){
            BasicBlockASMCFG cfgnode = nodes.get(i);
            ASMInstruction lastStmtCurBlock = cfgnode.body.get(cfgnode.getBody().size()-1).getStmt();
            if (i != 0
                    && !((nodes.get(i-1).body.get(nodes.get(i-1).body.size()-1).getStmt()) instanceof ASMRet)
                    && !((nodes.get(i-1).body.get(nodes.get(i-1).body.size()-1).getStmt()) instanceof ASMJumpAlways)){
                BasicBlockASMCFG prevBlock = nodes.get(i-1);
                boolean hasOutOfBounds = false;
                for (int j = 0; j< prevBlock.body.size();j++){
                    CFGNode<ASMInstruction> instr = prevBlock.body.get(j);
                    if (instr.getStmt() instanceof ASMCall call
                            && call.getLeft() instanceof ASMNameExpr name
                            && name.equals(outOfBounds)){
                        hasOutOfBounds = true;
                        break;
                    }
                }
                if (!hasOutOfBounds) {
                    cfgnode.addPredecessor(nodes.get(i - 1));
                }
            }
            if (i != nodes.size()-1
                    && !(cfgnode.body.get(cfgnode.body.size()-1).getStmt() instanceof ASMRet)
                    && !(cfgnode.body.get(cfgnode.body.size()-1).getStmt() instanceof ASMJumpAlways)){
                boolean hasOutOfBounds = false;
                for (int j = 0; j< cfgnode.body.size();j++){
                    CFGNode<ASMInstruction> instr = cfgnode.body.get(j);
                    if (instr.getStmt() instanceof ASMCall call
                            && call.getLeft() instanceof ASMNameExpr name
                            && name.equals(outOfBounds)){
                        hasOutOfBounds = true;
                        break;
                    }
                }
                if (!hasOutOfBounds) {
                    cfgnode.setFallThroughChild(nodes.get(i + 1));
                }
            }
            if (i != nodes.size()-1 && ((cfgnode.body.get(cfgnode.body.size()-1).getStmt()) instanceof ASMCall call
                    && call.getLeft() instanceof ASMNameExpr name && name.equals(outOfBounds))){
                System.out.println("didn't add child im out of bounds OUTOFBOUNDS");
            }
            String asmName;
            if (lastStmtCurBlock instanceof ASMJumpAlways jmp){
                asmName = jmp.getLeft().toString();
                int blockWithLabel = labelMap.get(asmName);
                cfgnode.setJumpChild(nodes.get(blockWithLabel));
                indToBasicBlock.get(blockWithLabel).addPredecessor(cfgnode);
            }
            else if (lastStmtCurBlock instanceof ASMAbstractJump cjmp){
                asmName = cjmp.getLeft().toString();
                int blockWithLabel = labelMap.get(asmName);
                cfgnode.setJumpChild(nodes.get(blockWithLabel));
                indToBasicBlock.get(blockWithLabel).addPredecessor(cfgnode);
            }
        }
    }
    private void dfsWalk(BasicBlockASMCFG node, ArrayList<BasicBlockASMCFG> order, HashSet<BasicBlockASMCFG> visited) {
        visited.add(node);

        for (BasicBlockASMCFG succ : node.getChildren()) {
            if (succ != null && !visited.contains(succ)) {
                dfsWalk(succ, order, visited);
            }
        }

        order.add(node);
    }
    public ArrayList<BasicBlockASMCFG> reversePostorder() {
        HashSet<BasicBlockASMCFG> visited = new HashSet<>();
        ArrayList<BasicBlockASMCFG> order = new ArrayList<>();

        dfsWalk(nodes.get(0), order, visited);
        Collections.reverse(order);
        return order;
    }
    @Override
    public String toString() {
        return nodes.toString();
    }
    public String CFGtoDOT(HashMap<BasicBlockASMCFG,String> in, HashMap<BasicBlockASMCFG,String> out) {
        StringBuilder result = new StringBuilder();

        // Assume first node is start node
        if (nodes.size() == 0) {
            return "";
        }

        result.append("digraph g {").append("\n\t")
                .append("node [shape=record];").append("\n")
                .append("forcelabels=true;").append("\n");


        HashMap<BasicBlockASMCFG, Integer> visitedIDs = new HashMap<>();
        HashSet<BasicBlockASMCFG> visited = new HashSet<>();

        Stack<BasicBlockASMCFG> stack = new Stack<>();
        stack.push(nodes.get(0));

        while (!stack.isEmpty()) {
            BasicBlockASMCFG popped = stack.pop();

            if (visited.contains(popped)) {
                continue;
            }

            // Add node
            if (!visitedIDs.containsKey(popped)) {
                visitedIDs.put(popped, visitedIDs.size());
            }

            result.append("\t").append(visitedIDs.get(popped))
                    .append("\t [ label=\"")
                    .append("in:\t").append(StringEscapeUtils.escapeJava(in.getOrDefault(popped, ""))).append("\\n")
                    .append(StringEscapeUtils.escapeJava(popped.toString()))
                    .append("out:\t").append(StringEscapeUtils.escapeJava(out.getOrDefault(popped, ""))).append("\\n")
                    .append("\"]\n");

            for (BasicBlockASMCFG child : popped.getChildren()) {
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
    public String CFGtoDOT() {
        return CFGtoDOT(new HashMap<>(), new HashMap<>());
    }
    public ArrayList<BasicBlockASMCFG> getNodes() {
        return nodes;
    }

    public void removeUnreachableNodes(){
        HashMap<BasicBlockASMCFG, Integer> visitedIDs = new HashMap<>();
        HashSet<BasicBlockASMCFG> visited = new HashSet<>();
        Stack<BasicBlockASMCFG> stack = new Stack<>();
        stack.push(nodes.get(0));
        visited.add(nodes.get(0));
        while (!stack.isEmpty()) {
            BasicBlockASMCFG popped = stack.pop();

            for (BasicBlockASMCFG child : popped.getChildren()){
                if (child != null && !visited.contains(child)){
                    visited.add(child);
                    stack.push(child);
                }
            }
        }
        for (BasicBlockASMCFG node : nodes) {
            if (!visited.contains(node)) { // remove this node
                for (BasicBlockASMCFG child : node.getChildren()) {
                    if (child != null) {
                        cleanChild(child,node);
                    }
                }
            }
        }
        ArrayList<BasicBlockASMCFG> newGraph = new ArrayList<>();
        for (BasicBlockASMCFG node : nodes){
            if (visited.contains(node)){
                newGraph.add(node);
            }
        }
        nodes = newGraph;
    }
    private void cleanChild(BasicBlockASMCFG child,BasicBlockASMCFG parent){
        int indexOfParent = child.getPredecessors().indexOf(parent);
        child.removePredecessor(parent);
    }
    public ArrayList<ASMInstruction> getBackASM(){
        ArrayList<ASMInstruction> irs = new ArrayList<>();
        for (BasicBlockASMCFG bb : nodes) {
            for (CFGNode<ASMInstruction> stmt : bb.getBody()) {
                irs.add(stmt.getStmt());
            }
        }
        return irs;
    }

    public static HashMap<BasicBlockASMCFG,String> HashmapBlockString(HashMap<BasicBlockASMCFG,Set<ASMAbstractReg>> mapping, boolean inMap){
        HashMap<BasicBlockASMCFG,String> res = new HashMap<>();
        for (BasicBlockASMCFG node : mapping.keySet()){
            StringBuilder builder = new StringBuilder();
            Set<ASMAbstractReg> regs = mapping.get(node);
            int ind = 0;
            for (ASMAbstractReg reg : regs){
                builder.append(reg.toString()).append(" ");
                ind++;
                if (ind % 10 == 0){
                    ind = 0;
                    builder.append("\n");
                }
            }
            res.put(node,builder.toString());
        }
        return res;
    }

    public static HashMap<BasicBlockCFG,String> HashmapBlockStringIR(HashMap<BasicBlockCFG,Set<IRTemp>> mapping, boolean inMap){
        HashMap<BasicBlockCFG,String> res = new HashMap<>();
        for (BasicBlockCFG node : mapping.keySet()){
            StringBuilder builder = new StringBuilder();
            Set<IRTemp> regs = mapping.get(node);
            int ind = 0;
            for (IRTemp reg : regs){
                builder.append(reg.toString()).append(" ");
                ind++;
                if (ind % 10 == 0){
                    ind = 0;
                    builder.append("\n");
                }
            }
            res.put(node,builder.toString());
        }
        return res;
    }
}
