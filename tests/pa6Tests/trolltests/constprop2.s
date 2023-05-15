    .file  "tests/pa6Tests/trolltests/constprop2.eta"
    .intel_syntax noprefix
    .text
    .globl  _Imain_paai
    .type	_Imain_paai, @function
_Imain_paai:
    enter 0, 0
    mov rdi, 0
    mov r8, 46666670
l2:
    mov rcx, 10
    cmp r8, rcx
    jl l5
    mov rcx, 20
    cmp r8, rcx
    jg l5
    mov rdx, 5
    mov rcx, r8
    imul rcx, rdx
    lea rdi, QWORD PTR [ rdi + rcx ]
l6:
    mov rcx, 1
    sub r8, rcx
    mov rcx, 0
    cmp r8, rcx
    jg l2
    # Add Padding
    call _IunparseInt_aii
    mov rdi, rax
    # Undo Padding
    # Add Padding
    call _Iprintln_pai
    # Undo Padding
    leave 
    ret 
l5:
    sub rdi, r8
    jmp l6

    .data
