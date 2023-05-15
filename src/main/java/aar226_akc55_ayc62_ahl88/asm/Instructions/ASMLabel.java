package aar226_akc55_ayc62_ahl88.asm.Instructions;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.visit.RegisterAllocationTrivialVisitor;
import java.util.ArrayList;

/**
 * Class for labels
 */
public class ASMLabel extends ASMInstruction {
    String label;

    /**
     * @param label
     */
    public ASMLabel(String label) {
        super(ASMOpCodes.LABEL);
        this.label = label.replaceAll("'", "__prime__");
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString(){
        return opCodeToString() + label+":";
    }
    @Override
    public ArrayList<ASMInstruction> accept(RegisterAllocationTrivialVisitor regVisitor) {
        return regVisitor.visit(this);
    }
}
