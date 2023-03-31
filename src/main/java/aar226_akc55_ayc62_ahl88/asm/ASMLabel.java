package aar226_akc55_ayc62_ahl88.asm;

public class ASMLabel extends ASMInstruction {
    String label;

    public ASMLabel(String label) {
        super(ASMOpCodes.LABEL);
        this.label = label;
    }
}
