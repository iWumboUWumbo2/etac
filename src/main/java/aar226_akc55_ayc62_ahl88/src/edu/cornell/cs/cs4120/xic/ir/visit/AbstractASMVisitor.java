package aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit;

import aar226_akc55_ayc62_ahl88.asm.*;
import aar226_akc55_ayc62_ahl88.asm.Expressions.*;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg2;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMComment;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMLabel;
import aar226_akc55_ayc62_ahl88.asm.Instructions.arithmetic.*;
import aar226_akc55_ayc62_ahl88.asm.Instructions.bitwise.*;
import aar226_akc55_ayc62_ahl88.asm.Instructions.jumps.*;
import aar226_akc55_ayc62_ahl88.asm.Instructions.mov.ASMMov;
import aar226_akc55_ayc62_ahl88.asm.Instructions.mov.ASMMovabs;
import aar226_akc55_ayc62_ahl88.asm.Instructions.stackops.ASMPop;
import aar226_akc55_ayc62_ahl88.asm.Instructions.stackops.ASMPush;
import aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine.ASMCall;
import aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine.ASMEnter;
import aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine.ASMLeave;
import aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine.ASMRet;
import aar226_akc55_ayc62_ahl88.asm.Instructions.tstcmp.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.InternalCompilerError;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * NOTES:
 * - branches: use cmp then corresponding jump
 * - multireturn: store first argument in rax, push rest on stack
 * - function args: rdi, rsi, rdx, rcx, r8, r9, push rest on stack
 * - callee saved: rbx, rbp, and r12–r15
 * - caller saved: everything else
 * THINGS WE NEEEEEEEEEEEDDDDDDDDD
 * - ir compunit ->
 *      add glob mem locations
 *      call visit to all functions
 * - ir func decl ->
 *      create new label at the top for function name
 *      follow abi for args and register alloc
 *      call visit on each statement
 *      flatten arraylist we get back
 *      follow abi for correct func exit
 * - jump -> jr <label name>
 * - cjump ->
 *      two args: cmp <t1> <t2>; j<flag> <label name>
 *      if instance of IRConst and is boolliteral: true -> jr / false -> nothing
 * - move ->
 *      mooooooooooooove
 *      contains expr: used for dynamic tiling
 *      tiling
 * - mem
 * - binop
 * - call stmt ->
 *      init function call follow abi for function call (store func args correctly)
 *      jump to function
 *      clean accroding to ABI spec (be careful about multireturns and stack dead space)
 *      pop from function
 * - IR Label -> ASM label
 * Generic ASM Class will be called ASMNode
 */

public class AbstractASMVisitor {
    private int tempCnt = 0;

    private final HashMap<String, Pair<Integer,Integer>> functionsNameToSig = new HashMap<>();

    private String nxtTemp() {
        return String.format("_ASMReg_t%d", (tempCnt++));
    }

    public ArrayList<ASMInstruction> visit(IRData node){
        throw new InternalCompilerError("not visiting IRDATA");
    }

