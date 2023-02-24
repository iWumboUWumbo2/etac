package aar226_akc55_ayc62_ahl88.newast.interfaceNodes;

import aar226_akc55_ayc62_ahl88.newast.AstNode;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

public class EtiInterface extends AstNode {
    private ArrayList<Method_Interface> methods_inter;

    public EtiInterface(ArrayList<Method_Interface> mi,int l, int c) {
        super(l,c);
        this.methods_inter = mi;
        if (mi.size() == 0){
            throw new Error(1+":"+1+" error: no method definitions");
        }
    }
    // need pretty
    public String toString() {
        String build = "";
        build += methods_inter.toString();
        return build;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startUnifiedList();
        p.startUnifiedList();
        methods_inter.forEach(d -> d.prettyPrint(p));
        p.endList();
        p.endList();
    }
}