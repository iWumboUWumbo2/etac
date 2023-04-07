package aar226_akc55_ayc62_ahl88.asm.visit;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.Expressions.*;
import aar226_akc55_ayc62_ahl88.asm.Instructions.*;
import aar226_akc55_ayc62_ahl88.asm.Instructions.mov.ASMMov;
import aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine.ASMCall;
import aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine.ASMEnter;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.InternalCompilerError;

import java.util.ArrayList;
import java.util.HashMap;

public class RegisterAllocationTrivialVisitor implements ASMVisitor<ArrayList<ASMInstruction>>{


    // push these 3 whenever we enter this function ez
    private final String reg1 = "r12";
    private final String reg2 = "r13";
    private final String reg3 = "r14";
    @Override
    public ArrayList<ASMInstruction> visit(ASMLabel node) {
        return null;
    }

    @Override
    public ArrayList<ASMInstruction> visit(ASMArg0 node) { // leave, ret
        ArrayList<ASMInstruction> res = new ArrayList<>();
        res.add(node);
        return res;
    }

    // CALL SOLO
    // JUMPS, INC, DEC, NOT, IDIV, POP, PUSH,
    @Override
    public ArrayList<ASMInstruction> visit(ASMArg1 node) {
        ArrayList<ASMInstruction> res = new ArrayList<>();
        if (node instanceof ASMCall call){
            throw new InternalCompilerError("call TODO");
        }else{
            ASMExpr argument = node.getLeft();
            if (argument instanceof ASMTempExpr temp){ // migrate temp to stack?
                throw new InternalCompilerError("TEMP TODO");
            }else if (argument instanceof ASMMemExpr mem){ // see if inside mem is temp
                throw new InternalCompilerError("MEM TODO");
            }else{ // no change instruction Jumps
                res.add(node);
            }
        }
        return res;
    }

//    ENTER,MOV,MOVABS,
//    r64, r/m64 ADD,SUB,AND,OR,XOR,SHL,SHR,SAR,TEST,CMP,
    @Override
    public ArrayList<ASMInstruction> visit(ASMArg2 node) {
        ASMExpr left = node.getLeft();
        ASMExpr right = node.getRight();

        ArrayList<ASMInstruction> res = new ArrayList<>();

        ASMOpCodes opCodes = node.getOpCode();
        ASMExpr curDest = null; // replace Left with curDest
        ASMExpr curSrc = null;  // replace Right with curSrc
        if (node instanceof ASMEnter enter){
            throw new InternalCompilerError("enter TODO replace this enter with function One");
        }

        if (left instanceof ASMRegisterExpr reg){// known register Return

        }else{ // Move left into Reg1 cause left is a stack location

        }
        if (right instanceof ASMTempExpr temp){
            // Move temp into reg2 cause temp is a stack location
            // use reg 2 for right
        }else if (right instanceof ASMMemExpr mem){
            // Move the temp in mem (if exist) into reg2 cause temp is a stack location
            // Move the tempSecond in mem (if exist) into reg3 cause temp is a stack location
        }else if (right instanceof ASMConstExpr num){
            // Need an extra move if number is greater or less than max/min int
        }else{ // no idea how to get here
            curSrc = right;
            throw new InternalCompilerError("2 ARG NO IDEA");
        }
        ASMArg2 reBuild = new ASMArg2(opCodes,curDest,curSrc);
        return null;
    }

    // imul dest, v1, v2 // dest = v1 * v2;
    @Override
    public ArrayList<ASMInstruction> visit(ASMArg3 node) {

        return null;
    }
}
