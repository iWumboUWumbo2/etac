package aar226_akc55_ayc62_ahl88.asm.Instructions;

import aar226_akc55_ayc62_ahl88.asm.ASMOpCodes;
import aar226_akc55_ayc62_ahl88.asm.visit.RegisterAllocationTrivialVisitor;

import java.util.ArrayList;
import java.util.HashMap;

public class ASMComment extends ASMInstruction{

    private String comment;
    private String functionName;
    public ASMComment(String comment,String funcName) {
        super(ASMOpCodes.COMMENT);
        this.comment = comment;
        functionName = funcName;
    }
    public String getFunctionName() {
        return functionName;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "# " + comment;
    }

    @Override
    public ArrayList<ASMInstruction> accept(RegisterAllocationTrivialVisitor regVisitor) {
        return regVisitor.visit(this);
    }
}
