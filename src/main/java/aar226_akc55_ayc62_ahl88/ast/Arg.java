package aar226_akc55_ayc62_ahl88.ast;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.Pair;
public class Arg {
    Pair<Id, Type> arg;

    public Arg(String id, boolean t, Dimension d) {
        arg = new Pair(new Id(id), new Type(t, d));
    }

    public void prettyPrinter(CodeWriterSExpPrinter p) {
        p.startList();
        arg.part1().prettyPrint(p);
        arg.part2().prettyPrint(p);
        p.endList();
    }

}