    public ASMInstruction binopCondToOpCode(IRBinOp bin,IRCJump node){
        ASMNameExpr label = new ASMNameExpr(node.trueLabel());
        return switch (bin.opType()){
            case EQ -> new ASMJumpEqual(label);
            case NEQ -> new ASMJumpNotEqual(label);
            case LT -> new ASMJumpLT(label);
            case ULT -> new ASMJumpULT(label);
            case GT -> new ASMJumpGT(label);
            case LEQ -> new ASMJumpLE(label);
            case GEQ -> new ASMJumpGE(label);
            default -> throw new InternalCompilerError("binop Cond is not boolean it is: " + bin);
        };
    }
    public ArrayList<ASMInstruction> visit(IRCJump node) {
        ArrayList<ASMInstruction> instructions = new ArrayList<>();

        IRExpr condition = node.cond();

        if (condition instanceof IRBinOp c) { // maybe test is faster
            // create function for IRBINOP
            // DO A CMP instead
            if (c.opType() == IRBinOp.OpType.XOR){ //fix this shit
                ASMAbstractReg tleft = munchIRExpr(c.left());
                ASMAbstractReg tright = munchIRExpr(c.right());
                ASMTempExpr destTemp = new ASMTempExpr(nxtTemp());
                if (c.left() instanceof IRConst cons){
                    instructions.addAll(c.right().getBestInstructions());
                    instructions.add(new ASMMov(destTemp,tright));
                    instructions.add(new ASMXor(destTemp,new ASMConstExpr(cons.value())));
                }else if (c.right() instanceof  IRConst cons){
                    instructions.addAll(c.left().getBestInstructions());
                    instructions.add(new ASMMov(destTemp,tleft));
                    instructions.add(new ASMXor(destTemp,new ASMConstExpr(cons.value())));
                }else{
                    instructions.addAll(c.left().getBestInstructions());
                    instructions.addAll(c.right().getBestInstructions());
                    instructions.add(new ASMMov(destTemp,tleft));
                    instructions.add(new ASMXor(destTemp,tright));
                }
                instructions.add(new ASMTest(destTemp,destTemp));
                instructions.add(new ASMJumpNotEqual(new ASMNameExpr(node.trueLabel())));
            }else {
                ASMAbstractReg tleft = munchIRExpr(c.left());
                ASMAbstractReg tright = munchIRExpr(c.right());
                instructions.addAll(c.left().getBestInstructions()); // instrs to create left temp
                instructions.addAll(c.right().getBestInstructions()); // instrs to create right temp
                instructions.add(new ASMCmp(tleft, tright)); // cmp
                instructions.add(binopCondToOpCode(c, node));
            }
        } else if (condition instanceof IRConst c) {
            if (c.value() != 0L){ // jump
                instructions.add(new ASMJumpAlways(new ASMNameExpr(node.trueLabel())));
            }
        } else if (condition instanceof IRTemp c) {
//            functionToTemps.get(curFunction).add(c.name());
            ASMTempExpr tempName = new ASMTempExpr(c.name());
            instructions.add(new ASMTest(tempName,tempName));
            instructions.add(new ASMJumpNotEqual(new ASMNameExpr(node.trueLabel())));
            //test t, t
            //jnz l
        } else if (condition instanceof IRMem c) {
            ASMAbstractReg tempMem = munchIRExpr(c);
            instructions.addAll(c.getBestInstructions());
            instructions.add(new ASMTest(tempMem,tempMem));
            instructions.add(new ASMJumpNotEqual(new ASMNameExpr(node.trueLabel())));
            // do temp test and ASM Jump no Equal
        }else{
            throw new InternalCompilerError("CJUMP guard has another type");
        }
        return instructions;
    }
    public ASMCompUnit visit(IRCompUnit node) {
        HashSet<ASMData> globals = new HashSet<>();
        HashMap<String, ArrayList<ASMInstruction>> functionToInstructionList = new HashMap<>();
        HashMap<String, HashSet<String>> functionToTempsMapping = new HashMap<>();
        for (IRData data : node.dataMap().values()) {
            ASMLabel data_label = new ASMLabel(data.name());
            ASMData data_instr = new ASMData(data_label, data.data());
            globals.add(data_instr);
            // add to ASMCOMP UNIT GLOBAL
        }

        for (IRFuncDecl func : node.functions().values()) {
            String curFunction = func.name();
//            functionToTemps.put(func.name(),new HashSet<>());
            ArrayList<ASMInstruction> functionInstructions = visit(func);
//            functionToTempsMapping.put(curFunction,functionToTemps.get(curFunction));
            functionToInstructionList.put(curFunction,functionInstructions);
//            replaceTemps(functionInstructions,curFunction);
//            instructions.addAll(functionInstructions);
        }

        return new ASMCompUnit(globals,functionToInstructionList,functionToTempsMapping,functionsNameToSig);
    }
    public ArrayList<ASMInstruction> visit (IRConst x) {
        ArrayList<ASMInstruction> instructions = new ArrayList<>();
        boolean isInt = x.value() <= Integer.MAX_VALUE && x.value() >= Integer.MIN_VALUE;
        ASMArg2 instruction = (isInt) ? new ASMMov(new ASMTempExpr(nxtTemp()),new ASMConstExpr(x.value()))
                : new ASMMovabs(new ASMTempExpr(nxtTemp()),new ASMConstExpr(x.value()));
        instructions.add(instruction);
        return instructions;
    }
    public ArrayList<ASMInstruction> visit(IRFuncDecl node) {
        ArrayList<ASMInstruction> result = new ArrayList<>();

        // create new Starting label for this Function
        result.add(new ASMLabel(node.name()));

//        enter at Botoom
//        push rbp
//        mov rbp rsp
//        sub rsp, 8*l
//        result.add(new ASMPush(new ASMRegisterExpr("rbp")));
//        result.add(new ASMMov(new ASMRegisterExpr("rbp"),new ASMRegisterExpr("rsp")));

        // need to calculate number of temporaries used

        ArrayList<String> temps = new ArrayList<>();
        // foo(1,2,3,4,5,6,7....) -> rdi, rsi, rdx, rcx, r8, r9, stack
        int numParams = node.functionSig.inputTypes.size();
        int numReturns = node.functionSig.outputTypes.size();
        IRSeq body = (IRSeq) node.body();
        for (int i = 0; i< numParams;i++){
            IRMove nameAndArg = (IRMove) body.stmts().get(i);
            IRTemp name = (IRTemp) nameAndArg.target();
            temps.add(name.name());
        }
        ArrayList<ASMInstruction> bodyInstructions = new ArrayList<>();

        if (numReturns > 2){
//            functionToTemps.get(curFunction).add("_returnBase");
            bodyInstructions.add(new ASMMov(
                    new ASMTempExpr("_returnBase"),
                    new ASMRegisterExpr("rdi")
            ));
        }

        int start = numReturns > 2 ? 2: 1;
        int end   = numReturns > 2 ? numParams +1: numParams;
        for (int i = start; i<=end;i++){

            // Move arg into argI. argI <- RDI
            ASMExpr ARGI = switch (i) {
                case 1 -> new ASMRegisterExpr("rdi");
                case 2 -> new ASMRegisterExpr("rsi");
                case 3 -> new ASMRegisterExpr("rdx");
                case 4 -> new ASMRegisterExpr("rcx");
                case 5 -> new ASMRegisterExpr("r8");
                case 6 -> new ASMRegisterExpr("r9");
                default ->
                        new ASMMemExpr(
                                new ASMBinOpAddExpr(
                                        new ASMRegisterExpr("rbp"),
                                        new ASMConstExpr(8L * (i - 7 + 2))));
            };
//            String tempName = "_ARG" + i;
            String tempName = numReturns > 2 ? temps.get(i-2):temps.get(i-1);
//            functionToTemps.get(curFunction).add(tempName);
            // can't do [stack location] <- [stack location2]
            // need intermediate rax <- [stack location2]
            // then [stack location] <- temp rax
            bodyInstructions.add(new ASMMov(new ASMTempExpr(tempName),ARGI));
        }
        if (node.body() instanceof  IRSeq seq){
            for (int i = numParams;i< seq.stmts().size();i++){
                IRStmt stmt = seq.stmts().get(i);
                bodyInstructions.addAll(stmt.accept(this));
            }
        }else{
            throw new InternalCompilerError("body isn't a seq");
        }
        // add enter at begin.
        // enter 8*L, 0
//        ASMEnter begin = new ASMEnter(new ASMConstExpr(8L*functionToTemps.get(curFunction).size()),new ASMConstExpr(0));
        ASMEnter begin = new ASMEnter(new ASMConstExpr(0),new ASMConstExpr(0));
        result.add(begin);
        result.addAll(bodyInstructions);
        return result;
    }
    public ArrayList<ASMInstruction> visit(IRJump jump) {
        ArrayList<ASMInstruction> instructions = new ArrayList<>();
        if (jump.target() instanceof IRName name) {
            instructions.add(new ASMJumpAlways(new ASMNameExpr(name.name())));
        }
        return instructions;
    }
    public ArrayList<ASMInstruction> visit(IRLabel node) {
        ArrayList<ASMInstruction> instructions = new ArrayList<>();
        instructions.add(new ASMLabel(node.name()));
        return instructions;
    }
    // Always use these three rax, rcx, and rdx
    public ArrayList<ASMInstruction> visit(IRMove node){
        IRExpr dest = node.target();
        IRExpr source = node.source();
        ArrayList<ASMInstruction> instructions = new ArrayList<>();
        String nodeUnFlat = node.toString();
        instructions.add(new ASMComment(nodeUnFlat.replaceAll("\n",""),null));

        // TEMP TEMP
        if (dest instanceof IRTemp t1 && source instanceof IRTemp t2) { // random case for testing atm
            tileTempTemp(t1, t2, instructions);
        // TEMP CONST
        } else if (dest instanceof IRTemp t && source instanceof IRConst x) {
            tileTempConst(t, x, instructions);
        // TEMP MEM
        } else if (dest instanceof IRTemp t && source instanceof IRMem m) {
            tileTempMem(t, m, instructions);
        // TEMP BINOP
        } else if (dest instanceof IRTemp t && source instanceof IRBinOp b) {
            tileTempBinop(t, b, instructions);
        // MEM TEMP
        } else if (dest instanceof IRMem m && source instanceof IRTemp t) {
            tileMemTemp(m, t, instructions);
        // MEM MEM
        } else if (dest instanceof IRMem m1 && source instanceof IRMem m2) {
            tileMemMem(m1, m2, instructions);
        // MEM CONST
        } else if (dest instanceof IRMem m && source instanceof IRConst x) {
            tileMemConst(m, x, instructions);
        // MEM BINOP
        } else if (dest instanceof IRMem m && source instanceof IRBinOp b) {
            tileMemBinop(m, b, instructions);
        }
        else {
            System.out.println(node);
            throw new InternalCompilerError("TODO Other moves");
        }
//        System.out.println("before IRSTMT: ");
//        System.out.println(node);
//        System.out.println("After IRSTMT: ");
//        for (ASMInstruction instrs: instructions){
//            System.out.println(instrs);
//        }
        return instructions;
    }

    // Base Case
    public long tileTempTemp(IRTemp t1, IRTemp t2, ArrayList<ASMInstruction> instrs) {
        instrs.add(new ASMMov(new ASMTempExpr(t1.name()), new ASMTempExpr(t2.name())));
        return 1;
    }

    // Base Case
    public long tileTempConst(IRTemp t, IRConst c, ArrayList<ASMInstruction> instrs) {
        instrs.add(new ASMMov(new ASMTempExpr(t.name()), new ASMConstExpr(c.value())));
        return 1;
    }

    // Can Expand
    public long tileTempMem(IRTemp t, IRMem m, ArrayList<ASMInstruction> instrs) {
        long curBestCost = Long.MAX_VALUE;
        ArrayList<ASMInstruction> curBestInstructions = new ArrayList<>();
        ASMAbstractReg temp = munchIRExpr(m);
        if (false){ // other patterns;

        }else { // catch all case
            if (m.getBestCost() + 1 < curBestCost){
                ArrayList<ASMInstruction> caseInstructions = new ArrayList<>(m.expr().getBestInstructions()); // instructions for Mem
                caseInstructions.add(new ASMMov(new ASMTempExpr(t.name()), new ASMMemExpr(m.expr().getAbstractReg()))); // move the temp mem into ASMMOV
                curBestInstructions = caseInstructions;
                curBestCost = m.getBestCost() + 1;
            }
        }
        instrs.addAll(curBestInstructions);
        return curBestCost;
    }

