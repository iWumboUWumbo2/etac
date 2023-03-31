package aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit;

import aar226_akc55_ayc62_ahl88.asm.*;
import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.jumps.JumpAlways;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;

import java.util.ArrayList;

/**
 * NOTES:
 * - branches: use cmp then corresponding jump
 * - multireturn: store first argument in rax, push rest on stack
 * - function args: rdi, rsi, rdx, rcx, r8, r9, push rest on stack
 * - callee saved: rbx, rbp, and r12–r15
 * - caller saved: everything else
 *
 * THINGS WE NEEEEEEEEEEEDDDDDDDDD
 * - ir compunit ->
 *      add glob mem locations
 *      call visit to all functions
 * - ir func decl ->
 *      create new label at the top for function name
 *      follow abi for args and register alloc
 *      call visit on each statement
 *      flatten arraylist we get back
 *      follow abi for correct func exit
 * - jump -> jr <label name>
 * - cjump ->
 *      two args: cmp <t1> <t2>; j<flag> <label name>
 *      if instance of IRConst and is boolliteral: true -> jr / false -> nothing
 * - move ->
 *      mooooooooooooove
 *      contains expr: used for dynamic tiling
 *      tiling
 * - mem
 * - binop
 * - call stmt ->
 *      init function call follow abi for function call (store func args correctly)
 *      jump to function
 *      clean accroding to ABI spec (be careful about multireturns and stack dead space)
 *      pop from function
 * - IR Label -> ASM label
 *
 *
 * Generic ASM Class will be called ASMNode
 */

public class ASMVisitor {

    public ArrayList<ASMInstruction> visit(IRLabel label) {
        ArrayList<ASMInstruction> instructions = new ArrayList<ASMInstruction>();
        instructions.add(new ASMLabel(label.name()));
        return instructions;
    }

    public ArrayList<ASMInstruction> visit(IRJump jump) {
        ArrayList<ASMInstruction> instructions = new ArrayList<ASMInstruction>();
        if (jump.target() instanceof IRName) {
            instructions.add(new JumpAlways(new ASMName(jump.label())));
        }
        return instructions;
    }

    public ArrayList<ASMInstruction> visit(IRCompUnit compunit) {
        ArrayList<ASMInstruction> instructions = new ArrayList<ASMInstruction>();

        for (IRData data : compunit.dataMap().values()) {
            ASMLabel data_label = new ASMLabel(data.name());
            ASMData data_instr = new ASMData(getType(data.name()), new ASMConst(data.data()));
        }

        for (IRFuncDecl func : compunit.functions().values()) {
            instructions.addAll(visit(func));
        }

        return instructions;
    }

    public ArrayList<ASMInstruction> visit(IRFuncDecl func_decl) {
        return null;
    }

    public ArrayList<ASMInstruction> visit(IRCJump cjump) {
        ArrayList<ASMInstruction> instructions = new ArrayList<ASMInstruction>();

        IRExpr condition = cjump.cond();

        if (condition instanceof IRBinOp c) {
            return null;
        } else if (condition instanceof IRConst c) {
            if (c.value() != 0L){ // jump
                instructions.add(new JumpAlways(new ASMName(cjump.trueLabel())));
            }
        } else if (condition instanceof IRTemp c) {
            ASMTemp tempName = tempToASM(c);
            //test t, t
            //jnz l

        } else if (condition instanceof IRMem c) {
            return null;
        }
        return instructions;
    }



    //
    //
    //
    //
    private ASMDirectives getType(String name) {
        String type = name.split("_")[0];
        if (type.equals("i") || type.equals("b")) {
            return ASMDirectives.QUAD;
        }
        return ASMDirectives.ZERO;
    }

    // converts an IR TEMP to an ASM TEMP
    private ASMTemp tempToASM(IRTemp t) {
        return new ASMTemp(t.name());
    }

}
