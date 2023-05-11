package aar226_akc55_ayc62_ahl88.cfg.optimizations.BasicBlocks;

import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.cfg.HashSetInf;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.ir.LiveVariableAnalysis;
import aar226_akc55_ayc62_ahl88.newast.stmt.Block;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRMove;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRTemp;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ReachingDefinitionsBlock extends ForwardBlockDataflow<Set<CFGNode<IRStmt>>> {


    public HashMap<CFGNode<IRStmt>,Set<CFGNode<IRStmt>>> singleNodeInMapping;
    public HashMap<CFGNode<IRStmt>,Set<CFGNode<IRStmt>>> singleNodeOutMapping;
    public ReachingDefinitionsBlock(CFGGraphBasicBlock g,final HashMap<IRTemp, HashSet<CFGNode<IRStmt>>> tempToAllDefs) {
        super(g,

                (n,inN)->{
                    Pair<Set<CFGNode<IRStmt>>,Set<CFGNode<IRStmt>>> res = blockFuncIRForward(n,tempToAllDefs);
//                     Gen[pn] and kill[pn]
                    Set<CFGNode<IRStmt>> gen = res.part1();
                    Set<CFGNode<IRStmt>> kill = res.part2();
                    Set<CFGNode<IRStmt>> outN = new HashSet<>(inN);
                    outN.removeAll(kill);
                    outN.addAll(gen);
//                    System.out.println("block: " + n + "\n outN: " + outN);
                    return outN;
                },
                (l1, l2) -> {
                    Set<CFGNode<IRStmt>> l1_temp = new HashSet<>(l1);
                    l1_temp.addAll(l2);
                    return l1_temp;
                }, HashSet::new,
                new HashSet<>());
        singleNodeInMapping = new HashMap<>();
        singleNodeOutMapping = new HashMap<>();
    }

    public static HashMap<IRTemp, HashSet<CFGNode<IRStmt>>> locateDefs(CFGGraphBasicBlock g){
        HashMap<IRTemp, HashSet<CFGNode<IRStmt>>> res = new HashMap<>();
        for (BasicBlockCFG block : g.getNodes()){
            for (CFGNode<IRStmt> node : block.getBody()){
                Set<IRTemp> defs = LiveVariableAnalysis.def(node.getStmt());
                for (IRTemp def : defs){
                    if (!res.containsKey(def)){
                        res.put(def,new HashSet<>());
                    }
                    res.get(def).add(node);
                }
            }
        }
        return res;
    }

    /**
     * Creates Gen[pn] and kill[pn] for Reaching Definition Blocks
     * @param block
     * @return
     */

    // gen[pn] = gen[n] ∪ (gen[p] − kill[n])
    // kill[pn] = kill[p] ∪ kill[n].

    private static Pair<Set<CFGNode<IRStmt>>,Set<CFGNode<IRStmt>>> blockFuncIRForward(BasicBlockCFG block,
                                                                                     HashMap<IRTemp,HashSet<CFGNode<IRStmt>>> temps){
        if (block.getBody().size() == 0){
            return new Pair<>(new HashSet<>(),new HashSet<>());
        }
        Set<CFGNode<IRStmt>> genpn = genReach(block.getBody().get(0));
        Set<CFGNode<IRStmt>> killpn = killReach(block.getBody().get(0),temps);

        for (int i = 1; i< block.getBody().size();i++){
            CFGNode<IRStmt> n = block.getBody().get(i);
            genpn.removeAll(killReach(n,temps));
            genpn.addAll(genReach(n));
            killpn.addAll(killReach(n,temps));
        }
        return new Pair<>(genpn,killpn);

    }


    private static Set<CFGNode<IRStmt>> killReach(CFGNode<IRStmt> node,
                                           HashMap<IRTemp,HashSet<CFGNode<IRStmt>>> tempToAllDefs){
        if (node.getStmt() instanceof IRMove mov && mov.target()
                instanceof IRTemp t){
            // Copy
            Set<CFGNode<IRStmt>> allDefSites = new HashSet<>(tempToAllDefs.get(t));
            allDefSites.remove(node);
            return allDefSites;
        }else{
            return new HashSet<>();
        }
    }

    private static Set<CFGNode<IRStmt>> genReach(CFGNode<IRStmt> node){
        if (node.getStmt() instanceof IRMove mov && mov.target()
                instanceof IRTemp t){
            Set<CFGNode<IRStmt>> empty = new HashSet<>();
            empty.add(node);
            return empty;
        }else{
            return new HashSet<>();
        }
    }

    public void getSingleMapping(
            HashMap<IRTemp,HashSet<CFGNode<IRStmt>>> tempToAllDefs){
        for (BasicBlockCFG block : graph.getNodes()){
            Set<CFGNode<IRStmt>> inBlock = inMapping.get(block);
            for (CFGNode<IRStmt> node : block.getBody()){
                singleNodeInMapping.put(node,new HashSet<>(inBlock));
                inBlock.removeAll(killReach(node,tempToAllDefs));
                inBlock.addAll(genReach(node));
                singleNodeOutMapping.put(node,new HashSet<>(inBlock));
            }
        }
    }
}
