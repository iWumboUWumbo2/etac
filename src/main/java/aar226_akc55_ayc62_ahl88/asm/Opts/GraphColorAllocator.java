package aar226_akc55_ayc62_ahl88.asm.Opts;

import aar226_akc55_ayc62_ahl88.Main;
import aar226_akc55_ayc62_ahl88.asm.Expressions.*;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg1;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg2;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg3;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;
import aar226_akc55_ayc62_ahl88.asm.Instructions.mov.ASMMov;
import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.InternalCompilerError;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static aar226_akc55_ayc62_ahl88.asm.Opts.LiveVariableAnalysisASM.*;
import static aar226_akc55_ayc62_ahl88.asm.visit.RegisterAllocationTrivialVisitor.flattenMem;

public class GraphColorAllocator {

    CFGGraphBasicBlockASM progBlock;

    LiveVariableAnalysisASM LVA;

    HashSet<ASMAbstractReg> precolored;
    HashSet<ASMAbstractReg> initial;
    HashSet<ASMAbstractReg> simplifyWorklist;
    HashSet<ASMAbstractReg> freezeWorklist;
    HashSet<ASMAbstractReg> spillWorklist;
    HashSet<ASMAbstractReg> spilledNodes;
    HashSet<ASMAbstractReg> coalescedNodes;
    HashSet<ASMAbstractReg> coloredNodes;
    Stack<ASMAbstractReg> selectStack;
    HashSet<ASMMov> coalescedMoves;
    HashSet<ASMMov> constrainedMoves;
    HashSet<ASMMov> frozenMoves;
    HashSet<ASMMov> worklistMoves;
    HashSet<ASMMov> activeMoves;
    HashSet<Pair<ASMAbstractReg,
                ASMAbstractReg>> adjSet;
    HashMap<ASMAbstractReg,
            HashSet<ASMAbstractReg>> adjList;

    HashMap<ASMAbstractReg, Integer> degree;

    HashMap<ASMAbstractReg,HashSet<ASMMov>> moveList;

    HashMap<ASMAbstractReg,ASMAbstractReg> alias;

    HashMap<ASMAbstractReg,ASMRegisterExpr> color;

    ArrayList<String> validColors = new ArrayList<>(List.of("rcx", "rbx", "rdx", "rax", "r8", "r9", "r10", "r11", "r12", "rsi", "rdi"));
    int K = validColors.size();

    public GraphColorAllocator(CFGGraphBasicBlockASM g){
        progBlock = g;
        precolored = new HashSet<>();
        initial = new HashSet<>();
        simplifyWorklist = new HashSet<>();
        freezeWorklist = new HashSet<>();
        spillWorklist = new HashSet<>();
        spilledNodes = new HashSet<>();
        coalescedNodes = new HashSet<>();
        coloredNodes = new HashSet<>();
        selectStack = new Stack<>();
        coalescedMoves = new HashSet<>();
        constrainedMoves = new HashSet<>();
        frozenMoves = new HashSet<>();
        worklistMoves = new HashSet<>();
        activeMoves = new HashSet<>();
        adjSet = new HashSet<>();
        adjList = new HashMap<>();
        degree = new HashMap<>();
        moveList = new HashMap<>();
        alias = new HashMap<>();
        color = new HashMap<>();
        initTemps();
    }

   public void initTemps(){
        HashSet<ASMAbstractReg> temps = new HashSet<>();
        for (BasicBlockASMCFG b : progBlock.getNodes()) {
            for (CFGNode<ASMInstruction> instr : b.getBody()) {
                ASMInstruction unwrapped = instr.getStmt();
                temps.addAll(defsInASM(unwrapped));
                temps.addAll(usesInASM(unwrapped));
            }
        }
        for (ASMAbstractReg reg : temps){
            adjList.put(reg,new HashSet<>());
            degree.put(reg,0);
            moveList.put(reg,new HashSet<>());
            if (reg instanceof ASMRegisterExpr real){
                precolored.add(real);
                color.put(real,real);
            }else if (reg instanceof ASMTempExpr fake){
                initial.add(fake);
            }else{
                throw new InternalCompilerError("how we here");
            }
        }
   }
    public void MainFunc(){
        LVA = new LiveVariableAnalysisASM(progBlock);
        LVA.workList();
        Build();
        MakeWorklist();

        while (!allEmpty()){
            if (!simplifyWorklist.isEmpty()) {
                Simplify();
            } else if (!worklistMoves.isEmpty()) {
                Coalesce();
            } else if (!freezeWorklist.isEmpty()) {
                Freeze();
            } else if (!spillWorklist.isEmpty()) {
                SelectSpill();
            }
        }
        AssignColors();
        if (!spilledNodes.isEmpty()) {
            System.out.println("spilled nodes");
//            RewriteProgram();
//            MainFunc();
        }else{
//            System.out.println("no spilled");
//            System.out.println(color);
        }
    }



    boolean allEmpty() {
        return simplifyWorklist.isEmpty() &&
                worklistMoves.isEmpty() &&
                freezeWorklist.isEmpty() &&
                spillWorklist.isEmpty();
    }
  

