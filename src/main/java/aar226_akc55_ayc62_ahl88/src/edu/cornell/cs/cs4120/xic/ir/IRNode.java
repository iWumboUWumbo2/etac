package aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.SExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.AggregateVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.CheckCanonicalIRVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.CheckConstFoldedIRVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.InsnMapsBuilder;

/** A node in an intermediate-representation abstract syntax tree. */
public interface IRNode {

    /**
     * Visit the children of this IR node.
     *
     * @param v the visitor
     * @return the result of visiting children of this node
     */
    IRNode visitChildren(IRVisitor v);

    <T> T aggregateChildren(AggregateVisitor<T> v);

    InsnMapsBuilder buildInsnMapsEnter(InsnMapsBuilder v);

    IRNode buildInsnMaps(InsnMapsBuilder v);

    CheckCanonicalIRVisitor checkCanonicalEnter(CheckCanonicalIRVisitor v);

    boolean isCanonical(CheckCanonicalIRVisitor v);

    boolean isConstFolded(CheckConstFoldedIRVisitor v);

    String label();

    /**
     * Print an S-expression representation of this IR node.
     *
     * @param p the S-expression printer
     */
    void printSExp(SExpPrinter p);
}
