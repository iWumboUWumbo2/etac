package aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.ir.FunctionInliningVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.SExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.AbstractASMVisitor;

import java.util.ArrayList;

public class IRdud extends IRStmt{

    public IRdud(){
        System.out.println("created a dud");
    }
    @Override
    public IRNode accept(FunctionInliningVisitor v) {
        return null;
    }

    @Override
    public String label() {
        return "DUD";
    }

    @Override
    public void printSExp(SExpPrinter p) {

    }

    @Override
    public ArrayList<ASMInstruction> accept(AbstractASMVisitor v) {
        return null;
    }
}
