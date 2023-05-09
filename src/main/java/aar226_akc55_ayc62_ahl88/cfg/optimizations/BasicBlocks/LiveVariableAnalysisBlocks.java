package aar226_akc55_ayc62_ahl88.cfg.optimizations.BasicBlocks;

import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMAbstractReg;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMRegisterExpr;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;
import aar226_akc55_ayc62_ahl88.asm.Opts.BasicBlockASMCFG;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.ir.LiveVariableAnalysis;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRTemp;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.Pair;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

public class LiveVariableAnalysisBlocks extends BackwardBlockDataflow<Set<IRTemp>>{

    public LiveVariableAnalysisBlocks(CFGGraphBasicBlock g) {
        super(g,
                (n,outN) ->{
                    Pair<Set<IRTemp>,Set<IRTemp>> res = blockFuncIR(n);
                    Set<IRTemp> useSet = res.part1();
                    Set<IRTemp> defSet = res.part2();

                    Set<IRTemp> l_temp = new HashSet<>(outN);
                    l_temp.removeAll(defSet);
                    l_temp.addAll(useSet);
                    return l_temp;
                },
                (l1, l2) -> {
                    Set<IRTemp> l1_temp = new HashSet<>(l1);
                    l1_temp.addAll(l2);
                    return l1_temp;
                }, HashSet::new,
                new HashSet<>());
    }
    /**
     * Creates Gen[pn] and kill[pn]
     * @param block
     * @return
     */

    // for backwards its gen[ns] = gen[n] U (gen[s] - kill[n]) kill[ns]  = kill[s] U kill[n]
    public static Pair<Set<IRTemp>,Set<IRTemp>> blockFuncIR(BasicBlockCFG block){
        if (block.getBody().size() == 0){
            return new Pair<>(new HashSet<>(),new HashSet<>());
        }
        Set<IRTemp> genns = LiveVariableAnalysis.use(block.getBody().get(block.getBody().size()-1).getStmt());
        Set<IRTemp> killns = LiveVariableAnalysis.def(block.getBody().get(block.getBody().size()-1).getStmt());
        for (int i = block.getBody().size()-2;i>=0;i--){
            CFGNode<IRStmt> n = block.getBody().get(i);
            genns.removeAll(LiveVariableAnalysis.def(n.getStmt()));
            genns.addAll(LiveVariableAnalysis.use(n.getStmt()));
            killns.addAll(LiveVariableAnalysis.def(n.getStmt()));
        }
        return new Pair<>(genns,killns);

    }
}
