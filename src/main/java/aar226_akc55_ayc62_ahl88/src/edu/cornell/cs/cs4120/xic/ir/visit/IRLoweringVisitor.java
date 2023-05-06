package aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit;


import aar226_akc55_ayc62_ahl88.Main;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.OptimizationType;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.InternalCompilerError;

import java.util.*;

// Need to think about what to add
class BasicBlock {
    public boolean marked;
    public int ind;
    public int indegree;
    public ArrayList<Integer> predecessors;
    public ArrayList<Integer> successors;
    public ArrayList<IRStmt> statements;
    public ArrayList<String> destLabels;

    public ArrayList<String> originLabels;



    public BasicBlock(int i) {
        ind = i;
        indegree = 0;
        marked = false;
        predecessors = new ArrayList<>();
        successors = new ArrayList<>();
        statements = new ArrayList<>();
        destLabels = new ArrayList<>();
        originLabels = new ArrayList<>();
    }
}

public class IRLoweringVisitor extends IRVisitor {
    private int labelCnt;
    private int tempCnt;
    public boolean folding;

    private String nxtLabel() {
        return String.format("lb%d", (labelCnt++));
    }

    private String nxtTemp() {
        return String.format("tl%d", (tempCnt++));
    }
    public IRLoweringVisitor (IRNodeFactory inf) {
        super(inf);
        labelCnt = 0;
        tempCnt = 0;
        labelToNumber= new HashMap<>();
        folding = Main.opts.isSet(OptimizationType.CONSTANT_FOLDING);
    }
    private HashMap<String,Long> labelToNumber;

    @Override
    protected IRNode leave(IRNode parent, IRNode n, IRNode n_, IRVisitor v_) {
        if (n_ instanceof IRBinOp irbin) return canon(irbin);
        if (n_ instanceof IRCall irc) return canon(irc);
        if (n_ instanceof IRCallStmt ircstmt) return canon(ircstmt);
        if (n_ instanceof IRCJump ircj) return canon(ircj);
        if (n_ instanceof IRCompUnit icu) return canon(icu);
        if (n_ instanceof IRConst ic) return canon(ic);
        if (n_ instanceof IRESeq ireseq) return canon(ireseq);
        if (n_ instanceof IRExp irexp) return  canon(irexp);
        if (n_ instanceof IRFuncDecl irfunc) return canon(irfunc);
        if (n_ instanceof IRJump irj) return canon(irj);
        if (n_ instanceof IRLabel irl) return canon(irl);
        if (n_ instanceof IRMem irmem) return canon(irmem);
        if (n_ instanceof IRMove irmove) return canon(irmove);
        if (n_ instanceof IRName irname) return canon(irname);
        if (n_ instanceof IRReturn irret) return canon(irret);
        if (n_ instanceof IRSeq irseq) return canon(irseq);
        if (n_ instanceof IRTemp irtem) return canon(irtem);
        if (n_ instanceof IRdud dud) return canon(dud);

        throw new Error("Why is node not found");
    }

    private IRNode canon(IRdud dud) {
        return dud;
    }

