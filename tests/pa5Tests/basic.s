    .file  "tests/pa5Tests/basic.eta"
    .intel_syntax noprefix
    .text
    .globl  _Imain_paai
    .type	_Imain_paai, @function
_Imain_paai:
    enter 16, 0
    mov QWORD PTR [rbp - 16], rdi
    mov rax, QWORD PTR [rbp - 16]
    mov QWORD PTR [rbp - 8], rax
    leave 
    ret 
_Isquare_ii:
    enter 24, 0
    mov QWORD PTR [rbp - 8], rdi
    mov rax, QWORD PTR [rbp - 8]
    mov QWORD PTR [rbp - 16], rax
    mov rax, QWORD PTR [rbp - 16]
    mov QWORD PTR [rbp - 24], rax
    mov rax, QWORD PTR [rbp - 24]
    leave 
    ret 
