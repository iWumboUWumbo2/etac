package aar226_akc55_ayc62_ahl88.asm.Instructions;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.visit.RegisterAllocationTrivialVisitor;
import java.util.ArrayList;

/**
 * Abstract class for all instructions.
 */
public abstract class ASMInstruction {
    private ASMOpCodes opCode;

    /**
     * @param op Opcode
     */
    public ASMInstruction(ASMOpCodes op){
        opCode = op;
    }

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
            case SETB -> "setb ";
            case SETG -> "setg ";
            case SETNE -> "setne ";
            case SETL -> "setl ";
            case SETGE -> "setge ";
            case SETLE -> "setle ";
            case CQTO -> "cqto ";
            case SETAE -> "setae ";
            case LEA -> "lea ";
        };
    }

    public ASMOpCodes getOpCode() {
        return opCode;
    }
    public abstract ArrayList<ASMInstruction> accept(RegisterAllocationTrivialVisitor regVisitor);
}
