//package aar226_akc55_ayc62_ahl88.cfg.optimizations.ir;
//
//import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
//import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
//import aar226_akc55_ayc62_ahl88.cfg.optimizations.ir.BackwardIRDataflow;
//import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRStmt;
//import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRTemp;
//
//import java.util.HashSet;
//import java.util.Set;
//
//public class LiveVariableAnalysis extends BackwardIRDataflow<Set<IRTemp>> {
//    public LiveVariableAnalysis(CFGGraph<IRStmt> graph) {
//        super(
//                graph,
//                (n, l) -> {
//                    var useSet = use(n);
//                    var defSet = def(n);
//                    var l_temp = new HashSet<>(l);
//                    var diff = l_temp.remove(defSet);
//                    useSet.addAll()
//                },
//                (l1, l2) -> {
//                    var l1_temp = new HashSet<>(l1);
//                    l1_temp.addAll(l2);
//                    return l1_temp;
//                },
//                HashSet::new,
//                new HashSet<>()
//        );
//    }
//
//    private void use(CFGNode<IRStmt> n) {
//    }
//
//    private void def(CFGNode<IRStmt> n) {
//    }
//}