    private boolean hasNoUnmarkedPredecessors(BasicBlock block,ArrayList<BasicBlock> blocks) {

        boolean noUnmarkedPredecessor = true;
        for (int i : block.predecessors) {
            if (!blocks.get(i).marked){
                noUnmarkedPredecessor = false;
            }
        }


        return noUnmarkedPredecessor;
    }
    private void dfs(BasicBlock b, ArrayList<BasicBlock> res, ArrayList<BasicBlock> unorderedBlocks){
        b.marked = true;
//        System.out.println("START");
        boolean[] visit = new boolean[unorderedBlocks.size()];
        Stack<Integer> stack = new Stack<>();
        stack.add(b.ind);
        while (!stack.isEmpty()){
            int curNode = stack.pop();
            BasicBlock curBlock = unorderedBlocks.get(curNode);
            curBlock.marked = true;
            res.add(curBlock);
            boolean foundAnother = false;
            for (int succ : curBlock.successors){
                if (!visit[succ] && !unorderedBlocks.get(succ).marked){
                    visit[succ] = true;
                    stack.add(succ);
                    foundAnother = true;
                    break;
                }
            }
            if (!foundAnother){
                break;
            }
        }
    }
    private boolean allMarked(ArrayList<BasicBlock> blocks){
        for (BasicBlock b: blocks){
            if (!b.marked){
                return false;
            }
        }
        return true;
    }
    private BasicBlock chooseBlock(ArrayList<BasicBlock> blocks){
        boolean allMarked = true;
        BasicBlock curBlock = null;
        for (BasicBlock b: blocks){
            if (!b.marked && hasNoUnmarkedPredecessors(b,blocks)){
                return b;
            }
            if (!b.marked){
                allMarked = false;
                if (curBlock == null){
                    curBlock = b;
                }
            }
        }
        if (!allMarked){
            return curBlock;
        }
        throw new InternalCompilerError("Should only choose block when not all marked");
    }
    private ArrayList<BasicBlock> goodReordering(ArrayList<BasicBlock> unorderedBlocks){
        ArrayList<BasicBlock> result = new ArrayList<>();
        while (!allMarked(unorderedBlocks)) {
            BasicBlock block = chooseBlock(unorderedBlocks);
            dfs(block,result,unorderedBlocks);
        }
        return result;
    }

    private boolean stop(IRStmt stmt){
        return (stmt instanceof IRJump || stmt instanceof IRCJump || stmt instanceof IRReturn || stmt instanceof IRLabel);
    }

    private void compareBlocks(BasicBlock block1, BasicBlock block2){
        for (String originLabel : block1.originLabels) {
            for (String destLabel : block2.destLabels) {
                if (originLabel.equals(destLabel)) {
                    block1.successors.add(block2.ind);
                    block2.predecessors.add(block1.ind);
                }
            }
        }
    }

    private ArrayList<BasicBlock> createBasicBlocksAndGraph(IRSeq body){
        int ind = 0;
        ArrayList<BasicBlock> blocks = new ArrayList<>();
        BasicBlock dummy = new BasicBlock(ind);
        ind++;
        String lb = nxtLabel();
        dummy.statements.add(new IRJump(new IRName("dummy_head" + lb)));
        labelToNumber.put("dummy_head" + lb,1L);
        dummy.successors.add(1);
        blocks.add(dummy);
        BasicBlock curBlock = new BasicBlock(ind);
        curBlock.predecessors.add(0);
        curBlock.statements.add(new IRLabel("dummy_head" + lb));
        for (IRStmt stmt: body.stmts()){
            if (stop(stmt)) {
                if (stmt instanceof IRJump jmp) {
                    String destName = ((IRName) jmp.target()).name();
                    if (labelToNumber.containsKey(destName)) {
                        labelToNumber.put(destName, labelToNumber.get(destName) + 1);
                    }else{
                        labelToNumber.put(destName,1L);
                    }
                    curBlock.originLabels.add(destName);
                    curBlock.statements.add(stmt);
                }
                else if (stmt instanceof IRCJump cjmp) {
                    curBlock.originLabels.add(cjmp.trueLabel());
                    if (labelToNumber.containsKey(cjmp.trueLabel())) {
                        labelToNumber.put(cjmp.trueLabel(), labelToNumber.get(cjmp.trueLabel()) + 1);
                    }else{
                        labelToNumber.put(cjmp.trueLabel(),1L);
                    }
                    curBlock.originLabels.add(cjmp.falseLabel());
                    if (labelToNumber.containsKey(cjmp.falseLabel())) {
                        labelToNumber.put(cjmp.falseLabel(), labelToNumber.get(cjmp.falseLabel()) + 1);
                    }else{
                        labelToNumber.put(cjmp.falseLabel(),1L);
                    }
                    curBlock.statements.add(stmt);
                }else if (stmt instanceof IRReturn irr){
                    curBlock.statements.add(irr);
                }
                if (curBlock.statements.size() != 0) {
                    BasicBlock preBlock = curBlock;
                    blocks.add(curBlock);
                    ind++;
                    curBlock = new BasicBlock(ind);
                    if (stmt instanceof IRLabel il){ // in case of fall through Label add a jump on purpose
                        preBlock.successors.add(ind); // to maintain correctness
                        curBlock.predecessors.add(preBlock.ind);
                        curBlock.destLabels.add(il.name());
                        curBlock.statements.add(il);
                        preBlock.statements.add(new IRJump(new IRName(il.name())));
                        if (labelToNumber.containsKey(il.name())) {
                            labelToNumber.put(il.name(), labelToNumber.get(il.name()) + 1);
                        }else{
                            labelToNumber.put(il.name(),1L);
                        }
                    }
                }else if (stmt instanceof IRLabel il){ // prev block was empty so we just continue this block
                    curBlock.statements.add(il);
                    curBlock.destLabels.add(il.name());
                    if (!labelToNumber.containsKey(il.name())) {
                        labelToNumber.put(il.name(),0L);
                    }
                }else{
                    throw new InternalCompilerError("BRUH"); // pls try to get
                }
            }else{
                curBlock.statements.add(stmt);
            }
        }
        if (curBlock.statements.size() != 0) {
            blocks.add(curBlock);
        }

        for (int i = 0; i < blocks.size(); i++) {
            for (int j = i; j < blocks.size(); j++) {
                BasicBlock bi = blocks.get(i), bj = blocks.get(j);
                compareBlocks(bi, bj);
                compareBlocks(bj, bi);
            }
        }
        return blocks;

    }
    // Lower each statment then flatten all sequences
    private IRNode canon(IRSeq node) {
//        System.out.println(node);
        ArrayList<IRStmt> flatten = new ArrayList<>();
        for (IRStmt stmt: node.stmts()){
            if (stmt instanceof IRSeq seq){
                flatten.addAll(seq.stmts());
            }else{
                flatten.add(stmt);
            }
        }
        return new IRSeq(flatten);
    }

