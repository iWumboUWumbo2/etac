package aar226_akc55_ayc62_ahl88.newast.expr;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/**
 * Class for Integer Literal
 */
public class IntLiteral extends Expr{
    private long number;
    private String rawChar;

    /**
     * @param i incoming Long into Int Literal
     * @param l line Number
     * @param r Column Number
     */
    public IntLiteral(long i, int l, int r) {
        super(l,r);
        number = -i;
    }

    /**
     * @param inputChar character into IntLiteral
     * @param l line Number
     * @param c Column Number
     */
    //  '\x{8049}'
    //  'p'
    public IntLiteral(String inputChar ,int l, int c) {
        super(l, c);
        rawChar = inputChar;
        if (inputChar.length() > 1) { // hex representation
            int first = inputChar.indexOf("{");
            int last = inputChar.lastIndexOf("}");
            number = Long.parseLong(inputChar.substring(first+1,last),16);
        }else{ // single character
            number  = Character.getNumericValue(inputChar.charAt(0));
        }
    }

    @Override
    public Type typeChecker(SymbolTable s) {
        return new Type(Type.TypeCheckingType.INT, getLine(), getColumn());
    }

    public long getLong(){
        return number;
    }
    public String toString() {
        return (rawChar == null) ? Long.toString(number) : rawChar;
    }

    public void prettyPrint(CodeWriterSExpPrinter p) {
        if (rawChar == null) {
            p.printAtom(toString());
        }
        else {
            p.printAtom("'"+ toString()+ "'");
        }
    }
}