    // Can Expand
    public long tileTempBinop(IRTemp t, IRBinOp b, ArrayList<ASMInstruction> instrs) {
        long curBestCost = Long.MAX_VALUE;
        ArrayList<ASMInstruction> curBestInstructions = new ArrayList<>();
        ASMAbstractReg tempTemp = munchIRExpr(t);
        ASMAbstractReg binopTemp = munchIRExpr(b);
        if (b.left() instanceof IRTemp tleft && twoOpArith(b) && (t.toString().equals(tleft.toString()))) { // move a (a+tright)
            if (b.right().getBestCost() + 1 < curBestCost) {
                ArrayList<ASMInstruction> caseInstructions = new ArrayList<>(b.right().getBestInstructions());
                caseInstructions.add(new ASMArg2(irOpToASMOp(b), new ASMTempExpr(t.name()), b.right().getAbstractReg()));
                curBestInstructions = caseInstructions;
                curBestCost = b.right().getBestCost() + 1;
            }
        }
        if (b.right() instanceof IRTemp tright && twoOpArith(b) && (t.toString().equals(tright.toString()))) { // move a (e1 + a)
            if (b.left().getBestCost() + 1 < curBestCost) {
                ArrayList<ASMInstruction> caseInstructions = new ArrayList<>(b.left().getBestInstructions());
                caseInstructions.add(new ASMArg2(irOpToASMOp(b), new ASMTempExpr(t.name()), b.left().getAbstractReg()));
                curBestInstructions = caseInstructions;
                curBestCost = b.left().getBestCost() + 1;
            }
        }
        if (b.opType() == IRBinOp.OpType.ADD){
            // lea t1 [t2 + t3]
            if (b.left().getBestCost() + b.right().getBestCost() + 1 < curBestCost){
                ArrayList<ASMInstruction> caseInstructions = new ArrayList<>(b.left().getBestInstructions());
                caseInstructions.addAll(b.right().getBestInstructions());
                caseInstructions.add(new ASMLEA(tempTemp,new ASMMemExpr(new ASMBinOpAddExpr(b.left().getAbstractReg(),b.right().getAbstractReg())))); // lea t1 [t2 + t3]
                curBestInstructions = caseInstructions;
                curBestCost = b.left().getBestCost() + b.right().getBestCost() + 1;
            }
            // lea t1 [5 + t2]
            if (b.left() instanceof IRConst c){
                if (b.right().getBestCost() + 1 < curBestCost) {
                    ArrayList<ASMInstruction> caseInstructions = new ArrayList<>(b.right().getBestInstructions());
                    caseInstructions.add(new ASMLEA(tempTemp,new ASMMemExpr(new ASMBinOpAddExpr(new ASMConstExpr(c.value()),b.right().getAbstractReg()))));
                    curBestInstructions = caseInstructions;
                    curBestCost = b.right().getBestCost() + 1;
                }
            }
            // lea t1 [t2 + 5]
            if (b.right() instanceof IRConst c){
                if (b.left().getBestCost() + 1 < curBestCost) {
                    ArrayList<ASMInstruction> caseInstructions = new ArrayList<>(b.left().getBestInstructions());
                    caseInstructions.add(new ASMLEA(tempTemp,new ASMMemExpr(new ASMBinOpAddExpr(b.left().getAbstractReg(),new ASMConstExpr(c.value())))));
                    curBestInstructions = caseInstructions;
                    curBestCost = b.right().getBestCost() + 1;
                }
            }
        }

        if (false){ // other patterns;

        }else { // catch all case
            if (b.getBestCost() + 1 < curBestCost){
                ArrayList<ASMInstruction> caseInstructions = new ArrayList<>(b.getBestInstructions()); // instructions for Mem
                caseInstructions.add(new ASMMov(new ASMTempExpr(t.name()), binopTemp)); // move the temp mem into ASMMOV
                curBestInstructions = caseInstructions;
                curBestCost = b.getBestCost() + 1;
            }
        }
        instrs.addAll(curBestInstructions);
        return curBestCost;
    }

    // Can Expand
    public long tileMemTemp(IRMem m, IRTemp t, ArrayList<ASMInstruction> instrs) {
        long curBestCost = Long.MAX_VALUE;
        ArrayList<ASMInstruction> curBestInstructions = new ArrayList<>();
        ASMAbstractReg temp = munchIRExpr(m); // side effects
        // deepest pattern
        Pair<ArrayList<ASMInstruction>,Long> scaleOffsetResult = movMemAddScalePatternFromTemp(m,curBestCost,t);
        if (scaleOffsetResult.part2() < curBestCost){
            curBestCost = scaleOffsetResult.part2();
            curBestInstructions = scaleOffsetResult.part1();
            System.out.println("deep matching success");
        }
        if (false){ // other patterns;

        }else { // catch all case
            if (m.getBestCost() + 1 < curBestCost){
                ArrayList<ASMInstruction> caseInstructions = new ArrayList<>(m.expr().getBestInstructions()); // instructions for Mem
                caseInstructions.add(new ASMMov( new ASMMemExpr(m.expr().getAbstractReg()),new ASMTempExpr(t.name()))); // move the temp mem into ASMMOV
                curBestInstructions = caseInstructions;
                curBestCost = m.getBestCost() + 1;
            }
        }
        instrs.addAll(curBestInstructions);
        return curBestCost;
    }
    // Can Expand
    public long tileMemMem(IRMem m1, IRMem m2, ArrayList<ASMInstruction> instrs) {
        long curBestCost = Long.MAX_VALUE;
        ArrayList<ASMInstruction> curBestInstructions = new ArrayList<>();
        ASMAbstractReg leftTemp = munchIRExpr(m1);
        ASMAbstractReg rightTemp = munchIRExpr(m2);

        if (false){ // other patterns;

        }else { // catch all case do right mem then left mem
//            if (m2.getBestCost() + m1.expr().getBestCost() +  1 < curBestCost){
//                ArrayList<ASMInstruction> caseInstructions = new ArrayList<>(); // instructions for Mem
//                caseInstructions.addAll(m2.getBestInstructions());
//                caseInstructions.addAll(m1.expr().getBestInstructions());
//                caseInstructions.add(new ASMMov(new ASMMemExpr(m1.expr().getAbstractReg()), m2.getAbstractReg())); // move the temp mem into ASMMOV
//                curBestInstructions = caseInstructions;
//                curBestCost = m2.getBestCost() + m1.getBestCost() +  1;
//            }

            if (m2.expr().getBestCost() + m1.expr().getBestCost() + 1 < curBestCost){
                ArrayList<ASMInstruction> caseInstructions = new ArrayList<>(); // instructions for Mem
                caseInstructions.addAll(m2.expr().getBestInstructions());
                caseInstructions.addAll(m1.expr().getBestInstructions());
                caseInstructions.add(new ASMMov(new ASMMemExpr(m1.expr().getAbstractReg()), new ASMMemExpr(m2.expr().getAbstractReg()))); // move the temp mem into ASMMOV
                curBestInstructions = caseInstructions;
                curBestCost = m2.expr().getBestCost() + m1.expr().getBestCost() +  1;
            }
        }
        instrs.addAll(curBestInstructions);
        return curBestCost;
    }
    // Can Expand
    public long tileMemConst(IRMem m, IRConst c, ArrayList<ASMInstruction> instrs) {
        long curBestCost = Long.MAX_VALUE;
        ArrayList<ASMInstruction> curBestInstructions = new ArrayList<>();
        ASMAbstractReg temp = munchIRExpr(m);
        if (false){ // other patterns;

        }else { // catch all case
            if (m.getBestCost() + 1 < curBestCost){
                ArrayList<ASMInstruction> caseInstructions = new ArrayList<>(m.expr().getBestInstructions()); // instructions for Mem
                caseInstructions.add(new ASMMov(new ASMMemExpr(m.expr().getAbstractReg()), new ASMConstExpr(c.value()))); // move the temp mem into ASMMOV
                curBestInstructions = caseInstructions;
                curBestCost = m.getBestCost() + 1;
            }
        }
        instrs.addAll(curBestInstructions);
        return curBestCost;
    }
    // Can Expand
    public long tileMemBinop(IRMem m, IRBinOp b, ArrayList<ASMInstruction> instrs) {
        long curBestCost = Long.MAX_VALUE;
        ArrayList<ASMInstruction> curBestInstructions = new ArrayList<>();
        ASMAbstractReg temp1 = munchIRExpr(m);
        ASMAbstractReg temp2 = munchIRExpr(b);
        if (false){ // other patterns;

        }else { // catch all case
            if (m.getBestCost() + b.getBestCost() + 1 < curBestCost){
                ArrayList<ASMInstruction> caseInstructions = new ArrayList<>(); // instructions for Mem
                caseInstructions.addAll(b.getBestInstructions());
                caseInstructions.addAll(m.expr().getBestInstructions());
                caseInstructions.add(new ASMMov(new ASMMemExpr(m.expr().getAbstractReg()), temp2)); // move the temp mem into ASMMOV
                curBestInstructions = caseInstructions;
                curBestCost = m.getBestCost() + 1;
            }
        }
        instrs.addAll(curBestInstructions);
        return curBestCost;
    }