    // Lower each return expressions then add Return
    private IRNode canon(IRReturn node) {
        ArrayList<IRStmt> stmts = new ArrayList<>();
        ArrayList<String> temps_strs = new ArrayList<>();

        for (IRExpr expr : node.rets()) {
            String ti = nxtTemp();
            temps_strs.add(ti);
            if (expr instanceof IRESeq eseq) {
                if (eseq.stmt() instanceof IRSeq seq){
                    stmts.addAll(seq.stmts());
                }else{
                    stmts.add(eseq.stmt());
                }
                stmts.add(new IRMove(new IRTemp(ti),eseq.expr()));
            }else{
                stmts.add(new IRMove(new IRTemp(ti),expr));
            }
        }
        List<IRExpr> temps = new ArrayList<>();
        for (String tmp : temps_strs) {
            temps.add(new IRTemp(tmp));
        }

        stmts.add(new IRReturn(temps));
        return new IRSeq(stmts);
    }

    // Lower Move be very careful look at slides
    private IRNode canon(IRMove node) {
        IRExpr target = node.target();
        IRExpr source = node.source();
//
        if (target instanceof IRMem mem){
            return moveCommute(node) ? moveNaive(mem,source) : moveGeneral(mem,source);
        }
        return easyMove(node);
    }

    private IRNode easyMove(IRMove node){
        ArrayList<IRStmt> stmts = new ArrayList<>();
        IRExpr targ = node.target();
        IRExpr source = node.source();
        if (node.target() instanceof IRESeq ires1){
            if (ires1.stmt() instanceof IRSeq seq){
                stmts.addAll(seq.stmts());
            }else{
                stmts.add(ires1.stmt());
            }
            targ = ires1.expr();
        }
        if (node.source() instanceof IRESeq ires){
            if (ires.stmt() instanceof IRSeq seq2){
                stmts.addAll(seq2.stmts());
            }else{
                stmts.add(ires.stmt());
            }
            source = ires.expr();
        }
        if (stmts.size() != 0){
            stmts.add(new IRMove(targ,source));
            return new IRSeq(stmts);
        }
        return node;
    }
    // to do
    private boolean moveCommute(IRMove node){
        if (node.target() instanceof IRMem mem && mem.expr() instanceof IRName){
            return true;
        }
        if (node.source() instanceof IRESeq eseqRight) {
            ArrayList<IRNode> rightStatement = new FlattenIrVisitor().visit(eseqRight.stmt());
            ArrayList<IRNode> leftExpression;
            if (node.target() instanceof IRESeq eseqLeft) {
                leftExpression = new FlattenIrVisitor().visit(eseqLeft);
            } else {
                leftExpression = new FlattenIrVisitor().visit(node.target());
            }
            // Make sure right havs no mem
            for (IRNode n : rightStatement) {
                if (n instanceof IRMem) {
                    return false;
                }
            }
            for (IRNode n : leftExpression) {
                if (n instanceof IRMem) {
                    return false;
                }
            }
            boolean rightCall = false;
            for (IRNode n : rightStatement){
                if (n instanceof IRCallStmt call){
                    rightCall = true;
                }
            }
            HashSet<String> rightTemps = usedTemps(rightStatement);
            HashSet<String> leftTemps = usedTemps(leftExpression);
            if (rightCall){
                for (String tempName : leftTemps){
                    if (tempName.startsWith("_RV")){
                        return false;
                    }
                }
            }
            for (String s : rightTemps) {
                if (leftTemps.contains(s)) {
                    return false;
                }
            }
        }
        return true;
    }
    private IRStmt moveNaive(IRMem targ, IRExpr src){
        ArrayList<IRStmt> stmts = new ArrayList<>();
        IRExpr e1 = targ.expr();
        IRExpr e2 = src;
        if (targ.expr() instanceof IRESeq ires){
            if (ires.stmt() instanceof IRSeq seq){
                stmts.addAll(seq.stmts());
            }else{
                stmts.add(ires.stmt());
            }
            e1 = ires.expr();
        }
        if (src instanceof  IRESeq ires2){
            if (ires2.stmt() instanceof IRSeq seq1){
                stmts.addAll(seq1.stmts());
            }else{
                stmts.add(ires2.stmt());
            }
            e2 = ires2.expr();
        }
        if (stmts.size() != 0){
            stmts.add(new IRMove(new IRMem(e1),e2));
            return new IRSeq(stmts);
        }
        return new IRMove(targ,src);
    }

