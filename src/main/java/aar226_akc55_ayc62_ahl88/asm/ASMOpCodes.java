package aar226_akc55_ayc62_ahl88.asm;

/**
 * Contains all ASM opcodes.
 */
public enum ASMOpCodes{
        // 0 arg
        RET, // when you finish executing function do ret.
        LEAVE,
        CQTO,

        // 1 arg
        JB, // Jump Below unsigned
        JMP, // jump always
        JE, // jump if equal a == b
        JNE, // jump if not equal a != b
        JL, // jump if less than a < b
        JLE, // jump if less than equal a <= b
        JG, //jump if greater a > b
        JGE, // jump if greater equal a >= b
        CALL, // call it when you call a function call stmt
        INC, // increment single operand
        DEC, // decrement single operand
        NOT, //inverts bits, not this -> !
        IDIV, // (rdx.rax) / divisor, stores quotient in rax, stores remainder in rdx
        POP,
        PUSH,
        SETE, //sets its argument to 1 if the zero flag is set or to 0 otherwise.
        SETNE,
        SETL,
        SETLE,
        SETG,
        SETGE,
        SETB,
        SETAE,


        // 2 arg
        ENTER,
        MOV, //MOOOOOVE data from src to dest
        MOVABS,
        ADD,
        SUB,
        IMUL, // Can be 3 args
        AND, //bitwise and
        OR, //bitwise or
        XOR, //bitwise xor
        SHL, //shift left
        SHR, // shift right
        SAR, // shift arithmetic right, preserve sign bit
        TEST,
        CMP,
        LEA,

        // EXTRA
        LABEL,
        COMMENT
}