    private ASMAbstractReg munchIRExpr(IRExpr e) {
        IRNode_c top = (IRNode_c) e;
        if (top.visited){
//            System.out.println("visited");
            return top.tempName;
        }
        if (e instanceof IRBinOp binop) {
            return munchBinop(binop);
        }else if (e instanceof IRTemp t){
            return munchTemp(t);
        }else if (e instanceof IRConst cons){
            return munchIRConst(cons);
        }else if (e instanceof IRName name){ // cheese way of doing it
            return munchIRName(name);
        }else if (e instanceof IRMem mem){
            return munchIRMem(mem);
        }else{
            throw new InternalCompilerError("TODO EXPR not tested");
        }
    }
    private ASMNameExpr munchIRName(IRName name) {
        name.visited = true;
        name.bestCost = 0;
        name.bestInstructions = new ArrayList<>();
        ASMNameExpr res = new ASMNameExpr(name.name());
        name.tempName = res;
        return res;
    }

    // Scale has to be a power of 2 (1, 2, 4, 8)
    private boolean isValidScale(long x) {
        return (x != 0) && (x & (x - 1)) == 0;
    }
    private ASMTempExpr munchIRMem(IRMem mem) {
        long curBestCost = Long.MAX_VALUE;
        ArrayList<ASMInstruction> curBestInstructions = new ArrayList<>();
        ASMTempExpr destTemp = new ASMTempExpr(nxtTemp());
        ASMAbstractReg munched = munchIRExpr(mem.expr());

        if (mem.expr() instanceof IRName name){ // other patterns;
            if (mem.expr().getBestCost() + 1 < curBestCost){
                ArrayList<ASMInstruction> caseInstructions = new ArrayList<>(mem.expr().getBestInstructions()); // instructions for Mem
                caseInstructions.add(new ASMMov(destTemp, new ASMMemExpr(new ASMNameExpr(name.name()))));
                curBestInstructions = caseInstructions;
                curBestCost = mem.expr().getBestCost() + 1;
            }
        }
        if (mem.expr() instanceof IRBinOp memBinop && ((IRBinOp) mem.expr()).opType() == IRBinOp.OpType.MUL) {
            // MEM( Blah * Const 8)
            //IRBinOp memBinop = (IRBinOp) mem.expr();
            IRExpr left = memBinop.left();
            IRExpr right = memBinop.right();

            if (left instanceof IRConst c && isValidScale(c.value()) && right instanceof IRTemp rTemp) {
                if (1 < curBestCost) {
                    ArrayList<ASMInstruction> caseInstructions = new ArrayList<>(); // instructions for Mem
                    caseInstructions.add(new ASMMov(destTemp, new ASMMemExpr(new ASMBinOpMultExpr(new ASMConstExpr(c.value()), new ASMTempExpr(rTemp.name())))));
                    curBestInstructions = caseInstructions;
                    curBestCost = 1;
                }
            } else if (right.isConstant() && isValidScale(((IRConst) right).value()) && left instanceof IRTemp) {
                if (1 < curBestCost) {
                    ArrayList<ASMInstruction> caseInstructions = new ArrayList<>(); // instructions for Mem
                    caseInstructions.add(new ASMMov(destTemp, new ASMMemExpr(new ASMBinOpMultExpr(new ASMConstExpr(((IRConst) right).value()), new ASMTempExpr(((IRTemp) left).name())))));
                    curBestInstructions = caseInstructions;
                    curBestCost = 1;
                }
            }
        }

        if (mem.expr() instanceof IRBinOp memBinop && memBinop.opType() == IRBinOp.OpType.ADD) {
            // MEM (ADD BLAH, MUL(TEMP, CONST))
            IRExpr left = memBinop.left();
            IRExpr right = memBinop.right();

            // memBinop is (ADD BLAH, MUL(TEMP, CONST))
            if (left instanceof IRBinOp leftBinop && leftBinop.opType() == IRBinOp.OpType.MUL && right instanceof IRTemp rTemp) {
                // left Binop MUL(TEMP, CONST)
                IRExpr leftLeft = leftBinop.left();
                IRExpr leftRight = leftBinop.right();
//                ASMConstExpr addConst = new ASMConstExpr(rConst.value());
                ASMTempExpr addTemp = new ASMTempExpr(rTemp.name());

                if (leftLeft.isConstant() && isValidScale(((IRConst) leftLeft).value()) && leftRight instanceof IRTemp) {
                    if (1 < curBestCost) {
                        ASMTempExpr t = new ASMTempExpr(((IRTemp) leftRight).name());
                        ASMConstExpr multConst = new ASMConstExpr(((IRConst) leftLeft).value());
                        ASMBinOpMultExpr mult = new ASMBinOpMultExpr(t, multConst);
                        ASMBinOpAddExpr add = new ASMBinOpAddExpr(mult, addTemp);

                        ArrayList<ASMInstruction> caseInstructions = new ArrayList<>(); // instructions for Mem
                        caseInstructions.add(new ASMMov(destTemp, new ASMMemExpr(add)));
                        curBestInstructions = caseInstructions;
                        curBestCost = 1;
                    }
                } else if (leftRight.isConstant() && isValidScale(((IRConst) leftRight).value()) && leftLeft instanceof IRTemp) {
                    if (1 < curBestCost) {
                        ASMTempExpr t = new ASMTempExpr(((IRTemp) leftLeft).name());
                        ASMConstExpr multConst = new ASMConstExpr(((IRConst) leftRight).value());

                        ASMBinOpMultExpr mult = new ASMBinOpMultExpr(t, multConst);
                        ASMBinOpAddExpr add = new ASMBinOpAddExpr(mult, addTemp);

                        ArrayList<ASMInstruction> caseInstructions = new ArrayList<>(); // instructions for Mem
                        caseInstructions.add(new ASMMov(destTemp, new ASMMemExpr(add)));
                        curBestInstructions = caseInstructions;
                        curBestCost = 1;
                    }
                }
            }
            if (right instanceof IRBinOp rightBinop && ((IRBinOp) right).opType() == IRBinOp.OpType.MUL && left instanceof IRTemp lTemp) {
                // right Binop MUL(TEMP, CONST)
                IRExpr rightLeft = rightBinop.left();
                IRExpr rightRight = rightBinop.right();
//                ASMConstExpr addConst = new ASMConstExpr(lConst.value());
                ASMTempExpr addTemp = new ASMTempExpr(lTemp.name());

                if (rightLeft.isConstant() && isValidScale(((IRConst) rightLeft).value()) && rightRight instanceof IRTemp) {
                    if (1 < curBestCost) {
                        ASMTempExpr t = new ASMTempExpr(((IRTemp) rightRight).name());
                        ASMConstExpr multConst = new ASMConstExpr(((IRConst) rightLeft).value());

                        ASMBinOpMultExpr mult = new ASMBinOpMultExpr(t, multConst);
                        ASMBinOpAddExpr add = new ASMBinOpAddExpr(mult, addTemp);

                        ArrayList<ASMInstruction> caseInstructions = new ArrayList<>(); // instructions for Mem
                        caseInstructions.add(new ASMMov(destTemp, new ASMMemExpr(add)));
                        curBestInstructions = caseInstructions;
                        curBestCost = 1;
                    }
                } else if (rightRight.isConstant() && isValidScale(((IRConst) rightRight).value()) && rightLeft instanceof IRTemp) {
                    if (mem.expr().getBestCost() + 1 < curBestCost) {
                        ASMTempExpr t = new ASMTempExpr(((IRTemp) rightLeft).name());
                        ASMConstExpr multConst = new ASMConstExpr(((IRConst) rightRight).value());

                        ASMBinOpMultExpr mult = new ASMBinOpMultExpr(t, multConst);
                        ASMBinOpAddExpr add = new ASMBinOpAddExpr(mult, addTemp);

                        ArrayList<ASMInstruction> caseInstructions = new ArrayList<>(); // instructions for Mem
                        caseInstructions.add(new ASMMov(destTemp, new ASMMemExpr(add)));
                        curBestInstructions = caseInstructions;
                        curBestCost = 1;
                    }
                }
            }
        }
        if (false) {
        }else { // catch all case
            if (mem.expr().getBestCost() + 1 < curBestCost){
                ArrayList<ASMInstruction> caseInstructions = new ArrayList<>(mem.expr().getBestInstructions()); // instructions for Mem
                caseInstructions.add(new ASMMov(destTemp,new ASMMemExpr(munched)));
                curBestInstructions = caseInstructions;
                curBestCost = mem.expr().getBestCost() + 1;
            }
        }
        mem.visited = true;
        mem.bestCost = curBestCost;
        mem.bestInstructions = curBestInstructions;
        mem.tempName = destTemp;
        return destTemp;
//        return null;
    }
    private ASMTempExpr munchTemp(IRTemp temp) {
        temp.visited = true;
        temp.bestCost = 0;
        temp.bestInstructions = new ArrayList<>();
        ASMTempExpr res = new ASMTempExpr(temp.name());
        temp.tempName = res;
        return res;
    }
    private ASMTempExpr munchIRConst(IRConst c){
        c.visited = true;
        c.bestCost = 1;
        String extraTemp = nxtTemp();
        ArrayList<ASMInstruction> extraInstructions = new ArrayList<>();
        extraInstructions.add(new ASMMov(new ASMTempExpr(extraTemp),new ASMConstExpr(c.value())));
        c.bestInstructions = extraInstructions;
        ASMTempExpr res = new ASMTempExpr(extraTemp);
        c.tempName = res;
        return res;
    }
    private ASMAbstractReg munchBinop(IRBinOp binop) {
        // TODO: LATER ADD TEMP/CONST, TEMP/TEMP, CONST/TEMP, ELSE MUCH
        long curBestCost = Long.MAX_VALUE;
        ASMTempExpr destTemp = new ASMTempExpr(nxtTemp());
        ArrayList<ASMInstruction> curBestInstructions = new ArrayList<>();
        ASMAbstractReg l1 = munchIRExpr(binop.left());
        ASMAbstractReg l2 = munchIRExpr(binop.right());

        if (false) {

        } else {

            curBestInstructions.addAll(binop.left().getBestInstructions());
            curBestInstructions.addAll(binop.right().getBestInstructions());

            switch (binop.opType()) {
                case ADD:
                    if ( binop.left().getBestCost() +
                            binop.right().getBestCost() + 2 < curBestCost) {
                        curBestCost = binop.left().getBestCost() +
                                binop.right().getBestCost() + 2;
                        curBestInstructions.add(new ASMMov(destTemp, l1));
                        curBestInstructions.add(new ASMAdd(destTemp, l2));
                    }
                    break;
                case MUL:
                    if (binop.left().getBestCost() +
                            binop.right().getBestCost() + 2 < curBestCost) {
                        curBestCost = binop.left().getBestCost() +
                                binop.right().getBestCost() + 2;
                        curBestInstructions.add(new ASMMov(destTemp, l1));
                        curBestInstructions.add(new ASMIMul(destTemp, l2));
                    }
                    break;
                case DIV: // rax/div, store result in rax and remainder in rdx
                    if (binop.left().getBestCost() +
                            binop.right().getBestCost() + 3 < curBestCost) {
                        curBestCost = binop.left().getBestCost() +
                                binop.right().getBestCost() + 3;
//                        curBestInstructions.add(new ASMXor(new ASMRegisterExpr("rdx"),new ASMRegisterExpr("rdx")));
                        curBestInstructions.add(new ASMMov(new ASMRegisterExpr("rax"), l1));
                        curBestInstructions.add(new ASMCQTO());
                        curBestInstructions.add(new ASMIDiv(l2));
                        curBestInstructions.add(new ASMMov(destTemp, new ASMRegisterExpr("rax")));
                    }
                    break;
                case SUB:
                    if (binop.left().getBestCost() +
                            binop.right().getBestCost() + 2 < curBestCost) {
                        curBestCost = binop.left().getBestCost() +
                                binop.right().getBestCost() + 2;
                        curBestInstructions.add(new ASMMov(destTemp, l1));
                        curBestInstructions.add(new ASMArg2(ASMOpCodes.SUB, destTemp, l2));
                    }
                    break;
                case HMUL: // TODO: fix this
                    if (binop.left().getBestCost() +
                            binop.right().getBestCost() + 3 < curBestCost) {
                        curBestCost = binop.left().getBestCost() +
                                binop.right().getBestCost() + 3;

                        curBestInstructions.add(new ASMMov(new ASMRegisterExpr("rax"), l1));
                        curBestInstructions.add(new ASMIMul(l2));
                        curBestInstructions.add(new ASMMov(destTemp, new ASMRegisterExpr("rdx")));
                    }
                    break;
                case MOD: // rax/div, store result in rax and remainder in rdx
                    if (binop.left().getBestCost() +
                            binop.right().getBestCost() + 3 < curBestCost) {
                        curBestCost = binop.left().getBestCost() +
                                binop.right().getBestCost() + 3;
//                        curBestInstructions.add(new ASMXor(new ASMRegisterExpr("rdx"),new ASMRegisterExpr("rdx")));
                        curBestInstructions.add(new ASMMov(new ASMRegisterExpr("rax"), l1));
                        curBestInstructions.add(new ASMCQTO());
                        curBestInstructions.add(new ASMIDiv(l2));
                        curBestInstructions.add(new ASMMov(destTemp, new ASMRegisterExpr("rdx")));
                    }
                    break;
                case AND:
                    if (binop.left().getBestCost() +
                            binop.right().getBestCost() + 2 < curBestCost) {
                        curBestCost = binop.left().getBestCost() +
                                binop.right().getBestCost() + 2;
                        curBestInstructions.add(new ASMMov(destTemp, l1));
                        curBestInstructions.add(new ASMAnd(destTemp, l2));
                    }
                    break;
                case OR:
                    if (binop.left().getBestCost() +
                            binop.right().getBestCost() + 2 < curBestCost) {
                        curBestCost = binop.left().getBestCost() +
                                binop.right().getBestCost() + 2;
                        curBestInstructions.add(new ASMMov(destTemp, l1));
                        curBestInstructions.add(new ASMOr(destTemp, l2));
                    }
                    break;
                case XOR:
                    if (binop.left().getBestCost() +
                            binop.right().getBestCost() + 2 < curBestCost) {
                        curBestCost = binop.left().getBestCost() +
                                binop.right().getBestCost() + 2;
                        curBestInstructions.add(new ASMMov(destTemp, l1));
                        curBestInstructions.add(new ASMXor(destTemp, l2));
                    }
                    break;
                case LSHIFT: // logical left shift
                    if (binop.left().getBestCost() +
                            binop.right().getBestCost() + 2 < curBestCost) {
                        curBestCost = binop.left().getBestCost() +
                                binop.right().getBestCost() + 2;
                        curBestInstructions.add(new ASMMov(destTemp, l1));
                        curBestInstructions.add(new ASMShl(destTemp, l2));
                    }
                    break;
                case RSHIFT: // logical right shift
                    if (binop.left().getBestCost() +
                            binop.right().getBestCost() + 2 < curBestCost) {
                        curBestCost = binop.left().getBestCost() +
                                binop.right().getBestCost() + 2;
                        curBestInstructions.add(new ASMMov(destTemp, l1));
                        curBestInstructions.add(new ASMShr(destTemp, l2));
                    }
                    break;
                case ARSHIFT:
                    if (binop.left().getBestCost() +
                            binop.right().getBestCost() + 2 < curBestCost) {
                        curBestCost = binop.left().getBestCost() +
                                binop.right().getBestCost() + 2;
                        curBestInstructions.add(new ASMMov(destTemp, l1));
                        curBestInstructions.add(new ASMSar(destTemp, l2));
                    }
                    break;
                case EQ:
                    if (binop.left().getBestCost() +
                            binop.right().getBestCost() + 4 < curBestCost) {
                        curBestCost = binop.left().getBestCost() +
                                binop.right().getBestCost() + 4;
                        curBestInstructions.add(new ASMMov(destTemp, l1));
                        curBestInstructions.add(new ASMCmp(destTemp, l2));
                        curBestInstructions.add(new ASMSete(new ASMRegisterExpr("al")));
                        curBestInstructions.add(new ASMAnd(new ASMRegisterExpr("rax"), new ASMConstExpr(1)));
                        curBestInstructions.add(new ASMMov(destTemp, new ASMRegisterExpr("rax")));
                    }
                    break;
                case NEQ:
                    if (binop.left().getBestCost() +
                            binop.right().getBestCost() + 4 < curBestCost) {
                        curBestCost = binop.left().getBestCost() +
                                binop.right().getBestCost() + 4;
                        curBestInstructions.add(new ASMMov(destTemp, l1));
                        curBestInstructions.add(new ASMCmp(destTemp, l2));
                        curBestInstructions.add(new ASMSetne(new ASMRegisterExpr("al")));
                        curBestInstructions.add(new ASMAnd(new ASMRegisterExpr("rax"), new ASMConstExpr(1)));
//                instrs.add(new ASMAnd(al, new ASMConstExpr(1))); in clang but not in gcc
                        curBestInstructions.add(new ASMMov(destTemp, new ASMRegisterExpr("rax")));
                    }
                    break;
                case LT:
                    if (binop.left().getBestCost() +
                            binop.right().getBestCost() + 4 < curBestCost) {
                        curBestCost = binop.left().getBestCost() +
                                binop.right().getBestCost() + 4;
                        curBestInstructions.add(new ASMMov(destTemp, l1));
                        curBestInstructions.add(new ASMCmp(destTemp, l2));
                        curBestInstructions.add(new ASMSetl(new ASMRegisterExpr("al")));
                        curBestInstructions.add(new ASMAnd(new ASMRegisterExpr("rax"), new ASMConstExpr(1)));
//                instrs.add(new ASMAnd(al, new ASMConstExpr(1))); in clang but not in gcc
                        curBestInstructions.add(new ASMMov(destTemp, new ASMRegisterExpr("rax")));
                    }
                    break;
                case ULT:
                    if (binop.left().getBestCost() +
                            binop.right().getBestCost() + 4 < curBestCost) {
                        curBestCost = binop.left().getBestCost() +
                                binop.right().getBestCost() + 4;
                        curBestInstructions.add(new ASMMov(destTemp, l1));
                        curBestInstructions.add(new ASMCmp(destTemp, l2));
                        curBestInstructions.add(new ASMSetb(new ASMRegisterExpr("al")));
                        curBestInstructions.add(new ASMAnd(new ASMRegisterExpr("rax"), new ASMConstExpr(1)));
                        //                instrs.add(new ASMAnd(al, new ASMConstExpr(1))); in clang but not in gcc
                        curBestInstructions.add(new ASMMov(destTemp, new ASMRegisterExpr("rax")));
                    }
                    break;
                case GT:
                    if (binop.left().getBestCost() +
                            binop.right().getBestCost() + 4 < curBestCost) {
                        curBestCost = binop.left().getBestCost() +
                                binop.right().getBestCost() + 4;
                        curBestInstructions.add(new ASMMov(destTemp, l1));
                        curBestInstructions.add(new ASMCmp(destTemp, l2));
                        curBestInstructions.add(new ASMSetg(new ASMRegisterExpr("al")));
                        curBestInstructions.add(new ASMAnd(new ASMRegisterExpr("rax"), new ASMConstExpr(1)));
//                instrs.add(new ASMAnd(al, new ASMConstExpr(1))); in clang but not in gcc
                        curBestInstructions.add(new ASMMov(destTemp, new ASMRegisterExpr("rax")));
                    }
                    break;
                case LEQ:
                    if (binop.left().getBestCost() +
                            binop.right().getBestCost() + 4 < curBestCost) {
                        curBestCost = binop.left().getBestCost() +
                                binop.right().getBestCost() + 4;
                        curBestInstructions.add(new ASMMov(destTemp, l1));
                        curBestInstructions.add(new ASMCmp(destTemp, l2));
                        curBestInstructions.add(new ASMSetle(new ASMRegisterExpr("al")));
                        curBestInstructions.add(new ASMAnd(new ASMRegisterExpr("rax"), new ASMConstExpr(1)));
//                instrs.add(new ASMAnd(al, new ASMConstExpr(1))); in clang but not in gcc
                        curBestInstructions.add(new ASMMov(destTemp, new ASMRegisterExpr("rax")));
                    }
                    break;
                case GEQ:
                    if (binop.left().getBestCost() +
                            binop.right().getBestCost() + 4 < curBestCost) {
                        curBestCost = binop.left().getBestCost() +
                                binop.right().getBestCost() + 4;
                        curBestInstructions.add(new ASMMov(destTemp, l1));
                        curBestInstructions.add(new ASMCmp(destTemp, l2));
                        curBestInstructions.add(new ASMSetge(new ASMRegisterExpr("al")));
                        curBestInstructions.add(new ASMAnd(new ASMRegisterExpr("rax"), new ASMConstExpr(1)));
//                instrs.add(new ASMAnd(al, new ASMConstExpr(1))); in clang but not in gcc
                        curBestInstructions.add(new ASMMov(destTemp, new ASMRegisterExpr("rax")));
                    }
                    break;
            }
        }
        binop.bestInstructions = curBestInstructions;
        binop.bestCost = curBestCost;
        binop.visited = true;
        binop.tempName = destTemp;
        return destTemp;
    }




