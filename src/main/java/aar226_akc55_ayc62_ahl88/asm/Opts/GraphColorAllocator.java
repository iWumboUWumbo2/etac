package aar226_akc55_ayc62_ahl88.asm.Opts;

import aar226_akc55_ayc62_ahl88.asm.ASMCompUnit;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.Expressions.*;
import aar226_akc55_ayc62_ahl88.asm.Instructions.*;
import aar226_akc55_ayc62_ahl88.asm.Instructions.arithmetic.ASMLEA;
import aar226_akc55_ayc62_ahl88.asm.Instructions.mov.ASMMov;
import aar226_akc55_ayc62_ahl88.asm.Instructions.stackops.ASMPop;
import aar226_akc55_ayc62_ahl88.asm.Instructions.stackops.ASMPush;
import aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine.ASMCall;
import aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine.ASMEnter;
import aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine.ASMLeave;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.InternalCompilerError;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

import static aar226_akc55_ayc62_ahl88.Main.writeOutputDot;
import static aar226_akc55_ayc62_ahl88.asm.Opts.CFGGraphBasicBlockASM.HashmapBlockString;
import static aar226_akc55_ayc62_ahl88.asm.Opts.LiveVariableAnalysisASM.*;
import static aar226_akc55_ayc62_ahl88.asm.visit.RegisterAllocationTrivialVisitor.tempsToRegs;
import static aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.AbstractASMVisitor.fixInstrsAbstract;
import static aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.AbstractASMVisitor.fixSingleAbstract;

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

    HashMap<ASMAbstractReg,ASMMemExpr> spillMapping;

    ArrayList<String> validColors;
    int K;
    public boolean failed = false;
    public int attempt;
    ASMCompUnit compUnit;
    String funcName;
    boolean reserveMode;
    ArrayList<String> reservedRegs = new ArrayList<>(List.of("r12", "r13", "r14"));
    boolean mainCalled;
    String file;


    HashMap<ASMAbstractReg,Long> regToCost;
    HashSet<ASMAbstractReg> insertedNodes;

    long INF = 100000;
    /**
     * Graph Coloring Allocator. We don't insert extra defs and uses one time runthrough
     * @param g
     * @param comp
     * @param curFunc
     * @param reserve
     */
    public GraphColorAllocator(CFGGraphBasicBlockASM g, ASMCompUnit comp, String curFunc, boolean reserve, String fileName,boolean isMainCalled){
        mainCalled = isMainCalled;
        file = fileName;
        reserveMode = reserve;
        attempt = 0;
        tempCnt = 0;
        numSpillsplus1 = 1;
        progBlock = g;
        if (reserve){
            validColors = new ArrayList<>(List.of("rcx", "rbx", "rdx", "rax", "r8", "r9", "r10", "r11","r15","rsi", "rdi"));
            K = validColors.size();
        }else{
            validColors = new ArrayList<>(List.of("rcx", "rbx", "rdx", "rax", "r8", "r9", "r10", "r11", "r12", "r13","r14","r15","rsi", "rdi"));
            K = validColors.size();
        }
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
        compUnit = comp;
        funcName = curFunc;
        spillMapping = new HashMap<>();
        regToCost = new HashMap<>();
        insertedNodes = new HashSet<>();
    }

   public void initTemps(){
        HashSet<ASMAbstractReg> temps = new HashSet<>();
        for (BasicBlockASMCFG b : progBlock.getNodes()) {
            for (CFGNode<ASMInstruction> instr : b.getBody()) {
                ASMInstruction unwrapped = instr.getStmt();
                temps.addAll(defsInASM(unwrapped));
                temps.addAll(usesInASMFunc(unwrapped, funcName,mainCalled));
            }
        }
        for (ASMAbstractReg reg : temps){
            adjList.put(reg,new HashSet<>());
            degree.put(reg,0);
            if (insertedNodes.contains(reg)){
                regToCost.put(reg,INF);
            }else {
                regToCost.put(reg, 0L);
            }
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
        initTemps();
        LVA = new LiveVariableAnalysisASM(progBlock,mainCalled);
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
            if (!reserveMode) {
                failed = true;
            }
//            attempt++;
//            RewriteProgram();
//            MainFunc();
        }
    }

