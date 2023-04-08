package aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;

import java.util.ArrayList;

public class ASMTiling {

    private ArrayList<ASMInstruction> tileExpression(IRExpr expr) {
        if (expr instanceof IRMem mem) {
            return tileMemExpression(mem);
        }

        return null;
    }

    private ArrayList<ASMInstruction> tileMemExpression(IRMem mem) {
        IRExpr expr = mem.expr();

        if (expr instanceof IRBinOp bop) {
            return tileMemBinOp(bop);
        } else if (expr instanceof IRConst cnst) {
            
        } else if (expr instanceof IRConst cnst) {

        }
        return null;
    }

    private ArrayList<ASMInstruction> tileMemBinOp(IRBinOp bop) {
        switch (bop.opType()) {
            case ADD -> {
                return tileMemAddExpression(bop);
            }
            case MUL -> {
                return tileMemMulExpression(bop);
            }
            default -> {
                return null;
            }
        }
    }

    private ArrayList<ASMInstruction> tileMemAddExpression(IRBinOp bop) {
        ArrayList<IRExpr> flatAdd = bop.flatten(IRBinOp.OpType.ADD);

        return null;
    }

    private ArrayList<ASMInstruction> tileMemMulExpression(IRBinOp bop) {
        IRExpr left = bop.left(), right = bop.right();

        return null;
    }

    private boolean isValidScale(IRConst scale) {
        long n = scale.value() & 0x0F;
        return (n != 0) && ((n & (n - 1)) == 0);
    }
}
