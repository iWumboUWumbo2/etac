//package aar226_akc55_ayc62_ahl88.cfg.optimizations.ir;
//
//import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMAbstractReg;
//import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMBinOpExpr;
//import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg1;
//import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg2;
//import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg3;
//import aar226_akc55_ayc62_ahl88.asm.Instructions.mov.ASMMov;
//import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
//import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
//import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.LinkedList;
//import java.util.Queue;
//
//public class WorklistIR {
//    private Queue<CFGNode<IRStmt, IRTemp>> worklistNodes;
//
//
//    public WorklistIR(CFGGraph<IRStmt, IRTemp> graph) {
//        worklistNodes = new LinkedList<>();
//        ArrayList<CFGNode<IRStmt, IRTemp>> nodes = graph.getNodes();
//        worklistNodes.addAll(nodes);
//    }
//
//    public void setUse(CFGNode<IRStmt, IRTemp> node) {
//        HashSet<IRTemp> use = new HashSet<>();
//        IRStmt stmt = node.getStmt();
//
//        // if [mov temp, expr] then don't add temp
//        if ((stmt instanceof IRMove irmove &&
//                irmove.target() instanceof IRTemp)) {
//            use.addAll(getTemps(irmove.source(), new ArrayList<>()));
//            node.setUse(use);
//            return;
//        }
//
//        // MEM
//        if (stmt instanceof IRMem mem) {
//            use.addAll(getTemps((T) mem.expr(), new ArrayList<T>()));
//            node.setUse(use);
//            return;
//        }
//
//        // MOV
//        if (stmt instanceof IRMove mov) {
//            use.addAll(getTemps((T) mov.source(), new ArrayList<T>()));
//            use.addAll(getTemps((T) mov.target(), new ArrayList<T>()));
//            node.setUse(use);
//            return;
//        }
//
//        // JUMP
//        if (stmt instanceof IRCJump jump) {
//            use.addAll(getTemps((T) jump.cond(), new ArrayList<T>()));
//            node.setUse(use);
//            return;
//        }
//
//        if (stmt instanceof ASMArg1 instr) {
//            use.addAll(getTemps((T) instr.getLeft(), new ArrayList<T>()));
//            node.setUse(use);
//            return;
//        }
//
//        if (stmt instanceof ASMArg2 instr) {
//            use.addAll(getTemps((T) instr.getLeft(), new ArrayList<T>()));
//            use.addAll(getTemps((T) instr.getRight(), new ArrayList<T>()));
//            node.setUse(use);
//            return;
//        }
//
//        if (stmt instanceof ASMArg3 instr) {
//            use.addAll(getTemps((T) instr.getA1(), new ArrayList<T>()));
//            use.addAll(getTemps((T) instr.getA2(), new ArrayList<T>()));
//            use.addAll(getTemps((T) instr.getA3(), new ArrayList<T>()));
//            node.setUse(use);
//        }
//
//        node.setUse(use);
//    }
//
//
//
//    public ArrayList<T> getTemps(T node, ArrayList<T> temps) {
//        if (node instanceof ASMAbstractReg asmreg) {
//            temps.add((T) asmreg);
//            return temps;
//        }
//        if (node instanceof IRTemp irtemp) {
//            temps.add((T) irtemp);
//            return temps;
//        }
//        if (node instanceof ASMBinOpExpr binop) {
//            temps.addAll(getTemps((T) binop.getLeft(), temps));
//            temps.addAll(getTemps((T) binop.getRight(), temps));
//            return temps;
//        }
//        if (node instanceof IRBinOp binop) {
//            temps.addAll(getTemps((T) binop.left(), temps));
//            temps.addAll(getTemps((T) binop.right(), temps));
//            return temps;
//        }
//
//        return temps;
//    }
//
//    public void setDef(CFGNode<IRStmt, IRTemp> node) {
//        HashSet<IRTemp> def = new HashSet<>();
//        if (node.getStmt() instanceof IRMove irmove &&
//                irmove.target() instanceof IRTemp irtemp) {
//            def.add(irtemp);
//            node.setDef(def);
//        }
//    }
//
//    public HashSet<T> setIn(CFGNode<T> node) {
//        HashSet<T> in = new HashSet<>();
//        ArrayList<CFGNode<T>> predecessors = node.getPredecessors();
//        CFGNode<T> fallThroughChild = node.getFallThroughChild();
//        CFGNode<T> jumpChild = node.getJumpChild();
//
//        in.addAll(node.getOut());
//        for (T deftemp : node.getDef()) {
//            if (in.contains(deftemp)) in.remove(deftemp);
//        }
//        in.addAll(node.getUse());
//
//        return in;
//
////        node.setIn(in);
//    }
//
//    public void setOut(CFGNode<T> node) {
//        HashSet<T> out = new HashSet<>();
//        CFGNode<T> fallThroughChild = node.getFallThroughChild();
//        CFGNode<T> jumpChild = node.getJumpChild();
//
//        if (fallThroughChild != null) {
//            out.addAll(fallThroughChild.getIn());
//        }
//
//        if (jumpChild != null) {
//            out.addAll(jumpChild.getIn());
//        }
//
//        node.setOut(out);
//    }
//
//    public void liveVarAnalysis(CFGGraph<T> graph) {
//        while (!worklistNodes.isEmpty()) {
//            CFGNode<T> node = worklistNodes.poll();
//            setOut(node);
//            HashSet<T> ts = setIn(node);
//
//            if (node.getIn().size() != ts.size()) {
//                for (CFGNode<T> pred : node.getPredecessors()) {
//                    if (!worklistNodes.contains(pred) && pred != null) {
//                        worklistNodes.add(node);
//                    }
//                }
//            }
//
//            node.setIn(ts);
//        }
//
//        // TODO: set up queue, and finish worklist algorithm
//    }
//}
