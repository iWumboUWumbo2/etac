package aar226_akc55_ayc62_ahl88.asm;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ASMCompUnit {

    HashMap<String, long[]> globals;
    HashMap<String, ArrayList<ASMInstruction>> functionToInstructionList;
    HashMap<String, HashSet<String>> functionToTempsMapping;

    public ASMCompUnit(HashMap<String, long[]> globals, HashMap<String, ArrayList<ASMInstruction>> functions,
    HashMap<String, HashSet<String>> functionToTemps){
        this.globals = globals;
        functionToInstructionList = functions;
        functionToTempsMapping = functionToTemps;
    }

    public HashMap<String, ArrayList<ASMInstruction>> getFunctionToInstructionList() {
        return functionToInstructionList;
    }

    public HashMap<String, long[]> getGlobals() {
        return globals;
    }

    @Override
    public String toString() {
        return "ASMCompUnit{" +
                "globals=" + globals +
                ", functionToInstructionList=" + functionToInstructionList +
                ", functionToTempsMapping=" + functionToTempsMapping +
                '}';
    }
}