    public ArrayList<ASMInstruction> visit(IRSeq node){
        ArrayList<ASMInstruction> instructions = new ArrayList<>();
        for (IRStmt stmt : node.stmts()) {
            ArrayList<ASMInstruction> stmtInstrs = stmt.accept(this);
            instructions.addAll(stmtInstrs);
        }
        return instructions;
    }
    public ArrayList<ASMInstruction> visit(IRReturn node) {
        //
        ArrayList<ASMInstruction> returnInstructions = new ArrayList<>();
        int returnSize = node.rets().size();

        ArrayList<String> tempNames = new ArrayList<>();
        //in case returns stop being temporaries in the future.
        for (IRExpr e: node.rets()){
             if (e instanceof IRTemp t){
                tempNames.add(t.name());
             }else{
                 System.out.println("return is not a temp? " + e);
                 String nxtName = nxtTemp();
                 tempNames.add(nxtName);
//                 ASMTempExpr tmp = new ASMTempExpr(nxtName);
                 // need to translate
                 throw new InternalCompilerError("return has an element that isn't a temp");
             }
        }
//        functionToTemps.get(curFunction).addAll(tempNames);
        // looping in reverse so rax can be used temporarily until the end
        for (int i = 1; i <= returnSize; i++) {
            // move expression to Return Location
            // Move ret into reti. reti <- RDI
            ASMExpr retI = switch (i) {
                case 1 -> new ASMRegisterExpr("rax");
                case 2 -> new ASMRegisterExpr("rdx");
                default -> new ASMMemExpr(
                        new ASMBinOpAddExpr(
                                new ASMTempExpr("_returnBase"),
                                new ASMConstExpr(8L*(i-3))));
            };
//
//            if (i >2){
//                if (i == 3){
//                    returnInstructions.add(new ASMMov(new ASMRegisterExpr("rsi"),
//                            new ASMTempExpr("_returnBase")));
//                }
//                System.out.println("greater than 3");
//                // just in case we just put everything on the stack lol need intermediate
//                // rcx <- [origin]
//                returnInstructions.add(new ASMMov(new ASMRegisterExpr("rcx"),new ASMTempExpr(tempNames.get(i-1)))); // check this
//                // [dest] <- rcx
//                returnInstructions.add(new ASMMov(retI,new ASMRegisterExpr("rcx")));
//            }else{
//                returnInstructions.add(new ASMMov(retI,new ASMTempExpr(tempNames.get(i-1))));
//            }
            returnInstructions.add(new ASMMov(retI,new ASMTempExpr(tempNames.get(i-1))));
        }

        // leave
        // ret
        returnInstructions.add(new ASMLeave());
        returnInstructions.add(new ASMRet());
        return returnInstructions;
    }
    public ArrayList<ASMInstruction> visit(IRCallStmt node) {
        ArrayList<ASMInstruction> instructions = new ArrayList<>();
        IRName functionName = (IRName) node.target();
        int argSiz = node.args().size();
        ArrayList<String> tempNames = new ArrayList<>();
        //in case returns stop being temporaries in the future.
        // Will have to revisit translation too if we change iRCALLSTMT
        // Move the Push translations to later
        for (IRExpr e: node.args()){
            if (e instanceof IRTemp t){
                tempNames.add(t.name());
            }else{
                System.out.println("call is not a temp? " + e);
                String nxtName = nxtTemp();
                tempNames.add(nxtName);
                ASMTempExpr tmp = new ASMTempExpr(nxtName);
                // need to translate
                throw new InternalCompilerError("return has an element that isn't a temp");
            }
        }
//        functionToTemps.get(curFunction).addAll(tempNames);
        instructions.add(new ASMComment("Add Padding",functionName.name()));
        // add extra stack space for returns
        if (node.n_returns() >2){
            instructions.add(new ASMSub(
                    new ASMRegisterExpr("rsp"),
                    new ASMConstExpr(8* (node.n_returns()-2))));
            instructions.add(new ASMMov(
                    new ASMRegisterExpr("rdi"),
                    new ASMRegisterExpr("rsp")));
        }
        // pushes for Arguments
        if (argSiz >= 6){
            int end = node.n_returns() > 2 ? 6 : 7;
            int ind = tempNames.size();
            while (ind >= end){
                instructions.add(new ASMPush(new ASMTempExpr(tempNames.get(ind-1))));
                ind--;
            }
        }
        int start = node.n_returns() > 2 ? Math.min(argSiz+1,6): Math.min(argSiz,6);
        int end = node.n_returns() > 2 ? 2 : 1;
        for (int i = start; i >= end; i--) {
            // move expression from temp to required register
            // Move ret into reti. reti <- RDI
            ASMExpr argI = switch (i) {
                case 1 -> new ASMRegisterExpr("rdi");
                case 2 -> new ASMRegisterExpr("rsi");
                case 3 -> new ASMRegisterExpr("rdx");
                case 4 -> new ASMRegisterExpr("rcx");
                case 5 -> new ASMRegisterExpr("r8");
                case 6 -> new ASMRegisterExpr("r9");
                default -> throw new InternalCompilerError("should not be in default for Function Call");
            };
            int loc = node.n_returns() > 2? i-2: i-1;
            String tempName = tempNames.get(loc);
            instructions.add(new ASMMov(argI,new ASMTempExpr(tempName)));
        }
        // Align by 16 bytes I have no idea how
        functionsNameToSig.put(functionName.name(),new Pair<>(argSiz,node.n_returns().intValue()));
        instructions.add(new ASMCall(new ASMNameExpr(functionName.name())));

        if (argSiz > 6 && node.n_returns() <= 2){
            instructions.add(new ASMAdd(new ASMRegisterExpr("rsp"),
                    new ASMConstExpr(8L*(argSiz-6))));
        }else if (argSiz > 5 && node.n_returns() > 2){
            instructions.add(new ASMAdd(new ASMRegisterExpr("rsp"),
                    new ASMConstExpr(8L*(argSiz-5))));
        }
        String ret = "_RV";
        for (int i = 1; i<= node.n_returns();i++){
            ASMTempExpr temp = new ASMTempExpr(ret+i);
            if (i == 1){
                instructions.add(new ASMMov(temp,new ASMRegisterExpr("rax")));
            }else if (i == 2){
                instructions.add(new ASMMov(temp,new ASMRegisterExpr("rdx")));
            }else{
                instructions.add(new ASMPop(temp));
            }
        }
        instructions.add(new ASMComment("Undo Padding",functionName.name()));
        return instructions;
    }

