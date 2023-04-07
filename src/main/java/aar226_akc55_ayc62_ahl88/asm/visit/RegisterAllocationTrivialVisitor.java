package aar226_akc55_ayc62_ahl88.asm.visit;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.Expressions.*;
import aar226_akc55_ayc62_ahl88.asm.Instructions.*;
import aar226_akc55_ayc62_ahl88.asm.Instructions.mov.ASMMov;
import aar226_akc55_ayc62_ahl88.asm.Instructions.mov.ASMMovabs;
import aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine.ASMCall;
import aar226_akc55_ayc62_ahl88.asm.Instructions.subroutine.ASMEnter;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.InternalCompilerError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class RegisterAllocationTrivialVisitor implements ASMVisitor<ArrayList<ASMInstruction>>{


    // push these 3 whenever we enter this function ez

    HashMap<String, HashSet<String>> functionToTemps = new HashMap<>();
    HashMap<String,HashMap<String,Long>> functionToTempsToStackOffset = new HashMap<>();

    String currentFunction;
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
        ArrayList<String> availReg = new ArrayList<>(Arrays.asList("rax", "rcx","rdx"));
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
            curDest = left;
            availReg.remove(reg.getRegisterName());
        }else if (left instanceof ASMTempExpr temp){ // Move left into Reg1 cause left is a stack location
            // get the stack location through mapping
            String curReg = availReg.get(availReg.size()-1);
            availReg.remove(availReg.size()-1);
            // add the move
            ASMMemExpr stackLoc = tempToStack(temp);
            ASMRegisterExpr usedReg = new ASMRegisterExpr(curReg);
            res.add(new ASMMov(usedReg,stackLoc));
            curDest = usedReg;
        }else if (left instanceof ASMMemExpr mem){
            // find the memory locations of the temps inside of mem pass in current avail Regs
            ArrayList<ASMExpr> expressions = flattenMem(mem);
            HashMap<String, String> tempToReg= new HashMap<>();
            for (ASMExpr expr: expressions){
                if (expr instanceof ASMTempExpr temp){
                    // get stack mapping for reg
                    String curReg = availReg.get(availReg.size()-1);
                    availReg.remove(availReg.size()-1);
                    // add the move
                    ASMMemExpr stackLoc = tempToStack(temp);
                    ASMRegisterExpr usedReg = new ASMRegisterExpr(curReg);
                    res.add(new ASMMov(usedReg,stackLoc));
                    tempToReg.put(temp.getName(),curReg);
                }
            }
            curDest = tempsToRegs(mem,tempToReg);
        }else{
            throw new InternalCompilerError("nothing else should be left on the top level");
        }

        // need intermediate steps cause both can't be mem
        // need extra reg

        if (!(left instanceof ASMRegisterExpr)){ // left is mem
            if (right instanceof ASMTempExpr temp) {
                // Move temp into reg2 cause temp is a stack location
                // use reg 2 for right
            } else if (right instanceof ASMMemExpr mem) {
                // Move the temp in mem (if exist) into reg2 cause temp is a stack location
                // Move the tempSecond in mem (if exist) into reg3 cause temp is a stack location
            } else if (right instanceof ASMConstExpr num) {
                // Need an extra move if number is greater or less than max/min int
            } else { // ASM Register
                curSrc = right;
            }
        }else{ // left is reg
            if (right instanceof ASMTempExpr temp) {
                // Move temp into reg2 cause temp is a stack location
                // use reg 2 for right
            } else if (right instanceof ASMMemExpr mem) {
                // Move the temp in mem (if exist) into reg2 cause temp is a stack location
                // Move the tempSecond in mem (if exist) into reg3 cause temp is a stack location
            } else if (right instanceof ASMConstExpr num) {
                // Need an extra move if number is greater or less than max/min int
            } else { // ASM Register
                curSrc = right;
            }
        }
        ASMArg2 reBuild = new ASMArg2(opCodes, curDest, curSrc);
        return null;
    }

    // imul dest, v1, v2 // dest = v1 * v2;
    @Override
    public ArrayList<ASMInstruction> visit(ASMArg3 node) {

        return null;
    }

    /**
     * Creates a new Instruction Enter for the number of locations on the stack needed.
     * @param instructions
     * @return
     */
    private ASMEnter createEnterAndBuildMapping(ArrayList<ASMInstruction> instructions){

        HashSet<String> temps = functionToTemps.get(currentFunction);
        for (ASMInstruction instr: instructions){
            if (instr instanceof ASMArg1 arg1){
                checkExprForTemp(arg1.getLeft(),temps);
            }else if (instr instanceof  ASMArg2 arg2){
                checkExprForTemp(arg2.getLeft(),temps);
                checkExprForTemp(arg2.getRight(),temps);
            }else if (instr instanceof ASMArg3 arg3){
                checkExprForTemp(arg3.getA1(),temps);
                checkExprForTemp(arg3.getA2(),temps);
                checkExprForTemp(arg3.getA3(),temps);
            }
        }
        long index = 1;
        HashMap<String,Long> tempsToStack =  functionToTempsToStackOffset.get(currentFunction);
        for (String temp: temps){
            tempsToStack.put(temp,-8L*index);
            index++;
        }
        return new ASMEnter(new ASMConstExpr(8L*temps.size()), new ASMConstExpr(0));
    }


    private void checkExprForTemp(ASMExpr expr, HashSet<String> temps){
        if (expr == null){
            return;
        }
        if (expr instanceof ASMTempExpr temp){
            temps.add(temp.getName());
        }else if (expr instanceof ASMMemExpr mem){
            ArrayList<ASMExpr> res = flattenMem(mem);
            for (ASMExpr ex: res){
                if (ex instanceof ASMTempExpr temp){
                    temps.add(temp.getName());
                }
            }
        }else if (expr instanceof ASMBinOpExpr binop){
            ArrayList<ASMExpr> res = flattenBinop(binop);
            for (ASMExpr ex: res){
                if (ex instanceof ASMTempExpr temp){
                    temps.add(temp.getName());
                }
            }
        }

    }


    /**
     * Returns a list of all the ASM expressions inside the memory operand.
     * @param mem
     * @return
     */
    private ArrayList<ASMExpr> flattenMem(ASMMemExpr mem){
        ArrayList<ASMExpr> res = new ArrayList<>();
        if (mem.getMem() instanceof ASMBinOpExpr binop){
            res.addAll(flattenBinop(binop));
        }else{
            res.add(mem);
        }
        return res;
    }
    /**
     * Returns a list of all the ASM expressions inside the binop in a list
     * @param binop
     * @return
     */
    private ArrayList<ASMExpr> flattenBinop(ASMBinOpExpr binop){
        ArrayList<ASMExpr> res = new ArrayList<>();
        if (binop.getLeft() instanceof ASMBinOpExpr leftBinop){
            res.addAll(flattenBinop(leftBinop));
        }else{
            res.add(binop.getLeft());
        }
        if (binop.getRight() instanceof ASMBinOpExpr rightBinop){
            res.addAll(flattenBinop(rightBinop));
        }else{
            res.add(binop.getRight());
        }
        return res;
    }
    /**
     * Maps the Temporary to the corresponding stack location
     * @param temp
     * @return the stack location of the temporary
     */
    private ASMMemExpr tempToStack(ASMTempExpr temp){
        long index = functionToTempsToStackOffset.get(currentFunction).get(temp.getName());

        return new ASMMemExpr(new ASMBinOpAddExpr(
                new ASMRegisterExpr("rbp"),
                new ASMConstExpr(index)));
    }

    /**
     * Checks if the immediate is too big and will insert an extra move and a register to
     * make sure instruction sequence is valid.
     * @param cons the constant we are checking
     * @param instructions the current instructions that are in the arrayList
     * @param availRegs up to the three registers we can use for this instruction
     * @return
     */
    private ASMExpr isIMMTooBig(ASMConstExpr cons, ArrayList<ASMInstruction> instructions, ArrayList<String> availRegs){

        boolean isInt = cons.getValue() <= Integer.MAX_VALUE && cons.getValue() >= Integer.MIN_VALUE;
        if (!isInt){
            String usedReg = availRegs.get(availRegs.size()-1);
            availRegs.remove(availRegs.size()-1);
            ASMRegisterExpr reg = new ASMRegisterExpr(usedReg);
            ASMMovabs extraMove = new ASMMovabs(reg,cons);
            instructions.add(extraMove);
            return reg;
        }else{
            return cons;
        }
        // returns either a reg or the imm
    }

    /**
     * Replace the temporaries found in memory with real registers assuming already mapped
     * @param expr
     * @param tempMapping
     * @return
     */
    private ASMExpr tempsToRegs(ASMExpr expr, HashMap<String,String> tempMapping){
        if (expr instanceof ASMTempExpr temp){ // base case
            return new ASMRegisterExpr(tempMapping.get(temp.getName()));
        }else if (expr instanceof ASMMemExpr mem){
            return new ASMMemExpr(tempsToRegs(mem.getMem(),tempMapping));
        }else if (expr instanceof ASMBinOpMultExpr binopMult){
            return new ASMBinOpMultExpr(tempsToRegs(binopMult.getLeft(),tempMapping),
                    tempsToRegs(binopMult.getRight(),tempMapping));
        }else if (expr instanceof ASMBinOpAddExpr binopAdd){
            return new ASMBinOpAddExpr(tempsToRegs(binopAdd.getLeft(),tempMapping),
                    tempsToRegs(binopAdd.getRight(),tempMapping));
        }else{ // other base case
            return expr;
        }
    }

}