    public void Build(){
        for (BasicBlockASMCFG b : progBlock.getNodes()){
            Set<ASMAbstractReg> live = LVA.getOutMapping().get(b);
            for (int i = b.getBody().size() - 1; i >= 0; i--) {
                ASMInstruction instr = b.getBody().get(i).getStmt();
                Set<ASMAbstractReg> uses =  usesInASM(instr);
                Set<ASMAbstractReg> defs = defsInASM(instr);
                if (instr instanceof ASMMov mov
                        && mov.getLeft() instanceof ASMAbstractReg
                        && mov.getRight() instanceof ASMAbstractReg) {
                    live.removeAll(uses);
                    Set<ASMAbstractReg> combined = new HashSet<>(uses);
                    combined.addAll(defs);
                    for (ASMAbstractReg n : combined){
                        moveList.get(n).add(mov);
                    }
                    worklistMoves.add(mov);
                }
                live.addAll(defs);
                for (ASMAbstractReg d : defs){
                    for (ASMAbstractReg l : live){
                        AddEdge(l, d);
                    }
                }

                live.removeAll(defs);
                live.addAll(uses);
            }
        }
    }

    public void MakeWorklist(){
        HashSet<ASMAbstractReg> newInitial = new HashSet<>(initial);
        for (ASMAbstractReg n : newInitial) {
            initial.remove(n);
            if (degree.get(n) >= K) {
                spillWorklist.add(n);
            } else if (MoveRelated(n)) {
                freezeWorklist.add(n);
            }
            else {
                simplifyWorklist.add(n);
            }

        }
    }

    public HashSet<ASMAbstractReg> Adjacent(ASMAbstractReg n) {
        HashSet<ASMAbstractReg> newAdjList = new HashSet<>(adjList.get(n));

        HashSet<ASMAbstractReg> combined = new HashSet<>(selectStack);
        combined.addAll(coalescedNodes);

        newAdjList.removeAll(combined);
        return newAdjList;
    }

    public HashSet<ASMMov> NodeMoves(ASMAbstractReg n) {
        HashSet<ASMMov> nodeMovesRes = new HashSet<>(moveList.get(n));

        HashSet<ASMMov> combined = new HashSet<>(activeMoves);
        combined.addAll(worklistMoves);
        nodeMovesRes.retainAll(combined);

        return nodeMovesRes;
    }

    public boolean MoveRelated(ASMAbstractReg n) {
        return !NodeMoves(n).isEmpty();
    }

    public void Simplify() {
        ASMAbstractReg n = simplifyWorklist.iterator().next();
        simplifyWorklist.remove(n);
        selectStack.push(n);
        for (ASMAbstractReg m : Adjacent(n)) {
            DecrementDegree(m);
        }
    }

    private void DecrementDegree(ASMAbstractReg m) {
        Integer d = degree.get(m);
        degree.put(m, d - 1);

        if (d == K) {
            HashSet<ASMAbstractReg> arg = Adjacent(m);
            arg.add(m);
            EnableMoves(arg);
            spillWorklist.remove(m);

            if (MoveRelated(m)) {
                freezeWorklist.add(m);
            }
            else {
                simplifyWorklist.add(m);
            }
        }
    }

    private void EnableMoves(HashSet<ASMAbstractReg> nodes) {
        for (ASMAbstractReg node : nodes) {
            for (ASMMov m : NodeMoves(node)) {
                if (activeMoves.contains(m)) {
                    activeMoves.remove(m);
                    worklistMoves.add(m);
                }
            }
        }
    }

    public void AddWorkList(ASMAbstractReg u) {
        if (!precolored.contains(u) && !MoveRelated(u) && degree.get(u) < K) {
            freezeWorklist.remove(u);
            simplifyWorklist.add(u);
        }
    }

    public boolean OK(ASMAbstractReg t, ASMAbstractReg r) {
        return degree.get(t) < K || precolored.contains(t) || adjSet.contains(new Pair<>(t, r));
    }

    public boolean Conservative(HashSet<ASMAbstractReg> nodes) {
        int k = 0;
        for (ASMAbstractReg n : nodes) {
            if (degree.get(n) >= K) {
                k++;
            }
        }
        return k < K;
    }

    private void Coalesce() {
        ASMMov m = worklistMoves.iterator().next();

        ASMAbstractReg x = GetAlias((ASMAbstractReg) m.getLeft());
        ASMAbstractReg y = GetAlias((ASMAbstractReg) m.getRight());

        Pair<ASMAbstractReg, ASMAbstractReg> uv;

        if (precolored.contains(y)) {
            uv = new Pair<>(y, x);
        }
        else {
            uv = new Pair<>(x, y);
        }
        worklistMoves.remove(m);
        ASMAbstractReg u = uv.part1();
        ASMAbstractReg v = uv.part2();

        boolean adjCheck = true;
        for (ASMAbstractReg t : Adjacent(v)) {
            adjCheck = adjCheck && OK(t, u);
        }

        HashSet<ASMAbstractReg> comb = Adjacent(u);
        comb.addAll(Adjacent(v));
        boolean consCheck = Conservative(comb);

        if (u.equals(v)) {
            coalescedMoves.add(m);
            AddWorkList(u);
        } else if (precolored.contains(v) || adjSet.contains(uv)) {
            constrainedMoves.add(m);
            AddWorkList(u);
            AddWorkList(v);
        }
        else if ((precolored.contains(u) && adjCheck) || (!precolored.contains(u) && consCheck)) {
            coalescedMoves.add(m);
            Combine(u, v);
            AddWorkList(u);
        }
        else {
            activeMoves.add(m);
        }
    }

