package aar226_akc55_ayc62_ahl88.newast.declarations;

import aar226_akc55_ayc62_ahl88.newast.expr.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

/**
 * Class for Array Access Reassignment
 */
public class ArrAccessDecl extends Decl{

    private Id id;
    private ArrayList<Expr> indices;

    /**
     * @param id id
     * @param indices indices
     * @param l line number
     * @param c column number
     */
    public ArrAccessDecl(Id id, ArrayList<Expr> indices, int l, int c) {
        super(l, c);
        this.id = id;
        this.indices = indices;
    }

    public void prettyPrint(CodeWriterSExpPrinter p) {

        for (int i = 0; i< indices.size();i++){
            p.startList();
            p.printAtom("[]");
        }
        id.prettyPrint(p);

        for (int i = 0; i<indices.size();i++){
            indices.get(i).prettyPrint(p);
            p.endList();
        }
    }



}
