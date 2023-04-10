package aar226_akc55_ayc62_ahl88.asm.Instructions;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.Expressions.*;
import aar226_akc55_ayc62_ahl88.asm.visit.RegisterAllocationTrivialVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public abstract class ASMInstruction {
    private ASMOpCodes opCode;


    public ASMInstruction(ASMOpCodes op){
        opCode = op;
    }

    public abstract void createPrint(HashMap<String, Integer> location);

    public String opCodeToString(){
        return switch (opCode){
            case RET -> "ret ";
            case LEAVE -> "leave ";
            case JMP -> "jmp ";
            case JE -> "je ";
            case JNE -> "jne ";
            case JL -> "jl ";
            case JLE -> "jle ";
            case JG -> "jg ";
            case JGE -> "jge ";
            case JR -> "jr ";
            case CALL -> "call ";
            case LABEL -> "";
            case INC -> "inc ";
            case DEC -> "dec ";
            case NOT -> "not ";
            case IDIV -> "idiv ";
            case POP -> "pop ";
            case PUSH -> "push ";
            case ENTER -> "enter ";
            case MOV -> "mov ";
            case MOVABS -> "movabs ";
            case ADD -> "add ";
            case SUB -> "sub ";
            case IMUL -> "imul ";
            case AND -> "and ";
            case OR -> "or ";
            case XOR -> "xor ";
            case SHL -> "shl ";
            case SHR -> "shr ";
            case SAR -> "sar ";
            case TEST -> "test ";
            case CMP -> "cmp ";
            case COMMENT -> "";
            case SETE -> "sete ";
            case JB -> "jb ";
        };
    }
    public String exprASMToString(ASMExpr expr, HashMap<String, Integer> location){
        if (expr instanceof ASMTempExpr temp){
//            System.out.println(temp.getName());
//            System.out.println(location.get(temp.getName()));
            return "QWORD PTR [rbp - " + location.get(temp.getName()) + "]";
        }else if (expr instanceof ASMRegisterExpr reg){
            return reg.getRegisterName();
        }else if (expr instanceof ASMConstExpr cons){
            return Long.toString(cons.getValue());
        }else{
            throw new Error("TODO");
        }
    }

    public ASMOpCodes getOpCode() {
        return opCode;
    }
    public abstract ArrayList<ASMInstruction> accept(RegisterAllocationTrivialVisitor regVisitor);
}
