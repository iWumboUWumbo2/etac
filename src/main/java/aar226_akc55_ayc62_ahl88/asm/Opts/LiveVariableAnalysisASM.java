package aar226_akc55_ayc62_ahl88.asm.Opts;

import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMAbstractReg;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMRegisterExpr;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg1;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg2;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg3;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;
import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.ir.BackwardIRDataflow;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.InternalCompilerError;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

import static aar226_akc55_ayc62_ahl88.asm.visit.RegisterAllocationTrivialVisitor.checkExprForTemp;

public class LiveVariableAnalysisASM extends BackwardIRDataflow<Set<ASMAbstractReg>> {

    public LiveVariableAnalysisASM(CFGGraph<IRStmt> g, BiFunction<CFGNode<IRStmt>, Set<ASMAbstractReg>, Set<ASMAbstractReg>> transfer, BinaryOperator<Set<ASMAbstractReg>> meet, Supplier<Set<ASMAbstractReg>> acc, Set<ASMAbstractReg> topElement) {
        super(g, transfer, meet, acc, topElement);
    }

    public static Set<ASMAbstractReg> usesInASM(ASMInstruction instr){
        HashSet<ASMAbstractReg> useSet = new HashSet<>();

        return useSet;
    }
    public static Set<ASMAbstractReg> defsInASM(ASMInstruction instr) {
        HashSet<ASMAbstractReg> defSet = new HashSet<>();
        switch(instr.getOpCode()){
            // RDX:RAX:= sign-extend of RAX.
            case CQTO -> {
                defSet.add(new ASMRegisterExpr("rdx"));
                defSet.add(new ASMRegisterExpr("rax"));
            }
            // INC/DEC r/m64
            // NOT r/m64
            case INC,DEC,NOT -> { // INC r/m64 // NOT r/m64
                ASMArg1 arg1 = (ASMArg1) instr;
                if (arg1.getLeft() instanceof ASMAbstractReg temp){
                    defSet.add(temp);
                }
            }
            // IDIV r/m64 RAX := Quotient, RDX := Remainder.
            case IDIV -> {
                defSet.add(new ASMRegisterExpr("rax"));
                defSet.add(new ASMRegisterExpr("rdx"));
            }

            // PUSH r/m64
            // POP r64
            case POP, PUSH -> {
                ASMArg1 arg1 = (ASMArg1) instr;
                if (arg1.getLeft() instanceof ASMAbstractReg temp){
                    defSet.add(temp);
                }
                defSet.add(new ASMRegisterExpr("rsp"));
                // TODO RSP????
            }
            // SETCC r/m8
            // REG "AL" -> "RAX" Might change AL since RAX used too much
            case SETE, SETNE, SETL, SETLE, SETG, SETGE, SETB, SETAE -> {
                ASMArg1 arg1 = (ASMArg1) instr;
                if (arg1.getLeft() instanceof ASMRegisterExpr reg && reg.getRegisterName().equals("al")){
                    defSet.add(new ASMRegisterExpr("rax"));
                }else{
                    throw new InternalCompilerError("SETCC shouldn't have temp" + arg1);
                }
            }
            //push rbp
            //mov rbp, rsp
            //sub rsp, 8*l
            case ENTER,LEAVE -> {
                defSet.add(new ASMRegisterExpr("rbp"));
                defSet.add(new ASMRegisterExpr("rsp"));
            }
            case MOV -> {
                ASMArg2 arg2 = (ASMArg2) instr;
                if (arg2.getLeft() instanceof ASMAbstractReg abs){
                    defSet.add(abs);
                }
            }
            case MOVABS -> {
                ASMArg2 arg2 = (ASMArg2) instr;
                if (arg2.getLeft() instanceof ASMAbstractReg abs){
                    defSet.add(abs);
                }else{
                    throw new InternalCompilerError("MOVABS not reg: " + instr);
                }
            }
            // AND r/m64
            case ADD, SUB, AND, OR, XOR -> {
                ASMArg2 arg2 = (ASMArg2) instr;
                if (arg2.getLeft() instanceof ASMAbstractReg abs){
                    defSet.add(abs);
                }
            }
            case IMUL -> {
                ASMArg3 arg3 = (ASMArg3) instr;
                //IMUL r/m64 RDX:RAX := RAX * r/m64.
                if (arg3.getA2() == null && arg3.getA3() == null){ //1
                    defSet.add(new ASMRegisterExpr("rdx"));
                    defSet.add(new ASMRegisterExpr("rax"));
               //IMUL r64, r/m64
                }else if (arg3.getA3() == null){ //2
                    if (arg3.getA1() instanceof ASMAbstractReg abs){
                        defSet.add(abs);
                    }else{
                        throw new InternalCompilerError("left must be reg: " + instr);
                    }
                }else{
                    throw new InternalCompilerError("all three non null");
                }
            }
            case SHL, SHR, SAR -> {
                ASMArg2 arg2 = (ASMArg2) instr;
                if (arg2.getLeft() instanceof ASMAbstractReg abs){
                    defSet.add(abs);
                }
            }
            // r/m64, r64
            case TEST -> {
                ASMArg2 arg2 = (ASMArg2) instr;
                if (arg2.getLeft() instanceof ASMAbstractReg abs){
                    defSet.add(abs);
                }
            }
            // LEA r64,m
            case LEA -> {
                ASMArg2 arg2 = (ASMArg2) instr;
                if (arg2.getLeft() instanceof ASMAbstractReg abs){
                    defSet.add(abs);
                }else{
                    throw new InternalCompilerError("Left can't be Mem");
                }
            }
            // Always Name
            case JMP,JB, JE, JNE, JL, JLE, JG, JGE -> {
                System.out.println("jump no defs");
            }

            // caller saved rax, rcx, rdx, rsi, rdi, and r8–r11
            case CALL -> {
                defSet.add(new ASMRegisterExpr("rax"));
                defSet.add(new ASMRegisterExpr("rdx"));
                defSet.add(new ASMRegisterExpr("rcx"));
                defSet.add(new ASMRegisterExpr("rdi"));
                defSet.add(new ASMRegisterExpr("rsi"));
                defSet.add(new ASMRegisterExpr("r8"));
                defSet.add(new ASMRegisterExpr("r9"));
                defSet.add(new ASMRegisterExpr("r10"));
                defSet.add(new ASMRegisterExpr("r11"));
            }
            case LABEL, COMMENT,CMP,RET -> {
            }
        }

        return defSet;
    }

}
