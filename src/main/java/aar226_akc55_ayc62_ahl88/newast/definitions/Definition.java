package aar226_akc55_ayc62_ahl88.newast.definitions;
import aar226_akc55_ayc62_ahl88.newast.AstNode;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public abstract class Definition extends AstNode{
//    private Method method;
//    private Globdecl globdecl;
//
//    public Definition( Method m){
//        this.method = m;
//        this.globdecl = null;
//    }
//
//    public Definition(Globdecl globdecl) {
//        this.globdecl = globdecl;
//        this.method = null;
//    }
//
//    public String toString(){
//        return (method == null) ? globdecl.toString() : method.toString();
//    }
//
//    @Override
//    public void prettyPrint(CodeWriterSExpPrinter p) {
//        p.startList();
//        if (method == null) globdecl.prettyPrint(p);
//        else method.prettyPrint(p);
//        p.endList();
//    }

    public Definition(int l, int c) {
        super(l, c);
    }
}
