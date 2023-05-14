package aar226_akc55_ayc62_ahl88.asm;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Class for CompUnit
 */
public class ASMCompUnit {
    HashSet<ASMData> globals;
    HashMap<String, ArrayList<ASMInstruction>> functionToInstructionList;
    HashMap<String, HashSet<String>> functionToTempsMapping;
    HashMap<String, Pair<Integer,Integer>> allFunctionsSigs;

    /**
     * @param globals Global variables
     * @param functions Function definitions
     * @param functionToTemps Map function to temporary
     * @param funcs Map function to signature
     */
    public ASMCompUnit(HashSet<ASMData> globals, HashMap<String, ArrayList<ASMInstruction>> functions,
    HashMap<String, HashSet<String>> functionToTemps, HashMap<String, Pair<Integer,Integer>> funcs){
        this.globals = globals;
        functionToInstructionList = functions;
        functionToTempsMapping = functionToTemps;
        allFunctionsSigs = funcs;
    }

    public HashMap<String, ArrayList<ASMInstruction>> getFunctionToInstructionList() {
        return functionToInstructionList;
    }

    public HashSet<ASMData> getGlobals() {
        return globals;
    }

    public HashMap<String, HashSet<String>> getFunctionToTempsMapping() {
        return functionToTempsMapping;
    }

    public HashMap<String, Pair<Integer, Integer>> getAllFunctionsSigs() {
        return allFunctionsSigs;
    }


    public String printInstructions(){
        StringBuilder out = new StringBuilder("ASMCompUnit{" + "globals=");
        out.append("functionToInstructionList=");
        for (Map.Entry<String, ArrayList<ASMInstruction>> kv: functionToInstructionList.entrySet()){
            out.append(kv.getKey()).append(":");
            for (ASMInstruction instr: kv.getValue()){
                out.append(instr).append("\n");
            }
        }
        return out.toString();
    }
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder("ASMCompUnit{" + "globals=");
        for (ASMData data: globals){
            out.append(data);
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

        out.append("\n allFunctions =");
        for (Map.Entry<String, Pair<Integer,Integer>> kv: allFunctionsSigs.entrySet()){
            out.append(kv.toString()).append("\n");
        }
        out.append('}');
        return out.toString();
    }
}
