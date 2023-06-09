package aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir;

import aar226_akc55_ayc62_ahl88.asm.ASMCompUnit;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.ir.FunctionInliningVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.SExpPrinter;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.AbstractASMVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.AggregateVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;

import java.util.*;

/** An intermediate representation for a compilation unit */
public class IRCompUnit extends IRNode_c {
    private final String name;
    private final Map<String, IRFuncDecl> functions;
    private final List<String> ctors;
    private final Map<String, IRData> dataMap;

    public IRCompUnit(String name) {
        this(name, new LinkedHashMap<>(), new ArrayList<>(), new LinkedHashMap<>());
    }

    public IRCompUnit(String name, Map<String, IRFuncDecl> functions) {
        this(name, functions, new ArrayList<>(), new LinkedHashMap<>());
    }

    public IRCompUnit(
            String name,
            Map<String, IRFuncDecl> functions,
            List<String> ctors,
            Map<String, IRData> dataMap) {
        this.name = name;
        this.functions = functions;
        this.ctors = ctors;
        this.dataMap = dataMap;
    }

    public void appendFunc(IRFuncDecl func) {
        functions.put(func.name(), func);
    }

    public void appendCtor(String functionName) {
        ctors.add(functionName);
    }

    public void appendData(IRData data) {
        dataMap.put(data.name(), data);
    }

    public String name() {
        return name;
    }

    public Map<String, IRFuncDecl> functions() {
        return functions;
    }

    public IRFuncDecl getFunction(String name) {
        return functions.get(name);
    }

    public List<String> ctors() {
        return ctors;
    }

    public Map<String, IRData> dataMap() {
        return dataMap;
    }

    public IRData getData(String name) {
        return dataMap.get(name);
    }

    @Override
    public String label() {
        return "COMPUNIT";
    }

    public ASMCompUnit accept(AbstractASMVisitor v) {
        return v.visit(this);
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        boolean modified = false;

        Map<String, IRFuncDecl> results = new LinkedHashMap<>();
        for (IRFuncDecl func : functions.values()) {
            IRFuncDecl newFunc = (IRFuncDecl) v.visit(this, func);
            if (newFunc != func) modified = true;
            results.put(newFunc.name(), newFunc);
        }
        if (modified){
            IRCompUnit nxtCompUnit = v.nodeFactory().IRCompUnit(name, results);
            for (Map.Entry<String,IRData> entry: dataMap.entrySet()){
                nxtCompUnit.appendData(entry.getValue());
            }
            return nxtCompUnit;
        }
        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        for (IRFuncDecl func : functions.values()) result = v.bind(result, v.visit(func));
        return result;
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("COMPUNIT");
        p.printAtom(name);
        for (String ctor : ctors) {
            p.printAtom(ctor);
        }
        for (IRData data : dataMap.values()) {
            p.startList();
            p.printAtom("DATA");
            p.printAtom(data.name());
            p.startList();
            for (long value : data.data()) {
                p.printAtom(String.valueOf(value));
            }
            p.endList();
            p.endList();
        }
        for (IRFuncDecl func : functions.values()) func.printSExp(p);
        p.endList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IRCompUnit that = (IRCompUnit) o;
        return Objects.equals(name, that.name) && Objects.equals(functions, that.functions) && Objects.equals(ctors, that.ctors) && Objects.equals(dataMap, that.dataMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, functions, ctors, dataMap);
    }

    @Override
    public IRCompUnit accept(FunctionInliningVisitor v) {
        return v.visit(this);
    }
}
