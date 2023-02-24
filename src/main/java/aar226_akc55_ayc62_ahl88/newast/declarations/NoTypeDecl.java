package aar226_akc55_ayc62_ahl88.newast.declarations;

import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;


/**
 * Class For Declarations that don't have a type
 * Annotation
 * Ex: a , bob, job etc
 * Not: a:int, bob:bool[]
 */
public class NoTypeDecl extends Decl{
    Id identifier;
    /**
     * @param i Identifier Input
     * @param l Line Number
     * @param c Column Number
     */
    public NoTypeDecl(Id i, int l, int c) {
        super(l, c);
        identifier = i;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        identifier.prettyPrint(p);
    }

}
//    @Override
//    public void prettyPrint(CodeWriterSExpPrinter p) {
//        if (type != null && id != null){
//            p.startList();
//            id.prettyPrint(p);
//            type.prettyPrint(p);
//            p.endList();
//        } else if (id != null){
//            id.prettyPrint(p);
//        }
//        else acc.prettyPrint(p);
//    }