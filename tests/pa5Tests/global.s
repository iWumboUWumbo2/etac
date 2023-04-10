    .file  "tests/pa5Tests/global.eta"
    .intel_syntax noprefix
    .text
    .globl  _Imain_paai
    .type	_Imain_paai, @function
_a:
    .quad 5
_b:
    .quad 24
_Imain_paai:
    enter 48, 0
    push r12
    push r13
    push r14
    mov r14, QWORD PTR [ rbp - 8 ]
    mov r14, rdi
    mov QWORD PTR [ rbp - 8 ], r14
    mov r14, QWORD PTR [ rbp - 32 ]
    mov r14, null
    mov QWORD PTR [ rbp - 32 ], r14
    sub rsp, 8
    mov r14, QWORD PTR [ rbp - 32 ]
    mov rdi, r14
    call _IunparseInt_aii
    mov r14, QWORD PTR [ rbp - 16 ]
    mov r14, rax
    mov QWORD PTR [ rbp - 16 ], r14
    sub rsp, 8
    mov r14, QWORD PTR [ rbp - 24 ]
    mov r13, QWORD PTR [ rbp - 16 ]
    mov r14, r13
    mov QWORD PTR [ rbp - 24 ], r14
    sub rsp, 8
    mov r14, QWORD PTR [ rbp - 24 ]
    mov rdi, r14
    call _Iprintln_pai
    mov r14, QWORD PTR [ rbp - 16 ]
    mov r14, rax
    mov QWORD PTR [ rbp - 16 ], r14
    sub rsp, 8
    mov r14, QWORD PTR [ rbp - 48 ]
    mov r14, null
    mov QWORD PTR [ rbp - 48 ], r14
    sub rsp, 8
    mov r14, QWORD PTR [ rbp - 48 ]
    mov rdi, r14
    call _IunparseInt_aii
    mov r14, QWORD PTR [ rbp - 16 ]
    mov r14, rax
    mov QWORD PTR [ rbp - 16 ], r14
    sub rsp, 8
    mov r14, QWORD PTR [ rbp - 40 ]
    mov r13, QWORD PTR [ rbp - 16 ]
    mov r14, r13
    mov QWORD PTR [ rbp - 40 ], r14
    sub rsp, 8
    mov r14, QWORD PTR [ rbp - 40 ]
    mov rdi, r14
    call _Iprintln_pai
    mov r14, QWORD PTR [ rbp - 16 ]
    mov r14, rax
    mov QWORD PTR [ rbp - 16 ], r14
    sub rsp, 8
    pop r14
    pop r13
    pop r12
    leave 
    ret 
