package aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;

import java.util.ArrayList;

public class FlattenIrVisitor extends AggregateVisitor<ArrayList<IRNode>> {

    @Override
    public ArrayList<IRNode> unit() {
        return new ArrayList<>();
    }

    @Override
    public ArrayList<IRNode> bind(ArrayList<IRNode> r1, ArrayList<IRNode> r2) {
        ArrayList<IRNode> ret = new ArrayList<>(r1);
        ret.addAll(r2);
        return ret;
    }

    @Override
    protected ArrayList<IRNode> leave(IRNode parent, IRNode n, ArrayList<IRNode> r,
                                      AggregateVisitor<ArrayList<IRNode>> v_) {
        r.add(n);
        return r;
    }


}
