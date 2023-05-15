package aar226_akc55_ayc62_ahl88.newast.expr;

import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRExpr;
import aar226_akc55_ayc62_ahl88.visitors.ContainsBreakVisitor;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;
import org.apache.commons.text.*;
/**
 * Class for Integer Literal
 */
public class IntLiteral extends Expr{

    public long number;
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
        String escapeForm  = StringEscapeUtils.unescapeJava(inputChar);
        if (escapeForm.length() > 1) { // hex representation
            int first = inputChar.indexOf("{");
            int last = inputChar.lastIndexOf("}");
            number = Long.parseLong(inputChar.substring(first+1,last),16);
        }else if (escapeForm.length() == 1){ // single character
            number = escapeForm.charAt(0);
        }else{
            number  = inputChar.charAt(0);
        }
    }

    public IntLiteral(char inputChar ,int l, int c) {
        super(l, c);
        rawChar = Character.toString(inputChar);
        number = (int) inputChar;
    }

    @Override
    public Type typeCheck(SymbolTable s) {
        nodeType = new Type(Type.TypeCheckingType.INT);
        return nodeType;
    }

    public long getLong(){
        return number;
    }
    public String toString() {
        return (rawChar == null) ? Long.toString(number) : rawChar;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter p) {
        if (rawChar == null) {
            if (number == Long.MIN_VALUE){
                p.startList();
                p.printAtom("-");
                p.printAtom(toString().substring(1));
                p.endList();
            }else{
                p.printAtom(toString());
            }
        }
        else {
            p.printAtom("'"+ toString()+ "'");
        }
    }
    @Override
    public Boolean accept(ContainsBreakVisitor v) {
        return v.visit(this);
    }
    @Override
    public IRExpr accept(IRVisitor visitor) {
        return visitor.visit(this);
    }
}