    public ArrayList<ASMInstruction> visit(IRExp irExp) {
        System.out.println("don't have irExp");
        return new ArrayList<>();
    }

    public ArrayList<ASMInstruction> visit(IRTemp irTemp) {
        return null;
    }

    public ArrayList<ASMInstruction> visit(IRName irName) {
        return null;
    }

    public ArrayList<ASMInstruction> visit(IRMem irMem) {
        return null;
    }

    public ArrayList<ASMInstruction> visit(IRBinOp irBinOp) {
        return null;
    }

    public ArrayList<ASMInstruction> visit(IRESeq ireSeq) {
        System.out.println("don't have ireSeq");
        return new ArrayList<>();
    }

    public ArrayList<ASMInstruction> visit(IRCall irCall) {
        System.out.println("don't have irCall");
        return new ArrayList<>();
    }

    // add/sub/xor/imul dest, src // dest += src; dest -= src; dest ^= src; dest *= src;

    /**
     * Checks if the IRBinop can be reduced to Two Arguments
     * @param b IRBinop node to check
     * @return the binop can be reduced to two arg asm instruction eg a = a * t1
     */
    private boolean twoOpArith(IRBinOp b){
        return (b.opType() == IRBinOp.OpType.ADD)
                || (b.opType() == IRBinOp.OpType.SUB)
                || (b.opType() == IRBinOp.OpType.XOR)
                || (b.opType() == IRBinOp.OpType.MUL);
    }

