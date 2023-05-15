    .file  "tests/pa6Tests/trolltests/constprop3.eta"
    .intel_syntax noprefix
    .text
    .globl  _Imain_paai
    .type	_Imain_paai, @function
_Imain_paai:
    enter 0, 0
    jmp l9
l10:
    # Add Padding
    mov rdi, 40
    call _eta_alloc
    # Undo Padding
    mov QWORD PTR [ rax ], 4
    mov QWORD PTR [ rax + 8 ], 68
    mov QWORD PTR [ rax + 16 ], 111
    mov QWORD PTR [ rax + 24 ], 110
    mov QWORD PTR [ rax + 32 ], 101
    lea rdi, QWORD PTR [ rax + 8 ]
    # Add Padding
    call _Iprintln_pai
    # Undo Padding
    leave 
    ret 
l9:
    mov rcx, 0
l6:
    lea rcx, QWORD PTR [ rcx + 1 ]
    movabs r8, 4666666670
    cmp rcx, r8
    jl l6
    jmp l10

    .data
