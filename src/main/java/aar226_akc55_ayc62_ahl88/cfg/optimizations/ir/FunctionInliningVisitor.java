package aar226_akc55_ayc62_ahl88.cfg.optimizations.ir;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.FlattenIrVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.IRLoweringVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.ReplaceTempsAndLabelVisitor;

import java.util.*;

public class FunctionInliningVisitor implements IROPTVisitor<IRNode>{
    private int tempCnt;
    private int labelCnt;

    private String currentFunction;

    private HashMap<String,ArrayList<IRStmt>> inLinedFunctions;

    private HashMap<String,HashSet<String>> functionToTemps;
    private HashMap<String,HashSet<String>> functionToLabels;

    private String nxtTemp(String funcName) {
        return String.format(funcName + "_tp%d_fi_", (tempCnt++));
    }
    private String nxtLabel(String funcName) {
        return String.format(funcName + "lb%d_fi_", (labelCnt++));
    }
    ArrayList<String> allowedInline;

    public FunctionInliningVisitor() {
        tempCnt = 0;
        labelCnt = 0;
        inLinedFunctions = new HashMap<>();
        functionToTemps = new HashMap<>();
        functionToLabels = new HashMap<>();
    }
    public ArrayList<String> topoSort(IRCompUnit node){
        HashMap<String,Integer> inDegreeMap = new HashMap<>();
        HashMap<String,HashSet<String> > adjList= new HashMap<>();
        for (String name : node.functions().keySet()){
            inDegreeMap.put(name,0);
            adjList.put(name,new HashSet<String>());
        }
        for (String name : node.functions().keySet()){
            IRSeq body = (IRSeq) node.functions().get(name).body();
            for (IRStmt stmt: body.stmts()){
                if (stmt instanceof IRCallStmt call){
                    IRName dest = (IRName) call.target();
                    if (adjList.containsKey(dest.name()) &&  !adjList.get(dest.name()).contains(name)){ // library func not on adj
                        adjList.get(dest.name()).add(name);
                        inDegreeMap.put(name,inDegreeMap.get(name) + 1);
                    }
                }
            }
        }
        Queue<String> q = new ArrayDeque<>();
        for (String name : node.functions().keySet()){
            if (inDegreeMap.get(name) == 0){
                q.offer(name);
            }
        }
        ArrayList<String> order = new ArrayList<>();
        int index = 0;
        while (!q.isEmpty()){
            String func = q.poll();
            order.add(func);
            index++;
            for (String dest : adjList.get(func)){
                inDegreeMap.put(dest,inDegreeMap.get(dest)-1);
                if (inDegreeMap.get(dest) == 0){
                    q.offer(dest);
                }
            }
        }
//        System.out.println("order: " + order);
//        System.out.println("adjList: " + adjList);
        return order;
    }

    @Override
    public IRExpr visit(IRBinOp node) {
        return node;
    }

    @Override
    public IRExpr visit(IRCall node) {
        return node;
    }

    @Override
    public IRStmt visit(IRCallStmt node) {
        return node;
    }

    @Override
    public IRStmt visit(IRCJump node) {
        return node;
    }

    @Override
    public IRCompUnit visit(IRCompUnit node) {
        allowedInline = topoSort(node);
        ArrayList<String> functionVisitOrder = new ArrayList<>(allowedInline);
        System.out.println("allowed inline: " + allowedInline);

        for (String func: node.functions().keySet()){
            if (!functionVisitOrder.contains(func)){
                functionVisitOrder.add(func);
            }
        }
        // Get Temps used
        HashMap<String,IRFuncDecl> newIrFunc = new HashMap<>();
        System.out.println(functionVisitOrder);
        for (String funcToVisit: functionVisitOrder){
            System.out.println("functionName: " + funcToVisit);
            IRFuncDecl func = node.getFunction(funcToVisit);
            IRFuncDecl externalUse  = firstPass(func); // Generates the Inlined Version for other people to use
            IRFuncDecl inLinedFunc = func.accept(this);
            newIrFunc.put(funcToVisit,inLinedFunc);
            functionToTemps.put(funcToVisit,new HashSet<>());
            functionToLabels.put(funcToVisit,new HashSet<>());
            ArrayList<IRNode> flattened = externalUse.aggregateChildren(new FlattenIrVisitor());
            for (IRNode n : flattened){
                if (n instanceof IRTemp t){
                    if (!(t.name().startsWith("_ARG") || t.name().startsWith("_RV"))){ // Dont add Temps
                        functionToTemps.get(funcToVisit).add(t.name());
                    }
                }
                if (n instanceof IRLabel l){
                    functionToLabels.get(funcToVisit).add(l.name());
                }
            }
        }
        return new IRCompUnit(node.name(), newIrFunc,new ArrayList<>(),node.dataMap());
    }