    private IRStmt moveGeneral(IRMem targ, IRExpr src){
        ArrayList<IRStmt> stmts = new ArrayList<>();
        IRExpr e1 = targ.expr();
        IRExpr e2 = src;
        if (targ.expr() instanceof IRESeq ires){
            if (ires.stmt() instanceof IRSeq seq){
                stmts.addAll(seq.stmts());
            }else{
                stmts.add(ires.stmt());
            }
            e1 = ires.expr();
        }
        String t = nxtTemp();
        stmts.add(new IRMove(new IRTemp(t),e1));
        if (src instanceof  IRESeq ires2){
            if (ires2.stmt() instanceof IRSeq seq1){
                stmts.addAll(seq1.stmts());
            }else{
                stmts.add(ires2.stmt());
            }
            e2 = ires2.expr();
        }
        stmts.add(new IRMove(new IRMem(new IRTemp(t)),e2));
        return new IRSeq(stmts);
    }

    // Create Basic Blocks And reorder all the body
    public IRNode canon(IRFuncDecl node) {
        if (node.body() instanceof IRSeq irs){
            ArrayList<IRStmt> orderedStatements = new ArrayList<>();
            ArrayList<BasicBlock> unorderedBlocks = createBasicBlocksAndGraph(irs);
            ArrayList<BasicBlock> orderedBlocks = goodReordering(unorderedBlocks);
            assert unorderedBlocks.size() == orderedBlocks.size() : "after ordering is different size tf";
            for (int i = 0; i< orderedBlocks.size()-1;i++){
                BasicBlock curblk = orderedBlocks.get(i);
                BasicBlock nxtblk = orderedBlocks.get(i+1);
                assert curblk.statements.size() >= 1: "block is empty";
                assert nxtblk.statements.size() >= 1: "dest block is empty";
                IRStmt lastStmt = curblk.statements.get(curblk.statements.size()-1);
                IRStmt firstStmtInNext = nxtblk.statements.get(0);
                if (lastStmt instanceof IRJump jmp && firstStmtInNext instanceof IRLabel il){
                    String name = ((IRName) jmp.target()).name();
                    if (name.equals(il.name())){ // remove jump
                        labelToNumber.put(name, labelToNumber.get(name)-1);
                        curblk.statements.remove(curblk.statements.size()-1);
                    }
                }else if (lastStmt instanceof IRCJump cjmp && firstStmtInNext instanceof IRLabel il){
                    String tlabel = cjmp.trueLabel();
                    String flabel = cjmp.falseLabel();
                    if (tlabel.equals(il.name())){
                        IRExpr newCond;
                        if (cjmp.cond() instanceof IRBinOp inner){
                            if (inner.opType() != IRBinOp.OpType.XOR) {
                                newCond = inner.negate();
                            }else if (inner.opType() == IRBinOp.OpType.XOR && inner.left() instanceof IRConst c && (c.value() == 1L)){
                                newCond = inner.right();
                            }else if (inner.opType() == IRBinOp.OpType.XOR && inner.right() instanceof IRConst c && (c.value() == 1L)){
                                newCond = inner.left();
                            }else{
                                newCond = new IRBinOp(IRBinOp.OpType.XOR,new IRConst(1),cjmp.cond());
                            }
                        }else{
                            newCond = new IRBinOp(IRBinOp.OpType.XOR,new IRConst(1),cjmp.cond());
                        }
//                        newCond = new IRBinOp(IRBinOp.OpType.XOR,new IRConst(1),cjmp.cond());
                        IRCJump newCJump = new IRCJump(newCond, flabel,null);
                        labelToNumber.put(tlabel, labelToNumber.get(tlabel)-1);
                        curblk.statements.set(curblk.statements.size()-1,newCJump);
                    }else if (flabel.equals(il.name())){
                        IRCJump newCJump = new IRCJump(cjmp.cond(), tlabel,null);
                        labelToNumber.put(flabel, labelToNumber.get(flabel)-1);
                        curblk.statements.set(curblk.statements.size()-1,newCJump);
                    }else{
//                        System.out.println("yikes somehow need double jump again idk?");
                        IRCJump newCJump = new IRCJump(cjmp.cond(), tlabel,null);
                        curblk.statements.set(curblk.statements.size()-1,newCJump);
                        curblk.statements.add(new IRJump(new IRName(flabel)));
                    }
                }
            }
            BasicBlock lastBlock = orderedBlocks.get(orderedBlocks.size()-1);
            IRStmt lastStmt = lastBlock.statements.get(lastBlock.statements.size()-1);
            if (lastStmt instanceof IRCJump cjmp){
                IRCJump newCJump = new IRCJump(cjmp.cond(), cjmp.trueLabel(),null);
                lastBlock.statements.set(lastBlock.statements.size()-1,newCJump);
                lastBlock.statements.add(new IRJump(new IRName(cjmp.falseLabel())));
            }
            for (HashMap.Entry<String, Long> entry : labelToNumber.entrySet()) {
                Long value = entry.getValue();
                assert value >= 0L: "Labels can't become negative";
                // ...
            }
            int del = 0;
            for (BasicBlock b: orderedBlocks){
                ArrayList<IRStmt> nxtBlockStmt = new ArrayList<>();
                for (IRStmt s: b.statements){
                    if (s instanceof IRLabel ir){
                        if (labelToNumber.get(ir.name()) > 0){
                            orderedStatements.add(s);
                            nxtBlockStmt.add(s);
                        }else{
                            del++;
                        }
                    }else {
                        orderedStatements.add(s);
                        nxtBlockStmt.add(s);
                    }
                }
                b.statements = nxtBlockStmt;
            }
            ArrayList<BasicBlock> cleanBlocks = new ArrayList<>();
            for (BasicBlock b: orderedBlocks){
                if (b.statements.size() > 0){
                    cleanBlocks.add(b);
                }
            }
            IRFuncDecl func = new IRFuncDecl(node.name(),new IRSeq(orderedStatements));
            func.functionSig = node.functionSig;
            return func;
        }else{
            throw new InternalCompilerError("METHOD BODY NOT SEQ");
        }
    }


