package aar226_akc55_ayc62_ahl88.asm.Opts;

import aar226_akc55_ayc62_ahl88.asm.Expressions.*;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg1;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg2;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMArg3;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;
import aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine.ASMCall;
import aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine.ASMRet;
import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.ir.BackwardIRDataflow;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.InternalCompilerError;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

import static aar226_akc55_ayc62_ahl88.asm.visit.RegisterAllocationTrivialVisitor.checkExprForTemp;
import static aar226_akc55_ayc62_ahl88.asm.visit.RegisterAllocationTrivialVisitor.flattenMem;

public class LiveVariableAnalysisASM extends BackwardBlockASMDataflow<Set<ASMAbstractReg>> {
    public LiveVariableAnalysisASM(CFGGraphBasicBlockASM g) {
        super(g,
                (n,outN) ->{
                    Pair<Set<ASMAbstractReg>,Set<ASMAbstractReg>> res = blockFunc(n);
                    Set<ASMAbstractReg> useSet = res.part1();
                    Set<ASMAbstractReg> defSet = res.part2();

                    Set<ASMAbstractReg> l_temp = new HashSet<>(outN);
                    l_temp.removeAll(defSet);
                    l_temp.addAll(useSet);
                    return l_temp;
                },
                (l1, l2) -> {
                    Set<ASMAbstractReg> l1_temp = new HashSet<>(l1);
                    l1_temp.addAll(l2);
                    return l1_temp;
                },
                HashSet::new,
                new HashSet<>()
        );
    }

    /**
     * Creates Gen[pn] and kill[pn]
     * @param block
     * @return
     */

    // for backwards its gen[ns] = gen[n] U (gen[s] - kill[n]) kill[ns]  = kill[s] U kill[n]
    public static Pair<Set<ASMAbstractReg>,Set<ASMAbstractReg>> blockFunc(BasicBlockASMCFG block){
        Set<ASMAbstractReg> genns = usesInASM(block.getBody().get(block.getBody().size()-1).getStmt());
        Set<ASMAbstractReg> killns = defsInASM(block.getBody().get(block.getBody().size()-1).getStmt());
        for (int i = block.getBody().size()-2;i>=0;i--){
            CFGNode<ASMInstruction> n = block.getBody().get(i);
            genns.removeAll(defsInASM(n.getStmt()));
            genns.addAll(usesInASM(n.getStmt()));
            killns.addAll(defsInASM(n.getStmt()));
        }

        return new Pair<>(genns,killns);

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
//                defSet.add(new ASMRegisterExpr("rbp"));
//                defSet.add(new ASMRegisterExpr("rsp"));
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

        defSet.removeIf(e-> e instanceof ASMNameExpr);
        return defSet;
    }

    public static void flattenAndAdd(ASMMemExpr mem, HashSet<ASMAbstractReg> usedSet) {
        ArrayList<ASMExpr> flat = flattenMem(mem);
        for (ASMExpr e : flat){
            if (e instanceof ASMAbstractReg abs){
                usedSet.add(abs);
            }
        }
    }
    
