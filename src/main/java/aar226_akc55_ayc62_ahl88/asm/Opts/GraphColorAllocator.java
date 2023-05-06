package aar226_akc55_ayc62_ahl88.asm.Opts;

import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMAbstractReg;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMRegisterExpr;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMTempExpr;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;
import aar226_akc55_ayc62_ahl88.asm.Instructions.mov.ASMAbstractMov;
import aar226_akc55_ayc62_ahl88.asm.Instructions.mov.ASMMov;
import aar226_akc55_ayc62_ahl88.asm.Instructions.mov.ASMMovabs;
import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;

import java.util.*;

import static aar226_akc55_ayc62_ahl88.asm.Opts.LiveVariableAnalysisASM.defsInASM;
import static aar226_akc55_ayc62_ahl88.asm.Opts.LiveVariableAnalysisASM.usesInASM;

public class GraphColorAllocator {

    CFGGraphBasicBlockASM prog;
    LiveVariableAnalysisASM LVA;

    ArrayDeque<ASMRegisterExpr> precolored;
    ArrayDeque<ASMTempExpr> initial;
    ArrayDeque<ASMAbstractReg> simplifyWorklist;
    ArrayDeque<ASMAbstractReg> freezeWorklist;
    ArrayDeque<ASMAbstractReg> spillWorklist;
    ArrayDeque<ASMAbstractReg> spilledNodes;
    ArrayDeque<ASMAbstractReg> coalescedNodes;
    ArrayDeque<ASMAbstractReg> coloredNodes;
    Stack<ASMAbstractReg> selectStack;
    ArrayDeque<ASMAbstractMov> coalescedMoves;
    ArrayDeque<ASMAbstractMov> constrainedMoves;
    ArrayDeque<ASMAbstractMov> frozenMoves;
    ArrayDeque<ASMAbstractMov> worklistMoves;
    ArrayDeque<ASMAbstractMov> activeMoves;
    HashMap<ASMAbstractReg,
            ASMAbstractReg> adjSet;
    HashMap<ASMAbstractReg,
            ArrayList<ASMAbstractReg>> adjList;

    HashMap<ASMAbstractReg, Integer> degree;

    HashMap<ASMAbstractReg,HashSet<CFGNode<ASMInstruction>>> moveList;

    HashMap<ASMAbstractReg,ASMAbstractReg> alias;

    HashMap<ASMAbstractReg,ASMRegisterExpr> color;





    public void MainFunc(){

    }
    public void Build(){
        for (BasicBlockASMCFG b : prog.getNodes()){
            CFGNode<ASMInstruction> last = b.getBody().get(b.getBody().size()-1);
            Set<ASMAbstractReg> live = LVA.getOutMapping().get(last);
            for (int i = b.getBody().size() - 1; i >= 0; i--) {
                CFGNode<ASMInstruction> node = b.getBody().get(i);
                ASMInstruction instr = b.getBody().get(i).getStmt();
                if (instr instanceof ASMAbstractMov){
                    Set<ASMAbstractReg> uses =  usesInASM(instr);
                    Set<ASMAbstractReg> defs = defsInASM(instr);
                    live.removeAll(uses);
                    Set<ASMAbstractReg> combined = new HashSet<>(uses);
                    combined.addAll(defs);
                    for (ASMAbstractReg n : combined){
                        moveList.get(n).add(node);
                    }
                }
            }
        }
    }

    public void MakeWorklist(){

    }
    public void AssignColors(){

    }
    public void AddEdge(CFGNode<ASMInstruction> u, CFGNode<ASMInstruction> v){

    }


//    private boolean isMove(ASMInstruction instr){
//        return instr instanceof ASMMov || instr instanceof ASMMovabs;
//    }

}
