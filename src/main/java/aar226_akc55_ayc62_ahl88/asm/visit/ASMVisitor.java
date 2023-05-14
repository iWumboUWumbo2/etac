package aar226_akc55_ayc62_ahl88.asm.visit;

import aar226_akc55_ayc62_ahl88.asm.Instructions.*;

/**
 * Interface for ASM visitor
 */
public interface ASMVisitor<T> {
    T visit(ASMComment node);
    T visit(ASMLabel node);
    T visit(ASMArg0 node);
    T visit(ASMArg1 node);
    T visit(ASMArg2 node);
    T visit(ASMArg3 node);
}
