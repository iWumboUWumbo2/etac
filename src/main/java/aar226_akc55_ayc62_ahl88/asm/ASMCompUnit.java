package aar226_akc55_ayc62_ahl88.asm;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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
        StringBuilder out = new StringBuilder("ASMCompUnit{" + "globals=");
        for (Map.Entry<String, long[]> kv: globals.entrySet()){
            out.append(kv.toString());
        }
        out.append("\n functionToInstructionList=");
        for (Map.Entry<String, ArrayList<ASMInstruction>> kv: functionToInstructionList.entrySet()){
            out.append(kv.getKey()).append(":");
            for (ASMInstruction instr: kv.getValue()){
                out.append(instr).append("\n");
            }
        }
        out.append("\n functionToTempsMapping=");
        for (Map.Entry<String, HashSet<String>> kv: functionToTempsMapping.entrySet()){
            out.append(kv.toString()).append("\n");
        }
        out.append('}');
        return out.toString();
    }
}