    /**
     * Converts the IRBinop Instruction to the Corresponding ASM Opcode
     * @param b IRBinop to Check
     * @return The ASMOpCode
     */
    private ASMOpCodes irOpToASMOp(IRBinOp b){
        return switch (b.opType()){
            case ADD -> ASMOpCodes.ADD;
            case SUB -> ASMOpCodes.SUB;
            case MUL, HMUL -> ASMOpCodes.IMUL;
            case DIV, MOD -> ASMOpCodes.IDIV;
            case AND -> ASMOpCodes.AND;
            case OR -> ASMOpCodes.OR;
            case XOR -> ASMOpCodes.XOR;
            case LSHIFT, ARSHIFT, RSHIFT -> throw new InternalCompilerError("NO ASM SHIFT");
            case EQ -> ASMOpCodes.SETE;
            case NEQ -> ASMOpCodes.SETNE;
            case LT -> ASMOpCodes.SETL;
            case ULT -> ASMOpCodes.SETB;
            case GT -> ASMOpCodes.SETG;
            case LEQ -> ASMOpCodes.SETLE;
            case GEQ -> ASMOpCodes.SETGE;
            case UGE -> ASMOpCodes.SETAE;
        };
    }

    /**
     * Returns the ASMDirective Type in ASM
     * @param name string to Check
     * @return the asm Directive
     */
    private ASMDirectives getType(String name) {
        String type = name.split("_")[0];
        if (type.equals("i") || type.equals("b")) {
            return ASMDirectives.QUAD;
        }
        return ASMDirectives.ZERO;
    }