    public void Combine(ASMAbstractReg u, ASMAbstractReg v) {
        if (freezeWorklist.contains(v)) {
            freezeWorklist.remove(v);
        }
        else {
            spillWorklist.remove(v);
        }

        coalescedNodes.add(v);
        alias.put(v, u);
        moveList.put(u, union(moveList.get(u), moveList.get(v)));
        HashSet<ASMAbstractReg> singleV = new HashSet<>();
        singleV.add(v);
        EnableMoves(singleV);

        for (ASMAbstractReg t : Adjacent(v)) {
            AddEdge(t, u);
            DecrementDegree(t);
        }

        if (degree.get(u) >= K && freezeWorklist.contains(u)) {
            freezeWorklist.remove(u);
            spillWorklist.add(u);
        }
    }

    private ASMAbstractReg GetAlias(ASMAbstractReg n) {
        if (coalescedNodes.contains(n)) {
            return GetAlias(alias.get(n));
        }
        else {
            return n;
        }
    }

    public void Freeze() {
        ASMAbstractReg u = freezeWorklist.iterator().next();
        freezeWorklist.remove(u);
        simplifyWorklist.add(u);
        FreezeMoves(u);
    }

    private void FreezeMoves(ASMAbstractReg u) {
        for (ASMMov m : NodeMoves(u)) {
            ASMAbstractReg x = (ASMAbstractReg) m.getLeft();
            ASMAbstractReg y = (ASMAbstractReg) m.getRight();

            ASMAbstractReg v;

            if (GetAlias(y).equals(GetAlias(u))) {
                v = GetAlias(x);
            }
            else {
                v = GetAlias(y);
            }

            activeMoves.remove(m);
            frozenMoves.add(m);

            if (freezeWorklist.contains(v) && NodeMoves(v).isEmpty()) {
                freezeWorklist.remove(v);
                simplifyWorklist.add(v);
            }
        }
    }

    //  heuristic for spill
    public void SelectSpill() {
        ASMAbstractReg m = spillWorklist.iterator().next();
        spillWorklist.remove(m);
        simplifyWorklist.add(m);
        FreezeMoves(m);
    }

    public void AssignColors(){
        while (!selectStack.isEmpty()) {
            ASMAbstractReg n = selectStack.pop();
            ArrayDeque<ASMRegisterExpr> okColors = validColors.stream().map(ASMRegisterExpr::new)
                    .collect(Collectors.toCollection(ArrayDeque::new));

            for (ASMAbstractReg w : adjList.get(n)) {
                if (union(coloredNodes, precolored).contains(GetAlias(w))) {
                    okColors.remove(color.get(GetAlias(w)));
                }
            }

            if (okColors.isEmpty()) {
                spilledNodes.add(n);
            }
            else {
                coloredNodes.add(n);
                ASMRegisterExpr c = okColors.iterator().next();
                color.put(n, c);
            }



        }

        for (ASMAbstractReg n : coalescedNodes) {
            color.put(n, color.get(GetAlias(n)));
        }
    }
    public void AddEdge(ASMAbstractReg u, ASMAbstractReg v){
        if (!adjSet.contains(new Pair<>(u, v)) && !u.equals(v)) {
            adjSet.add(new Pair<>(u, v));
            adjSet.add(new Pair<>(v, u));

            if (!precolored.contains(u)) {
                adjList.get(u).add(v);
                degree.put(u, degree.get(u) + 1);
            }

            if (!precolored.contains(v)) {
                adjList.get(v).add(u);
                degree.put(v, degree.get(v) + 1);
            }
        }
    }

    private void RewriteProgram() {
        HashSet<ASMAbstractReg> newTemps = new HashSet<>();




        spilledNodes.clear();
        initial = union(union(coloredNodes,coalescedNodes),newTemps);
        coloredNodes.clear();
        coalescedNodes.clear();
    }


//    private boolean isMove(ASMInstruction instr){
//        return instr instanceof ASMMov || instr instanceof ASMMovabs;
//    }


    public static <T> HashSet<T> union(HashSet<T> left, HashSet<T> right) {
        HashSet<T> combo = new HashSet<T>(left);
        combo.addAll(right);
        return combo;
    }

    public static <T> HashSet<T> intersect(HashSet<T> left, HashSet<T> right) {
        HashSet<T> combo = new HashSet<T>(left);
        combo.retainAll(right);
        return combo;
    }

}
