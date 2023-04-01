package aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit;

import aar226_akc55_ayc62_ahl88.asm.*;
import aar226_akc55_ayc62_ahl88.asm.Expressions.*;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg2;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMLabel;
import aar226_akc55_ayc62_ahl88.asm.Instructions.jumps.ASMJumpNotEqual;
import aar226_akc55_ayc62_ahl88.asm.Instructions.mov.ASMMov;
import aar226_akc55_ayc62_ahl88.asm.Instructions.mov.ASMMovabs;
import aar226_akc55_ayc62_ahl88.asm.Instructions.stackops.ASMPush;
import aar226_akc55_ayc62_ahl88.asm.Instructions.tstcmp.ASMTest;
import aar226_akc55_ayc62_ahl88.asm.Instructions.jumps.ASMJumpAlways;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.InternalCompilerError;

import java.util.ArrayList;
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

public class ASMVisitor {
    private int tempCnt;


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
    public ArrayList<ASMInstruction> visit(IRCallStmt node){
        return null;
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
    public ArrayList<ASMInstruction> visit(IRCompUnit node) {
        ArrayList<ASMInstruction> instructions = new ArrayList<ASMInstruction>();

        for (IRData data : node.dataMap().values()) {
            ASMLabel data_label = new ASMLabel(data.name());
//            ASMData data_instr = new ASMData(getType(data.name()), new ASMConstExpr(data.data()));
        }

        for (IRFuncDecl func : node.functions().values()) {
            instructions.addAll(visit(func));
        }

        return instructions;
    }
    public ArrayList<ASMInstruction> visit (IRConst x) {
        ArrayList<ASMInstruction> instructions = new ArrayList<ASMInstruction>();
        ASMArg2 instruction = new ASMMovabs(new ASMTempExpr(nxtTemp()),new ASMConstExpr(x.value()));
        instructions.add(instruction);
        return instructions;
    }
    public ArrayList<ASMInstruction> visit(IRFuncDecl node) {
        ArrayList<ASMInstruction> result = new ArrayList<>();

        // create new Starting label for this Function
        result.add(new ASMLabel(node.name()));

        // push rbp
        // mov rbp rsp
        result.add(new ASMPush(new ASMRegisterExpr("rbp")));
        result.add(new ASMMov(new ASMRegisterExpr("rbp"),new ASMRegisterExpr("rsp")));

        // need to calculate number of temporaries used
        // sub rsp, 8*l
        HashSet<String> asmTempNames = new HashSet<>();


        // foo(1,2,3,4,5,6,7....) -> rdi, rsi, rdx, rcx, r8, r9, stack
        int numParams = node.functionSig.inputTypes.size();
        ArrayList<ASMInstruction> bodyInstructions = new ArrayList<>();
        for (int i = 1; i<=numParams;i++){

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
            String tempName = nxtTemp();
            asmTempNames.add(tempName);
            // can't do [stack location] <- [stack location2]
            // need intermediate rax <- [stack location2]
            // then [stack location] <- temp rax
            if (i>=7){
                bodyInstructions.add(new ASMMov(new ASMRegisterExpr("rax"),ARGI));
                bodyInstructions.add(new ASMMov(new ASMTempExpr(tempName),new ASMRegisterExpr("rax")));
            }
            // just do MOV [stack location] <- register
            else{
                bodyInstructions.add(new ASMMov(new ASMTempExpr(tempName),ARGI));
            }
        }
        return null;
    }
    public ArrayList<ASMInstruction> visit(IRJump jump) {
        ArrayList<ASMInstruction> instructions = new ArrayList<ASMInstruction>();
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
    public ArrayList<ASMInstruction> visit(IRMove node){
        return null;
    }

}