//    private void estimateSpillCosts(){
//        for (BasicBlockASMCFG b : progBlock.getNodes()){
//            for (CFGNode<ASMInstruction> node : b.getBody()){
//                ASMInstruction stmt = node.getStmt();
//                Set<ASMAbstractReg> uses =  usesInASM(stmt);
//                for (ASMAbstractReg use: uses){
//                    regToCost.put(use,regToCost.get(use) + 1);
//                }
//            }
//        }
//        for (ASMAbstractReg reg : regToCost)
//    }


    private void degreeInvar(){
        Set<ASMAbstractReg> combined = union(spillWorklist, union(simplifyWorklist,freezeWorklist));
        for (ASMAbstractReg u : combined){
            Set<ASMAbstractReg> comb = union(spillWorklist, union(freezeWorklist, union(precolored,simplifyWorklist)));
            comb.retainAll(adjList.get(u));
            if (degree.get(u) != comb.size()){
                throw new InternalCompilerError("degree violated");
            }
        }
    }

    private void simplifyWorkInvar(){
        for (ASMAbstractReg u : simplifyWorklist){
            boolean cond = degree.get(u) < K && intersect(moveList.get(u),union(activeMoves,worklistMoves)).isEmpty();
            if (!cond){
                throw new InternalCompilerError("simplify violated");
            }
        }
    }

    private void freezeInvar(){
        for (ASMAbstractReg u : freezeWorklist){
            boolean cond = degree.get(u) < K && !intersect(moveList.get(u),union(activeMoves,worklistMoves)).isEmpty();
            if (!cond){
                throw new InternalCompilerError("freeze violated");
            }
        }
    }

    private void spillInvar(){
        for (ASMAbstractReg u : spillWorklist){
            boolean cond = degree.get(u) >= K;
            if (!cond){
                throw new InternalCompilerError("spill violated");
            }
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
                Set<ASMAbstractReg> uses =  usesInASMFunc(instr,funcName,mainCalled);
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
        degreeInvar();
        simplifyWorkInvar();
        freezeInvar();
        spillInvar();
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
            HashSet<ASMRegisterExpr> okColors = validColors.stream().map(ASMRegisterExpr::new)
                    .collect(Collectors.toCollection(HashSet::new));

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
            if (color.get(GetAlias(n)) != null) {
                color.put(n, color.get(GetAlias(n)));
            }
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




    private int tempCnt;


    private int numSpillsplus1;

    private String nxtTemp() {
        return String.format("_tgc%d", (tempCnt++));
    }


    /**
     * Maps the Temporary to the corresponding stack location
     * @param temp
     * @return the stack location of the temporary
     */
    private ASMMemExpr tempToStack(ASMAbstractReg temp){
        return new ASMMemExpr(new ASMBinOpAddExpr(
                new ASMRegisterExpr("rbp"),
                new ASMConstExpr(-8L * numSpillsplus1)));
    }
    /**
     * procedure RewriteProgram()
     * Allocate memory locations for each v ∈ spilledNodes,
     * Create a new temporary vi for each definition and each use,
     * In the program (instructions), insert a store after each definition of a vi , a fetch before each use of a vi .
     * Put all the vi into a set newTemps.
     * spilledNodes ← {}
     * initial ← coloredNodes ∪ coalescedNodes ∪ newTemps coloredNodes ← {}
     * coalescedNodes ← {}
     */
    private void RewriteProgram() {
        HashSet<ASMAbstractReg> newTemps = new HashSet<>();
        for (BasicBlockASMCFG block : progBlock.getNodes()){
            ArrayList<CFGNode<ASMInstruction>> nxtBody = new ArrayList<>();
            for (CFGNode<ASMInstruction> node : block.getBody()){
                ASMInstruction instr = node.getStmt();
                Set<ASMAbstractReg> used = usesInASMFunc(instr,funcName,mainCalled);
                Set<ASMAbstractReg> def = defsInASM(instr);
                HashSet<ASMAbstractReg> spilledDef = intersect(def, spilledNodes);
                HashSet<ASMAbstractReg> spilledUse = intersect(used, spilledNodes);
                ArrayList<CFGNode<ASMInstruction>> extraDefs = new ArrayList<>();
                ArrayList<CFGNode<ASMInstruction>> extraUses = new ArrayList<>();
                HashMap<String,String> replaceMapping = new HashMap<>();
                if (!spilledDef.isEmpty()){
                    for (ASMAbstractReg v : spilledDef){
                        ASMAbstractReg vi = new ASMTempExpr(nxtTemp());
                        if (!spillMapping.containsKey(v)) {
                            ASMMemExpr memLoc = tempToStack(v);
                            spillMapping.put(v, memLoc);
                            numSpillsplus1++;
                            extraDefs.add(new CFGNode<>(new ASMMov(memLoc,vi)));
                        }else{
                            ASMMemExpr memLoc = spillMapping.get(v);
                            extraDefs.add(new CFGNode<>(new ASMMov(memLoc,vi)));
                        }
                        replaceMapping.put(v.toString(),vi.toString());
                    }
                }
                if (!spilledUse.isEmpty()){
                    for (ASMAbstractReg v : spilledUse){
                        ASMMemExpr memLoc = spillMapping.get(v);
                        String vi = replaceMapping.containsKey(v.toString()) ? replaceMapping.get(v.toString()) :  nxtTemp();
                        newTemps.add(new ASMTempExpr(vi));
                        replaceMapping.put(v.toString(),vi);

                        extraUses.add(new CFGNode<>(new ASMMov(new ASMTempExpr(vi),memLoc)));
                    }
                }
                ASMInstruction newInstrs = replaceTempNames(instr,replaceMapping);
                ASMInstruction nonAbstract = fixSingleAbstract(newInstrs);
                node.setStmt(nonAbstract);
                nxtBody.addAll(extraUses);
                nxtBody.add(node);
                nxtBody.addAll(extraDefs);
            }
            block.body = nxtBody;
        }

        spilledNodes.clear();
        insertedNodes.addAll(newTemps);
        initial = union(union(coloredNodes,coalescedNodes),newTemps);
        coloredNodes.clear();
        coalescedNodes.clear();

        regToCost.clear();
        adjSet.clear();
        adjList.clear();
        degree.clear();
        moveList.clear();
        alias.clear();
        color.clear();
    }



    public static <T> HashSet<T> union(Set<T> left, Set<T> right) {
        HashSet<T> combo = new HashSet<T>(left);
        combo.addAll(right);
        return combo;
    }

    public static <T> HashSet<T> intersect(Set<T> left, Set<T> right) {
        HashSet<T> combo = new HashSet<T>(left);
        combo.retainAll(right);
        return combo;
    }


    /**
     * Takes the remaining temporaries that still need to be allocated and do trivial allocation on them
     * @return
     */
    public ArrayList<ASMInstruction> replaceTempReserve(){
        HashMap <String, String> colorMapping = new HashMap<>();
        for (Map.Entry<ASMAbstractReg,ASMRegisterExpr> kv : color.entrySet()){
            colorMapping.put(kv.getKey().toString(),kv.getValue().getRegisterName());
        }

        ArrayList<ASMInstruction> res = new ArrayList<>();
        for (BasicBlockASMCFG block : progBlock.getNodes()){
            for (CFGNode<ASMInstruction> node : block.getBody()){
                ASMInstruction instr = node.getStmt();
                ASMInstruction fixedInstr = fixInstruction(instr,colorMapping);
                if (fixedInstr.getOpCode() == ASMOpCodes.CALL){
                    ASMCall call = (ASMCall) instr;
                    fixedInstr = new ASMCall(call.getLeft(),call.numParams,call.numReturns);
                }
                if (fixedInstr.getOpCode() == ASMOpCodes.MOV || fixedInstr.getOpCode() == ASMOpCodes.MOVABS){
                    ASMArg2 arg2 = (ASMArg2) fixedInstr;
                    if (arg2.getLeft() instanceof ASMRegisterExpr realL &&
                            arg2.getRight() instanceof ASMRegisterExpr realR && realL.equals(realR)){
                    }else{
                        res.add(fixedInstr);
                    }
                }else {
                    res.add(fixedInstr);
                }
            }
        }
        HashSet<ASMAbstractReg> stillAbstract = new HashSet<>();
        for (ASMInstruction instr : res){
            Set<ASMAbstractReg> used = usesInASM(instr);
            Set<ASMAbstractReg> def = defsInASM(instr);
            for (ASMAbstractReg use: used){
                if (use instanceof ASMTempExpr temp){
                    stillAbstract.add(temp);
                }
            }
            for (ASMAbstractReg d: def) {
                if (d instanceof ASMTempExpr temp) {
                    stillAbstract.add(temp);
                }
            }
        }
        ArrayList<ASMInstruction> fixed = fixInstrsAbstract(res);
        if (stillAbstract.size() != 0 && !reserveMode){
            failed = true;
            return new ArrayList<>();
        }else if (stillAbstract.size() != 0 && reserveMode){
            // fix
            return buildMappingForRemainingAbstract(fixed,stillAbstract);
        }else{
            return fixed;
        }
    }


    /**
     * Does Trivial Register Allocation on the remaining Abstract Registers
     * @param instrs
     * @param abstractRegs
     * @return
     */
    private ArrayList<ASMInstruction> buildMappingForRemainingAbstract(ArrayList<ASMInstruction> instrs, HashSet<ASMAbstractReg> abstractRegs){
        HashMap<ASMAbstractReg,ASMMemExpr> temptoStack = new HashMap<>();
        int index = 1;
        for (ASMAbstractReg fake : abstractRegs){
            temptoStack.put(fake,new ASMMemExpr(new ASMBinOpAddExpr(
                    new ASMRegisterExpr("rbp"),
                    new ASMConstExpr(-8L * index))));
            index++;
        }

        ASMEnter nxtEnter = new ASMEnter(new ASMConstExpr(8L*(temptoStack.size())), new ASMConstExpr(0));;
        ArrayList<ASMInstruction> output = new ArrayList<>();
        for (ASMInstruction instr: instrs){
            Set<ASMAbstractReg> used = usesInASM(instr);
            Set<ASMAbstractReg> def = defsInASM(instr);
            ArrayDeque<String> reservedLocalRegs = new ArrayDeque<>(reservedRegs);
            if (instr instanceof ASMEnter){
                output.add(nxtEnter);
                output.add(new ASMPush(new ASMRegisterExpr("r12")));
                output.add(new ASMPush(new ASMRegisterExpr("r13")));
                output.add(new ASMPush(new ASMRegisterExpr("r14")));
            }else if (instr instanceof ASMLeave leave){
                output.add(new ASMPop(new ASMRegisterExpr("r14")));
                output.add(new ASMPop(new ASMRegisterExpr("r13")));
                output.add(new ASMPop(new ASMRegisterExpr("r12")));
                output.add(leave);
            }else{
                HashSet<ASMAbstractReg> spilledDef = intersect(def, abstractRegs);
                HashSet<ASMAbstractReg> spilledUse = intersect(used, abstractRegs);
                HashMap<String,String> replaceMapping = new HashMap<>();
                ArrayList<ASMInstruction> extraDefs = new ArrayList<>();
                ArrayList<ASMInstruction> extraUses = new ArrayList<>();
                for (ASMAbstractReg spilled : spilledUse){
                    String real = ! replaceMapping.containsKey(spilled.toString())? reservedLocalRegs.poll() : replaceMapping.get(spilled.toString());
                    replaceMapping.put(spilled.toString(),real);
                    extraUses.add(new ASMMov(new ASMRegisterExpr(real),temptoStack.get(spilled)));
                }
                reservedLocalRegs = new ArrayDeque<>(reservedRegs);
                for (ASMAbstractReg defReg : spilledDef){
                    String real = ! replaceMapping.containsKey(defReg.toString())? reservedLocalRegs.poll() : replaceMapping.get(defReg.toString());
                    replaceMapping.put(defReg.toString(),real);
                    extraDefs.add(new ASMMov(temptoStack.get(defReg),new ASMRegisterExpr(real)));
                }
                ASMInstruction realInstruction = fixSingleAbstract(fixInstruction(instr,replaceMapping));
                output.addAll(extraUses);
                output.add(realInstruction);
                output.addAll(extraDefs);
            }
        }


        return fixAllStackAlignmentsColor(output,abstractRegs);
    }

    public ASMInstruction fixInstruction(ASMInstruction instr, HashMap <String, String> mapping){
        if (instr instanceof ASMArg1 arg1){
            if (instr instanceof ASMCall){
                return instr;
            }
            ASMExpr left = tempsToRegs(arg1.getLeft(), mapping);
            return new ASMArg1(arg1.getOpCode(),left);
        }else if (instr instanceof ASMArg2 arg2){
            ASMExpr left = tempsToRegs(arg2.getLeft(),mapping);
            ASMExpr right = tempsToRegs(arg2.getRight(),mapping);
            return new ASMArg2(arg2.getOpCode(),left,right);
        }else if (instr instanceof ASMArg3 arg3){
            ASMExpr a1 = tempsToRegs(arg3.getA1(),mapping);
            ASMExpr a2 = tempsToRegs(arg3.getA2(),mapping);
            ASMExpr a3 = tempsToRegs(arg3.getA3(),mapping);
            return new ASMArg3(arg3.getOpCode(),a1,a2,a3);
        }else{
            return instr;
        }
    }

    public ASMInstruction replaceTempNames(ASMInstruction instr, HashMap <String, String> mapping){
        if (instr instanceof ASMArg1 arg1){
            if (instr instanceof ASMCall){
                return instr;
            }
            ASMExpr left = tempsToRegsDefaultSame(arg1.getLeft(), mapping);
            return new ASMArg1(arg1.getOpCode(),left);
        }else if (instr instanceof ASMArg2 arg2){
            ASMExpr left = tempsToRegsDefaultSame(arg2.getLeft(),mapping);
            ASMExpr right = tempsToRegsDefaultSame(arg2.getRight(),mapping);
            return new ASMArg2(arg2.getOpCode(),left,right);
        }else if (instr instanceof ASMArg3 arg3){
            ASMExpr a1 = tempsToRegsDefaultSame(arg3.getA1(),mapping);
            ASMExpr a2 = tempsToRegsDefaultSame(arg3.getA2(),mapping);
            ASMExpr a3 = tempsToRegsDefaultSame(arg3.getA3(),mapping);
            return new ASMArg3(arg3.getOpCode(),a1,a2,a3);
        }else{
            return instr;
        }
    }

    /**
     * Replace the temporaries found in memory with real registers assuming already mapped
     * @param expr
     * @param tempMapping
     * @return
     */
    public static ASMExpr tempsToRegsDefaultSame(ASMExpr expr, HashMap<String,String> tempMapping){
        if (expr instanceof ASMTempExpr temp){ // base case
            if (tempMapping.containsKey(temp.getName())){
                return new ASMTempExpr(tempMapping.get(temp.getName()));
            }
            return temp;
        }else if (expr instanceof ASMMemExpr mem){
            return new ASMMemExpr(tempsToRegsDefaultSame(mem.getMem(),tempMapping));
        }else if (expr instanceof ASMBinOpMultExpr binopMult){
            return new ASMBinOpMultExpr(tempsToRegsDefaultSame(binopMult.getLeft(),tempMapping),
                    tempsToRegsDefaultSame(binopMult.getRight(),tempMapping));
        }else if (expr instanceof ASMBinOpAddExpr binopAdd){
            return new ASMBinOpAddExpr(tempsToRegsDefaultSame(binopAdd.getLeft(),tempMapping),
                    tempsToRegsDefaultSame(binopAdd.getRight(),tempMapping));
        }else if (expr instanceof ASMBinOpSubExpr binopSub){
            return new ASMBinOpSubExpr(tempsToRegsDefaultSame(binopSub.getLeft(),tempMapping),
                    tempsToRegsDefaultSame(binopSub.getRight(),tempMapping));
        } else{ // other base case
            return expr;
        }
    }
    /**
     * Returns whether we need stack alignment or stack. Assumes We found number of temps required already
     * @param calledFunction
     * @return true if we need to insert stackalignment.
     */
    private boolean doWeNeedstackAlignmentColor(String calledFunction, HashSet<ASMAbstractReg> abstractArgs) {
        int paramCount = compUnit.getAllFunctionsSigs().get(calledFunction).part1();
        int returnCount = compUnit.getAllFunctionsSigs().get(calledFunction).part2();
        int returnSpace = Math.max(returnCount - 2, 0);
        int argSpace = Math.max(paramCount - (returnCount > 2 ? 5 : 6), 0);


        // assume 3 pushes
        //
        int stackSize =  1+ 1 + 3 + returnSpace + argSpace + abstractArgs.size();
        // rip, rbp, r12, r13, r14
        return (stackSize & 1) != 0;
    }

    /**
     * Fixes Stack Alignment by adding changes to Stack point in comments.
     * @param instrs
     * @param abstractRegs
     * @return
     */
    private ArrayList<ASMInstruction> fixAllStackAlignmentsColor(ArrayList<ASMInstruction> instrs, HashSet<ASMAbstractReg> abstractRegs) {
        ArrayList<ASMInstruction> alignedFunction = new ArrayList<>(instrs);
        for (int i = 0 ;i< alignedFunction.size();i++){
            ASMInstruction instr = alignedFunction.get(i);
            if (instr instanceof ASMComment comment && comment.getComment().equals("Add Padding")){
                if (doWeNeedstackAlignmentColor(comment.getFunctionName(),abstractRegs)){
                    alignedFunction.set(i,new ASMArg2(ASMOpCodes.SUB, new ASMRegisterExpr("rsp"), new ASMConstExpr(8)));
                    // find end
                    int undoIndex = undoCommentColor(alignedFunction,i+1);
                    alignedFunction.set(undoIndex,new ASMArg2(ASMOpCodes.ADD, new ASMRegisterExpr("rsp"), new ASMConstExpr(8)));
                }
            }
        }
        return alignedFunction;

    }
    private int undoCommentColor(ArrayList<ASMInstruction> instructionsList, int startIndex){
        for (int i = startIndex;i < instructionsList.size();i++){
            ASMInstruction instr = instructionsList.get(i);
            if (instr instanceof ASMComment comment && comment.getComment().equals("Undo Padding")){
                return i;
            }
        }
        throw new InternalCompilerError("did align properly and replace undo");
    }
}
