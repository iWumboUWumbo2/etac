package aar226_akc55_ayc62_ahl88.newast.stmt.declarations;

import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.expr.Id;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
/**
 * Class For Declarations that do have a type
 * Annotation
 * Ex: a:int, bob:bool[], doom:int[4][3][2]
 */
public class AnnotatedTypeDecl extends Decl{
    /**
     * @param i Identifier
     * @param t Type of Identifier
     * @param l line Number
     * @param c Column Number
     */

    Id identifier;

    Type type;
    public AnnotatedTypeDecl(Id i, Type t, int l, int c) {
        super(l, c);
        identifier = i;
        type = t;

    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        p.startList();
        identifier.prettyPrint(p);
        type.prettyPrint(p);
        p.endList();
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
}
