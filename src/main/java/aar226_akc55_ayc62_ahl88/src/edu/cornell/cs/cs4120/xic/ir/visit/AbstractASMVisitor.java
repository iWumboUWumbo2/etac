package aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit;

import aar226_akc55_ayc62_ahl88.asm.*;
import aar226_akc55_ayc62_ahl88.asm.Expressions.*;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg2;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMLabel;
import aar226_akc55_ayc62_ahl88.asm.Instructions.jumps.ASMJumpNotEqual;
import aar226_akc55_ayc62_ahl88.asm.Instructions.mov.ASMMov;
import aar226_akc55_ayc62_ahl88.asm.Instructions.mov.ASMMovabs;
import aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine.ASMEnter;
import aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine.ASMLeave;
import aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine.ASMRet;
import aar226_akc55_ayc62_ahl88.asm.Instructions.tstcmp.ASMTest;
import aar226_akc55_ayc62_ahl88.asm.Instructions.jumps.ASMJumpAlways;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.InternalCompilerError;

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
 *
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
 *
 *
 * Generic ASM Class will be called ASMNode
 */

public class AbstractASMVisitor {
    private int tempCnt = 0;

    private HashMap<String,HashSet<String>> functionToTemps = new HashMap<>();

    private String curFunction;


    private String nxtTemp() {
        return String.format("_ASMReg_t%d", (tempCnt++));
    }
    private ASMDirectives getType(String name) {
        String type = name.split("_")[0];
        if (type.equals("i") || type.equals("b")) {
            return ASMDirectives.QUAD;
        }
        return ASMDirectives.ZERO;
    }
    // converts an IR TEMP to an ASM TEMP
    private ASMTempExpr tempToASM(IRTemp t) {
        return new ASMTempExpr(t.name());
    }
    // change te parameters if needed
    private ArrayList<ASMInstruction> cJumpBinop(IRBinOp binop){
        ArrayList<ASMInstruction> instructions = new ArrayList<ASMInstruction>();
//        switch (binop.opType()) {
//            case EQ:
//            case NEQ:
//            case LT:
//            case ULT:
//            case GT:
//            case LEQ:
//            case GEQ:
////                ASMArg2 instr1 = new ASMCmp(binop.left(), binop.right());
//
//        }
        return instructions;
    }