    public static Set<ASMAbstractReg> usesInASM(ASMInstruction instr) {
        HashSet<ASMAbstractReg> usedSet = new HashSet<>();
        switch(instr.getOpCode()){
            // RDX:RAX:= sign-extend of RAX.
            case CQTO -> {
                usedSet.add(new ASMRegisterExpr("rax"));
            }
            // IDIV r/m64 RAX := Quotient, RDX := Remainder.
            case IDIV -> {
                ASMArg1 arg1 = (ASMArg1) instr;
                usedSet.add(new ASMRegisterExpr("rax"));
                usedSet.add(new ASMRegisterExpr("rdx"));
                if (arg1.getLeft() instanceof ASMAbstractReg temp){
                    usedSet.add(temp);
                }else if (arg1.getLeft() instanceof ASMMemExpr mem){
                    flattenAndAdd(mem, usedSet);
                }else{
                    throw new InternalCompilerError("not mem or temp" + arg1);
                }
            }
            // INC/DEC r/m64
            // NOT r/m64
            // PUSH r/m64
            // POP r64
            case INC,DEC,NOT,POP, PUSH-> { // INC r/m64 // NOT r/m64
                ASMArg1 arg1 = (ASMArg1) instr;
                if (arg1.getLeft() instanceof ASMAbstractReg temp){
                    usedSet.add(temp);
                }else if (arg1.getLeft() instanceof ASMMemExpr mem){
                    flattenAndAdd(mem, usedSet);
                }else{
                    throw new InternalCompilerError("not mem or temp" + arg1);
                }
            }

            // SETCC r/m8
            // REG "AL" -> "RAX" Might change AL since RAX used too much
            // doesn't use anything but CC
            case SETE, SETNE, SETL, SETLE, SETG, SETGE, SETB, SETAE -> {
            }

            //push rbp
            //mov rbp, rsp
            //sub rsp, 8*l
            case ENTER,LEAVE -> {
            }
            case MOV, MOVABS -> {
                ASMArg2 arg2 = (ASMArg2) instr;
                if (arg2.getRight() instanceof ASMAbstractReg abs){
                    usedSet.add(abs);
                }else if (arg2.getRight() instanceof ASMMemExpr mem){
                    flattenAndAdd(mem, usedSet);
                }else{
                    if (!(arg2.getRight() instanceof ASMConstExpr)){
                        throw new InternalCompilerError("not mem or temp" + arg2);
                    }
                }
            }
//            case MOVABS -> {
//                ASMArg2 arg2 = (ASMArg2) instr;
//                if (arg2.getRight() instanceof ASMAbstractReg abs){
//                    usedSet.add(abs);
//                }else if (arg2.getRight() instanceof ASMMemExpr mem){
//                    ArrayList<ASMExpr> flat = flattenMem(mem);
//                    for (ASMExpr e : flat){
//                        if (e instanceof ASMAbstractReg abs){
//                            usedSet.add(abs);
//                        }
//                    }
//                }else{
//                    if (arg2.getRight() instanceof ASMConstExpr ){
//
//                    }else {
//                        throw new InternalCompilerError("not mem or temp" + arg2);
//                    }
//                }
//            }
            // AND r/m64
            case ADD, SUB, AND, OR, XOR,TEST,CMP -> {
                ASMArg2 arg2 = (ASMArg2) instr;

                if (arg2.getLeft() instanceof ASMAbstractReg abs){
                    usedSet.add(abs);
                }else if (arg2.getLeft() instanceof ASMMemExpr mem){
                    flattenAndAdd(mem, usedSet);
                }else{
                    throw new InternalCompilerError("not mem or temp" + arg2);
                }
                if (arg2.getRight() instanceof ASMAbstractReg abs){
                    usedSet.add(abs);
                }else if (arg2.getRight() instanceof ASMMemExpr mem){
                    flattenAndAdd(mem, usedSet);
                }else{
                    if (!(arg2.getRight() instanceof ASMConstExpr)) {
                        throw new InternalCompilerError("not mem temp or const" + arg2);
                    }
                }
            }
            case IMUL -> {
                ASMArg3 arg3 = (ASMArg3) instr;
                //IMUL r/m64 RDX:RAX := RAX * r/m64.
                if (arg3.getA2() == null && arg3.getA3() == null){ //1
                    usedSet.add(new ASMRegisterExpr("rax"));
                    //IMUL r64, r/m64
                }else if (arg3.getA3() == null){ //2
                    if (arg3.getA1() instanceof ASMAbstractReg abs){
                        usedSet.add(abs);
                    }else if (arg3.getA1() instanceof ASMMemExpr mem){
                        flattenAndAdd(mem, usedSet);
                    }else{
                        throw new InternalCompilerError("not mem or temp" + arg3);
                    }
                    if (arg3.getA2() instanceof ASMAbstractReg abs){
                        usedSet.add(abs);
                    }else if (arg3.getA2() instanceof ASMMemExpr mem){
                        flattenAndAdd(mem, usedSet);
                    }else{
                        throw new InternalCompilerError("not mem or temp" + arg3);
                    }
                }else{
                    throw new InternalCompilerError("all three non null");
                }
            }
            case SHL, SHR, SAR -> {
                ASMArg2 arg2 = (ASMArg2) instr;
                if (arg2.getLeft() instanceof ASMAbstractReg abs){
                    usedSet.add(abs);
                }else if (arg2.getLeft() instanceof ASMMemExpr mem){
                    flattenAndAdd(mem, usedSet);
                }else{
                    throw new InternalCompilerError("not mem or temp Shifts" + arg2);
                }
            }
            // LEA r64,m
            case LEA -> {
                ASMArg2 arg2 = (ASMArg2) instr;
                if (arg2.getRight() instanceof ASMMemExpr mem){
                    flattenAndAdd(mem, usedSet);
                }else{
                    throw new InternalCompilerError("not mem or temp" + arg2);
                }
            }
            // Always Name
            case JMP,JB, JE, JNE, JL, JLE, JG, JGE -> {
            }

            // caller saved rax, rcx, rdx, rsi, rdi, and r8–r11
            case CALL -> {
                ASMCall call = (ASMCall)  instr;
                ArrayList<String> params = new ArrayList<>(List.of(
                        "rdi", "rsi", "rdx", "rcx", "r8", "r9"));

                long numParams = call.numReturns  > 2 ?
                        call.numParams + 1 : call.numParams;
                long subSet = Math.min(6,numParams);
                for (int i = 0; i< subSet;i++){
                    usedSet.add(new ASMRegisterExpr(params.get(i)));
                }
            }
            case RET -> {
                ASMRet ret = (ASMRet) instr;
                if (ret.rets >= 2){
                    usedSet.add(new ASMRegisterExpr("rdx"));
                    usedSet.add(new ASMRegisterExpr("rax"));
                }else if (ret.rets == 1){
                    usedSet.add(new ASMRegisterExpr("rax"));
                }
            }
            case LABEL, COMMENT -> {
            }
        }
        usedSet.removeIf(e-> e instanceof ASMNameExpr);
        return usedSet;
    }

}
