package aar226_akc55_ayc62_ahl88.cfg.optimizations.ir;

import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
import aar226_akc55_ayc62_ahl88.cfg.CFGNode;
import aar226_akc55_ayc62_ahl88.cfg.HashSetInf;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.ir.ForwardIRDataflow;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.Pair;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

public class CopyProp extends ForwardIRDataflow<HashSetInf<Pair<IRTemp, IRTemp>>> {
    public CopyProp(CFGGraph<IRStmt> g) {
        super(
                g,
                (n, l) -> {
                    var a = gen(n);
                    a.addAll(lMinusDiff(l, n));
                    return a;
                },
                (l1, l2) -> {
                    if (l1.isInfSize()) return l2;
                    if (l2.isInfSize()) return l1;

                    var intersect = new HashSetInf<>(l1);
                    intersect.retainAll(l2);
                    return intersect;
                },
                () -> new HashSetInf<>(true),
                new HashSetInf<>(true)
        );
    }

    private static HashSetInf<Pair<IRTemp, IRTemp>> gen(CFGNode<IRStmt> node) {
        if (node.getStmt() instanceof IRMove move) {
            if (move.target() instanceof IRTemp tdest && move.source() instanceof IRTemp tsrc) {
                HashSetInf<Pair<IRTemp, IRTemp>> setInf = new HashSetInf<>(false);
                setInf.add(new Pair<>(tdest, tsrc));
                return setInf;
            }
        }
        return new HashSetInf<>(false);
    }

    private static HashSetInf<Pair<IRTemp, IRTemp>> lMinusDiff(HashSetInf<Pair<IRTemp, IRTemp>> l, CFGNode<IRStmt> node) {
        if (l.isInfSize()) return new HashSetInf<>(true);

        HashSetInf<Pair<IRTemp, IRTemp>> res = new HashSetInf<>(l);
        if (node.getStmt() instanceof IRMove move) {
            if (move.target() instanceof IRTemp temp) {
                res.removeIf(e -> e.part1().equals(temp) || e.part2().equals(temp));
            }
            if (move.source() instanceof IRCallStmt call) {
                for (int i = 0; i < call.n_returns(); i++) {
                    IRTemp t = new IRTemp("_RV" + i);
                    res.removeIf(e -> e.part1().equals(t) || e.part2().equals(t));
                }
            }
            return res;
        }

        return res;
    }
}