    // Lift Statement that is it
    private IRNode canon(IRExp node) {
        if (node.expr() instanceof IRESeq ire) {
            if (ire.stmt() instanceof IRSeq seq){
                return new IRSeq(seq.stmts());
            }else{
                return ire.stmt();
            }
        }
        return new IRSeq();
    }

    // do nothing
    private IRNode canon(IRCompUnit node) {
        return node;
    }

    // Lower each Expr
    private IRNode canon(IRCallStmt node) {
        ArrayList<IRStmt> stmts = new ArrayList<>();
        ArrayList<String> temps_strs = new ArrayList<>();

        for (IRExpr expr : node.args()) {
            String ti = nxtTemp();
            temps_strs.add(ti);
            if (expr instanceof IRESeq eseq) {
                if (eseq.stmt() instanceof IRSeq seq){
                    stmts.addAll(seq.stmts());
                }else{
                    stmts.add(eseq.stmt());
                }
                stmts.add(new IRMove(new IRTemp(ti),eseq.expr()));
            }else{
                stmts.add(new IRMove(new IRTemp(ti),expr));
            }
        }

        List<IRExpr> temps = new ArrayList<>();
        for (String tmp : temps_strs) {
            temps.add(new IRTemp(tmp));
        }

        stmts.add(new IRCallStmt(node.target(), node.n_returns(), temps));

        return new IRSeq(stmts);
    }
    // Lower each Expr we never call this lol?
    private IRNode canon(IRCall node) {
        return node;
    }