    @Override
    public IRExpr visit(IRConst node) {
        return node;
    }

    @Override
    public IRExpr visit(IRESeq node) {
        return node;
    }

    @Override
    public IRStmt visit(IRExp node) {
        return node;
    }


    /**
     *
     * @param node
     * @return Inlined version of this function for other peoples use Still need to Replace Temps everytime we call though
     *
     */
    public IRFuncDecl firstPass(IRFuncDecl node){
        if (!allowedInline.contains(node.name())){
            return node;
        }
        IRSeq body = (IRSeq) node.body();
        ArrayList<IRStmt> newBody = new ArrayList<>();
        for (IRStmt stmt: body.stmts()){
            if (stmt instanceof IRCallStmt call){
                IRName destFunc = (IRName) call.target();
                if (allowedInline.contains(destFunc.name())) {
                    for (int i = 0; i < call.args().size(); i++) {
                        IRTemp dest = new IRTemp("_ARG" + (i + 1));
                        newBody.add(new IRMove(dest, call.args().get(i)));
                    }
                    ArrayList<IRStmt> mangle =  makeUnique(destFunc.name());
                    newBody.addAll(mangle);
                }else{
                    newBody.add(call);
                }
            }else if (stmt instanceof IRReturn ret){
                for (int i = 0; i< ret.rets().size();i++){
                    IRTemp dest = new IRTemp("_RV" + (i+1));
                    newBody.add(new IRMove(dest,ret.rets().get(i)));
                }
            }else{
                newBody.add(stmt);
            }
        }
        inLinedFunctions.put(node.name(),newBody);
        return new IRFuncDecl(node.name(),new IRSeq(newBody));
    }
    @Override
    public IRFuncDecl visit(IRFuncDecl node) {

        ArrayList<IRStmt> newBodyLocal = new ArrayList<>();
        IRSeq body = (IRSeq) node.body();
        for (IRStmt stmt: body.stmts()){
            if (stmt instanceof IRCallStmt call && allowedInline.contains(((IRName) call.target()).name())){
                String functionName = ((IRName) call.target()).name();
                for (int i = 0; i < call.args().size(); i++) {
                    IRTemp dest = new IRTemp("_ARG" + (i + 1));
                    newBodyLocal.add(new IRMove(dest, call.args().get(i)));
                }
                ArrayList<IRStmt> mangle =  makeUnique(functionName);
                newBodyLocal.addAll(mangle);
            }else{
                newBodyLocal.add(stmt);
            }
        }
        return new IRFuncDecl(node.name(),new IRSeq(newBodyLocal));
    }

    private ArrayList<IRStmt> makeUnique(String funcName) {
        ArrayList<IRStmt> irStmts = inLinedFunctions.get(funcName);
        HashSet<String> usedTemps = functionToTemps.get(funcName);
        HashSet<String> usedLabels = functionToLabels.get(funcName);
        HashMap<String,String> tempMapping = new HashMap<>();
        for (String temp: usedTemps){
            String mangled = nxtTemp(funcName) + temp;
            tempMapping.put(temp,mangled);
        }
        HashMap<String,String> labelMapping = new HashMap<>();
        for (String label: usedLabels){
            String mangled = nxtLabel(funcName) + label;
            labelMapping.put(label,mangled);
        }
        ReplaceTempsAndLabelVisitor replacementVisitor = new ReplaceTempsAndLabelVisitor(new IRNodeFactory_c(),tempMapping,labelMapping);
        ArrayList<IRStmt> result = new ArrayList<>();
        for (IRStmt stmt: irStmts){
            result.add((IRStmt) replacementVisitor.visit(stmt));
        }
        return result;
    }

    @Override
    public IRStmt visit(IRJump node) {
        return node;
    }

    @Override
    public IRStmt visit(IRLabel node) {
        return node;
    }

    @Override
    public IRExpr visit(IRMem node) {
        return node;
    }

    @Override
    public IRStmt visit(IRMove node) {
        return node;
    }

    @Override
    public IRExpr visit(IRName node) {
        return node;
    }

    @Override
    public IRStmt visit(IRReturn node) {
        return node;
    }

    @Override
    public IRStmt visit(IRSeq node) {
        return node;
    }

    @Override
    public IRExpr visit(IRTemp node) {
        return node;
    }
}
