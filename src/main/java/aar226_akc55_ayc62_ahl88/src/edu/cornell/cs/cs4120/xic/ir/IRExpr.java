package aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir;

public interface IRExpr extends IRNode {
    boolean isConstant();

    long constant();
}