    /**
     * Returns the Cost and INstructions for the mem scale pattern
     * @param mem Memory node to check
     * @param incomingCost
     * @return returns the pair list of isntructions and the cost of them
     */
    private Pair<ArrayList<ASMInstruction>, Long> movMemAddScalePatternFromTemp(IRMem mem, long incomingCost, IRTemp srcTemp){
        long curBestCost = incomingCost;
        ArrayList<ASMInstruction> curBestInstructions = new ArrayList<>();
        if (mem.expr() instanceof IRBinOp memBinop && memBinop.opType() == IRBinOp.OpType.ADD) {
            // MEM (ADD BLAH, MUL(TEMP, CONST))
            IRExpr left = memBinop.left();
            IRExpr right = memBinop.right();

            // memBinop is (ADD BLAH, MUL(TEMP, CONST))
            if (left instanceof IRBinOp leftBinop && leftBinop.opType() == IRBinOp.OpType.MUL && right instanceof IRTemp rTemp) {
                // left Binop MUL(TEMP, CONST)
                IRExpr leftLeft = leftBinop.left();
                IRExpr leftRight = leftBinop.right();
//                ASMConstExpr addConst = new ASMConstExpr(rConst.value());
                ASMTempExpr addTemp = new ASMTempExpr(rTemp.name());

                if (leftLeft.isConstant() && isValidScale(((IRConst) leftLeft).value()) && leftRight instanceof IRTemp) {
                    if (1 < curBestCost) {
                        ASMTempExpr t = new ASMTempExpr(((IRTemp) leftRight).name());
                        ASMConstExpr multConst = new ASMConstExpr(((IRConst) leftLeft).value());
                        ASMBinOpMultExpr mult = new ASMBinOpMultExpr(t, multConst);
                        ASMBinOpAddExpr add = new ASMBinOpAddExpr(mult, addTemp);

                        ArrayList<ASMInstruction> caseInstructions = new ArrayList<>(); // instructions for Mem
                        caseInstructions.add(new ASMMov(new ASMMemExpr(add),new ASMTempExpr(srcTemp.name())));
                        curBestInstructions = caseInstructions;
                        curBestCost = 1;
                    }
                } else if (leftRight.isConstant() && isValidScale(((IRConst) leftRight).value()) && leftLeft instanceof IRTemp) {
                    if (1 < curBestCost) {
                        ASMTempExpr t = new ASMTempExpr(((IRTemp) leftLeft).name());
                        ASMConstExpr multConst = new ASMConstExpr(((IRConst) leftRight).value());

                        ASMBinOpMultExpr mult = new ASMBinOpMultExpr(t, multConst);
                        ASMBinOpAddExpr add = new ASMBinOpAddExpr(mult, addTemp);

                        ArrayList<ASMInstruction> caseInstructions = new ArrayList<>(); // instructions for Mem
                        caseInstructions.add(new ASMMov(new ASMMemExpr(add),new ASMTempExpr(srcTemp.name())));
                        curBestInstructions = caseInstructions;
                        curBestCost = 1;
                    }
                }
            }
            if (right instanceof IRBinOp rightBinop && ((IRBinOp) right).opType() == IRBinOp.OpType.MUL && left instanceof IRTemp lTemp) {
                // right Binop MUL(TEMP, CONST)
                IRExpr rightLeft = rightBinop.left();
                IRExpr rightRight = rightBinop.right();
//                ASMConstExpr addConst = new ASMConstExpr(lConst.value());
                ASMTempExpr addTemp = new ASMTempExpr(lTemp.name());

                if (rightLeft.isConstant() && isValidScale(((IRConst) rightLeft).value()) && rightRight instanceof IRTemp) {
                    if (1 < curBestCost) {
                        ASMTempExpr t = new ASMTempExpr(((IRTemp) rightRight).name());
                        ASMConstExpr multConst = new ASMConstExpr(((IRConst) rightLeft).value());

                        ASMBinOpMultExpr mult = new ASMBinOpMultExpr(t, multConst);
                        ASMBinOpAddExpr add = new ASMBinOpAddExpr(mult, addTemp);

                        ArrayList<ASMInstruction> caseInstructions = new ArrayList<>(); // instructions for Mem
                        caseInstructions.add(new ASMMov(new ASMMemExpr(add),new ASMTempExpr(srcTemp.name())));
                        curBestInstructions = caseInstructions;
                        curBestCost = 1;
                    }
                } else if (rightRight.isConstant() && isValidScale(((IRConst) rightRight).value()) && rightLeft instanceof IRTemp) {
                    if (mem.expr().getBestCost() + 1 < curBestCost) {
                        ASMTempExpr t = new ASMTempExpr(((IRTemp) rightLeft).name());
                        ASMConstExpr multConst = new ASMConstExpr(((IRConst) rightRight).value());

                        ASMBinOpMultExpr mult = new ASMBinOpMultExpr(t, multConst);
                        ASMBinOpAddExpr add = new ASMBinOpAddExpr(mult, addTemp);

                        ArrayList<ASMInstruction> caseInstructions = new ArrayList<>(); // instructions for Mem
                        caseInstructions.add(new ASMMov(new ASMMemExpr(add),new ASMTempExpr(srcTemp.name())));
                        curBestInstructions = caseInstructions;
                        curBestCost = 1;
                    }
                }
            }
        }
        return new Pair<>(curBestInstructions,incomingCost);
    }
}

//    int index = numParams;
//            while (index < seq.stmts().size()){
//        IRStmt stmt = seq.stmts().get(index);
//        bodyInstructions.addAll(stmt.accept(this));
//        if (stmt instanceof IRCallStmt call){
//        int rvMoves = Math.toIntExact(call.n_returns());
//        for (int i = 1; i <= rvMoves;i++){
//        int getInd = (rvMoves + i-1);
//        IRMove nameAndArg = (IRMove) body.stmts().get(getInd);
//        IRTemp name = (IRTemp) nameAndArg.target();
//        ASMTempExpr retName = new ASMTempExpr(name.name());
//        if (i == 1){
//        bodyInstructions.add(new ASMMov(retName,new ASMRegisterExpr("rax")));
//        }else if (i == 2){
//        bodyInstructions.add(new ASMMov(retName,new ASMRegisterExpr("rdx")));
//        }else{
//        bodyInstructions.add(new ASMPop(retName));
//        }
//        }
//        index += rvMoves+1;
//        }else{
//        index++;
//        }
//        }