    // if commute do that otherwise do normal
    private IRNode canon(IRBinOp node) {
        return doesBinopCommunte(node) ? commuteBinop(node) : defaultBinop(node);
    }


    private HashSet<String> usedTemps(ArrayList<IRNode> nodes){
        HashSet<String> res = new HashSet<>();
        for (IRNode node : nodes){
            if (node instanceof IRTemp temp){
                res.add(temp.name());
            }
        }
        return res;
    }
    // to do
    private boolean doesBinopCommunte(IRBinOp node){
        IRExpr right = node.right();
        if (right instanceof IRESeq eseqRight) {
            ArrayList<IRNode> rightStatement = new FlattenIrVisitor().visit(eseqRight.stmt());
            ArrayList<IRNode> leftExpression;
            if (node.left() instanceof IRESeq eseqLeft) {
                leftExpression = new FlattenIrVisitor().visit(eseqLeft);
            } else {
                leftExpression = new FlattenIrVisitor().visit(node.left());
            }
            // Make sure right havs no mem
            for (IRNode n : rightStatement) {
                if (n instanceof IRMem) {
                    return false;
                }
            }
            for (IRNode n : leftExpression) {
                if (n instanceof IRMem) {
                    return false;
                }
            }
            boolean rightCall = false;
            for (IRNode n : rightStatement){
                if (n instanceof IRCallStmt call){
                    rightCall = true;
                }
            }
            HashSet<String> rightTemps = usedTemps(rightStatement);
            HashSet<String> leftTemps = usedTemps(leftExpression);
            if (rightCall){
                for (String tempName : leftTemps){
                    if (tempName.startsWith("_RV")){
                        return false;
                    }
                }
            }
            for (IRNode stmt : rightStatement){
                if (stmt instanceof IRMove mov && mov.target() instanceof IRTemp t){
                    if (leftTemps.contains(t.name())){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private IRNode commuteBinop(IRBinOp node){
        ArrayList<IRStmt> hoisted = new ArrayList<>();
        IRExpr e1 = node.left();
        IRExpr e2 = node.right();
        if (node.left() instanceof IRESeq ires1){
            if (ires1.stmt() instanceof IRSeq seq){
                hoisted.addAll(seq.stmts());
            }else{
                hoisted.add(ires1.stmt());
            }
            e1 = ires1.expr();
        }
        if (node.right() instanceof  IRESeq ires2){
            if (ires2.stmt() instanceof IRSeq seq2){
                hoisted.addAll(seq2.stmts());
            }else{
                hoisted.add(ires2.stmt());
            }
            e2 = ires2.expr();
        }
        IRExpr res =new IRBinOp(node.opType(),e1,e2);
        if (e1.isConstant() && e2.isConstant() && folding){
            try {
                long number = IRBinOp.eval(node.opType(),e1.constant(),e2.constant());
                res = new IRConst(number);
            }catch(InternalCompilerError c){

            }
        }
        if (hoisted.size() != 0){
            return new IRESeq(new IRSeq(hoisted),res);
        }else{
            if (node.left().isConstant() && node.right().isConstant() && folding){
                try{
                    long number = IRBinOp.eval(node.opType(),node.left().constant(),node.right().constant());
                    return new IRConst(number);
                }catch (InternalCompilerError c){
                    return node;
                }
            }
        }
        return node;
    }
    private IRNode defaultBinop(IRBinOp node){
        ArrayList<IRStmt> stmts = new ArrayList<>();
        String t1 = nxtTemp();
        IRExpr e2 = node.right();
        if (node.left() instanceof IRESeq ires1){
            if (ires1.stmt() instanceof IRSeq seq){
                stmts.addAll(seq.stmts());
            }else{
                stmts.add(ires1.stmt());
            }
            stmts.add(new IRMove(new IRTemp(t1), ires1.expr()));
        }else{
            stmts.add(new IRMove(new IRTemp(t1), node.left()));
        }

        if (node.right() instanceof IRESeq ires2){
            if (ires2.stmt() instanceof IRSeq seq2){
                stmts.addAll(seq2.stmts());
            }else{
                stmts.add(ires2.stmt());
            }
            e2 = ires2.expr();
        }
//        System.out.println(new IRSeq(stmts));
        return new IRESeq(new IRSeq(stmts),
                new IRBinOp(node.opType(),new IRTemp(t1),e2));
    }

    // canonical
    private IRNode canon(IRConst node){
        return node;
    }
    // canonical
    private IRNode canon(IRLabel node){
        return node;
    }
    // canonical
    private IRNode canon(IRName node){
        return node;
    }
    // canonical
    private IRNode canon(IRTemp node) {
        return node;
    }
    // MEM
    private IRNode canon(IRMem node){
        if (node.expr() instanceof IRESeq ireseq) { // lift expression
            IRStmt svec = ireseq.stmt();
            IRExpr ire = ireseq.expr();
            return new IRESeq(svec, new IRMem(ire));
        }
        return node;
    }
    // Conditional Jump
    private IRNode canon(IRCJump node){
        if (node.cond() instanceof IRESeq ireseq) {
            ArrayList<IRStmt> stmts = new ArrayList<>();
            if (ireseq.stmt() instanceof IRSeq seq){
                stmts.addAll(seq.stmts());
            }else{
                stmts.add(ireseq.stmt());
            }
            IRExpr ire = ireseq.expr();
            stmts.add(new IRCJump(ire, node.trueLabel(), node.falseLabel()));
            return new IRSeq(stmts);
        }
        return node;
    }
    // Jump
    private IRNode canon(IRJump node){
        if (node.target() instanceof IRESeq ireseq) {
            ArrayList<IRStmt> stmts = new ArrayList<>();
            if (ireseq.stmt() instanceof IRSeq seq){
                stmts.addAll(seq.stmts());
            }else{
                stmts.add(ireseq.stmt());
            }
            IRExpr ire = ireseq.expr();
            stmts.add(new IRJump(ire));
            return new IRSeq(stmts);
        }
        return node;
    }
    // ESEQ
    private IRNode canon(IRESeq node){
        if (node.expr() instanceof IRESeq ireseq) {
            IRExpr ire = ireseq.expr();
            ArrayList<IRStmt> stmts = new ArrayList<>();
            if (node.stmt() instanceof  IRSeq seqNode){
                stmts.addAll(seqNode.stmts());
            }else{
                stmts.add(node.stmt());
            }
            if (ireseq.stmt() instanceof IRSeq seq){
                stmts.addAll(seq.stmts());
            }else{
                stmts.add(ireseq.stmt());
            }
            return new IRESeq(new IRSeq(stmts), ire);
        }
        return node;
    }
}