    public ArrayList<ASMInstruction> visit(IRData node){
        ArrayList<ASMInstruction> instructions = new ArrayList<ASMInstruction>();

        return instructions;
    }
    public ArrayList<ASMInstruction> visit(IRCJump node) {
        ArrayList<ASMInstruction> instructions = new ArrayList<ASMInstruction>();

        IRExpr condition = node.cond();

        if (condition instanceof IRBinOp c) {
            // create function for IRBINOP
            // DO A CMP instead
            return cJumpBinop(c);
        } else if (condition instanceof IRConst c) {
            if (c.value() != 0L){ // jump
                instructions.add(new ASMJumpAlways(new ASMNameExpr(node.trueLabel())));
            }
        } else if (condition instanceof IRTemp c) {
            functionToTemps.get(curFunction).add(c.name());
            ASMTempExpr tempName = tempToASM(c);
            instructions.add(new ASMTest(tempName,tempName));
            instructions.add(new ASMJumpNotEqual(new ASMNameExpr(node.trueLabel())));
            //test t, t
            //jnz l
        } else if (condition instanceof IRMem c) {
            ASMTempExpr tempForMem = new ASMTempExpr(nxtTemp());
            // accept mem for this temp
            // add move instruction
            // do temp test and ASM Jump no Equal
            return null;
        }else{
            throw new InternalCompilerError("CJUMP guard has another type");
        }
        return instructions;
    }
    public ASMCompUnit visit(IRCompUnit node) {
        HashMap<String, long[]> globals = new HashMap<>();
        HashMap<String, ArrayList<ASMInstruction>> functionToInstructionList = new HashMap<>();
        HashMap<String, HashSet<String>> functionToTempsMapping = new HashMap<>();
        for (IRData data : node.dataMap().values()) {
            ASMLabel data_label = new ASMLabel(data.name());
//            ASMData data_instr = new ASMData(getType(data.name()), new ASMConstExpr(data.data()));
            // add to ASMCOMP UNIT GLOBAL
        }

        for (IRFuncDecl func : node.functions().values()) {
            curFunction = func.name();
            functionToTemps.put(func.name(),new HashSet<>());
            ArrayList<ASMInstruction> functionInstructions = visit(func);
            functionToTempsMapping.put(curFunction,functionToTemps.get(curFunction));
            functionToInstructionList.put(curFunction,functionInstructions);
//            replaceTemps(functionInstructions,curFunction);
//            instructions.addAll(functionInstructions);
        }

        return new ASMCompUnit(globals,functionToInstructionList,functionToTempsMapping);
    }
    public ArrayList<ASMInstruction> visit (IRConst x) {
        ArrayList<ASMInstruction> instructions = new ArrayList<ASMInstruction>();
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


        // foo(1,2,3,4,5,6,7....) -> rdi, rsi, rdx, rcx, r8, r9, stack
        int numParams = node.functionSig.inputTypes.size();
        int numReturns = node.functionSig.outputTypes.size();
        ArrayList<ASMInstruction> bodyInstructions = new ArrayList<>();

        if (numReturns > 2){
            functionToTemps.get(curFunction).add("_ARG0");
            bodyInstructions.add(new ASMMov(
                    new ASMTempExpr("_ARG0"),
                    new ASMRegisterExpr("rdi")
            ));
        }

        int start = numReturns > 2 ? 2: 1;
        int end   = numParams > 2 ? numParams +1: numParams;
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
            String tempName = "_ARG" + i;
            functionToTemps.get(curFunction).add(tempName);
            // can't do [stack location] <- [stack location2]
            // need intermediate rax <- [stack location2]
            // then [stack location] <- temp rax
//            if (i>=7){
//                bodyInstructions.add(new ASMMov(new ASMRegisterExpr("rax"),ARGI));
//                bodyInstructions.add(new ASMMov(new ASMTempExpr(tempName),new ASMRegisterExpr("rax")));
//            }
//            // just do MOV [stack location] <- register
//            else{
//                bodyInstructions.add(new ASMMov(new ASMTempExpr(tempName),ARGI));
//            }
            bodyInstructions.add(new ASMMov(new ASMTempExpr(tempName),ARGI));
        }
        if (node.body() instanceof  IRSeq seq){
            for (IRStmt stmt: seq.stmts()){
                bodyInstructions.addAll(stmt.accept(this));
            }
        }else{
            throw new InternalCompilerError("body isn't a seq");
        }
        // add enter at begin.
        // enter 8*L, 0
        ASMEnter begin = new ASMEnter(new ASMConstExpr(8L*functionToTemps.get(curFunction).size()),new ASMConstExpr(0));
        result.add(begin);
        result.addAll(bodyInstructions);
        return result;
    }
    public ArrayList<ASMInstruction> visit(IRJump jump) {
        ArrayList<ASMInstruction> instructions = new ArrayList<>();
        if (jump.target() instanceof IRName) {
            instructions.add(new ASMJumpAlways(new ASMNameExpr(jump.label())));
        }
        return instructions;
    }
    public ArrayList<ASMInstruction> visit(IRLabel node) {
        ArrayList<ASMInstruction> instructions = new ArrayList<ASMInstruction>();
        instructions.add(new ASMLabel(node.name()));
        return instructions;
    }
    // Always use these three rax, rcx, and rdx
    public ArrayList<ASMInstruction> visit(IRMove node){
        IRExpr dest = node.target();
        IRExpr source = node.source();
        ArrayList<ASMInstruction> instructions = new ArrayList<>();
        if (dest instanceof IRTemp t1 && source instanceof IRTemp t2){ // random case for testing atm
            functionToTemps.get(curFunction).add(t1.name());
            functionToTemps.get(curFunction).add(t2.name());
            instructions.add(new ASMMov(new ASMTempExpr(t1.name()),new ASMTempExpr(t2.name())));
        }else if (dest instanceof IRTemp t1 && source instanceof IRConst x){
            functionToTemps.get(curFunction).add(t1.name());
            boolean isInt = x.value() <= Integer.MAX_VALUE && x.value() >= Integer.MIN_VALUE;
            ASMArg2 instruction = (isInt) ? new ASMMov(new ASMTempExpr(t1.name()),new ASMConstExpr(x.value()))
                    : new ASMMovabs(new ASMTempExpr(t1.name()),new ASMConstExpr(x.value()));
            instructions.add(instruction);
        }else{
            throw new InternalCompilerError("TODO Other moves");
        }

        return instructions;
    }

    public ArrayList<ASMInstruction> visit(IRSeq node){
        ArrayList<ASMInstruction> instructions = new ArrayList<ASMInstruction>();
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
                 ASMTempExpr tmp = new ASMTempExpr(nxtName);
                 // need to translate
                 throw new InternalCompilerError("return has an element that isn't a temp");
             }
        }
        functionToTemps.get(curFunction).addAll(tempNames);
        // looping in reverse so rax can be used temporarily until the end
        for (int i = 1; i <= returnSize; i++) {
            // move expression to Return Location
            // Move ret into reti. reti <- RDI
            ASMExpr retI = switch (i) {
                case 1 -> new ASMRegisterExpr("rax");
                case 2 -> new ASMRegisterExpr("rdx");
                default -> new ASMMemExpr(
                        new ASMBinOpAddExpr(
                                new ASMTempExpr("_ARG0"),
                                new ASMConstExpr(8L*(i-3))));
            };
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

        return null;
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

    private ArrayList<ASMInstruction> visitExpression(IRExpr expr) {
        if (expr instanceof IRBinOp bop)
            return visit(bop);
        else if (expr instanceof IRCallStmt call)
            return visit(call);
        else if (expr instanceof IRConst cnst)
            return visit(cnst);
        else if (expr instanceof IRMem mem)
            return visit(mem);
        else if (expr instanceof IRName name)
            return visit(name);
        else if (expr instanceof IRTemp tmp)
            return visit(tmp);
        else throw new InternalCompilerError("Invalid expression for visitExpression");
    }

    private void replaceTemps(ArrayList<ASMInstruction> instructions, String functionName){
        functionToTemps.get(functionName);
        int index = 1;
        HashMap<String, Integer> tempToStack = new HashMap<>();
        for (String temp: functionToTemps.get(functionName)){
            tempToStack.put(temp,index*8);
            index++;
        }
        for (ASMInstruction instr: instructions){
            instr.createPrint(tempToStack);
            System.out.println(instr);
        }
    }

    // TODO: 4/1/2023
    // move
    // TODO: 4/1/2023
    // temp
    // TODO: 4/1/2023
    // mem
    // TODO: 4/1/2023
    // call_stmt
    // TODO: 4/1/2023
    // name
    // TODO: 4/1/2023
    // RETURN
}
