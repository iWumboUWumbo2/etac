package aar226_akc55_ayc62_ahl88.visitors;

import aar226_akc55_ayc62_ahl88.newast.Dimension;
import aar226_akc55_ayc62_ahl88.newast.Program;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.Use;
import aar226_akc55_ayc62_ahl88.newast.declarations.AnnotatedTypeDecl;
import aar226_akc55_ayc62_ahl88.newast.declarations.ArrAccessDecl;
import aar226_akc55_ayc62_ahl88.newast.declarations.NoTypeDecl;
import aar226_akc55_ayc62_ahl88.newast.declarations.UnderScore;
import aar226_akc55_ayc62_ahl88.newast.definitions.Globdecl;
import aar226_akc55_ayc62_ahl88.newast.definitions.Method;
import aar226_akc55_ayc62_ahl88.newast.definitions.MultiGlobalDecl;
import aar226_akc55_ayc62_ahl88.newast.expr.*;
import aar226_akc55_ayc62_ahl88.newast.expr.arrayaccessexpr.ArrayAccessExpr;
import aar226_akc55_ayc62_ahl88.newast.expr.arrayliteral.ArrayValueLiteral;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.boolbop.*;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.intbop.IntOutBinop;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.intbop.PlusBinop;
import aar226_akc55_ayc62_ahl88.newast.expr.unop.booluop.NotUnop;
import aar226_akc55_ayc62_ahl88.newast.expr.unop.intuop.IntegerNegExpr;
import aar226_akc55_ayc62_ahl88.newast.stmt.*;
import aar226_akc55_ayc62_ahl88.newast.stmt.declstmt.DeclAssignStmt;
import aar226_akc55_ayc62_ahl88.newast.stmt.declstmt.DeclNoAssignStmt;
import aar226_akc55_ayc62_ahl88.newast.stmt.declstmt.MultiDeclAssignStmt;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRConst;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRExpr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.InternalCompilerError;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IRVisitor implements Visitor<IRNode>{

    private static final int WORD_BYTES = 8;
    private static final String OUT_OF_BOUNDS = "out_of_bounds_error";
    private int labelCnt;
    private int tempCnt;
    private final String compUnitName;
    public IRVisitor(String name) {
        labelCnt = 0;
        tempCnt = 0;
        compUnitName = name;
    }
    private String nxtLabel() {
        return String.format("l%d", (labelCnt++));
    }

    private String nxtTemp() {
        return String.format("t%d", (tempCnt++));
    }

    @Override
    public IRExpr visit(IntOutBinop node) {
//        DIVIDE, HIGHMULT, MINUS, MODULO, TIMES
        Expr e1 = node.getLeftExpr();
        Expr e2 = node.getRightExpr();

        IRExpr ire1 = e1.accept(this);
        IRExpr ire2 = e2.accept(this);
        IRBinOp.OpType op = node.getOpType();

        return new IRBinOp(op, ire1, ire2);
    }

    public IRExpr plusArrays(ArrayValueLiteral e1, ArrayValueLiteral e2) {

        // START ALLOCATE ARR1
        String t1 = nxtTemp();   // temp label for malloc
        ArrayList<Expr> values1 = e1.getValues();
        long n1 = values1.size();
        String l1 = nxtTemp();

        // reg[l] <- length
        IRMove length_to_l1 = new IRMove(new IRTemp(l1), new IRConst(n1));

        // 8*n+8
        IRBinOp size1 = new IRBinOp(IRBinOp.OpType.ADD,
                new IRBinOp(IRBinOp.OpType.MUL,
                        new IRTemp(l1),
                        new IRConst(WORD_BYTES)),
                new IRConst(WORD_BYTES));

        // CALL(NAME(malloc), size)
        IRCall alloc_call1 = new IRCall(new IRName("_xi_alloc"), size1);

        // reg[t] <- call malloc
        IRMove malloc_move1 = new IRMove(new IRTemp(t1), alloc_call1);

        IRMove size_move1 = new IRMove(new IRMem(new IRTemp(t1)), new IRTemp(l1));

        List<IRStmt> seq_list1 = new ArrayList<>(List.of(length_to_l1, malloc_move1, size_move1));

        for(int i = 0; i < n1; i++) {
            IRExpr ire1 = (IRExpr) values1.get(i).accept(this);
            IRMove move_elmnt1 = new IRMove(new IRMem(new IRBinOp(
                    IRBinOp.OpType.ADD,
                    new IRTemp(t1),
                    new IRConst(8*(i+1)))),
                    ire1 );
            seq_list1.add(move_elmnt1);
        }

        IRSeq ir_seq1 = new IRSeq(seq_list1);

        IRESeq allo_arr1 =  new IRESeq(ir_seq1,
                        new IRBinOp(IRBinOp.OpType.ADD,
                        new IRTemp(t1),
                        new IRConst(WORD_BYTES)));
        // END ALLOCATE ARR1

        // START ALLOCATE ARR2
        String t2 = nxtTemp();   // temp label for malloc
        ArrayList<Expr> values2 = e2.getValues();
        long n2 = values2.size();
        String l2 = nxtTemp();

        // reg[l] <- length
        IRMove length_to_l2 = new IRMove(new IRTemp(l2), new IRConst(n2));

        // 8*n+8
        IRBinOp size2 = new IRBinOp(IRBinOp.OpType.ADD,
                new IRBinOp(IRBinOp.OpType.MUL,
                        new IRTemp(l2),
                        new IRConst(WORD_BYTES)),
                new IRConst(WORD_BYTES));

        // CALL(NAME(malloc), size)
        IRCall alloc_call2 = new IRCall(new IRName("_xi_alloc"), size2);

        // reg[t] <- call malloc
        IRMove malloc_move2 = new IRMove(new IRTemp(t2), alloc_call2);

        IRMove size_move2 = new IRMove(new IRMem(new IRTemp(t2)), new IRTemp(l2));

        List<IRStmt> seq_list2 = new ArrayList<IRStmt>(List.of(length_to_l2, malloc_move2,size_move2));

        for(int i = 0; i < n2; i++) {
            IRExpr ire2 = (IRExpr) values2.get(i).accept(this);
            IRMove move_elmnt2 = new IRMove(new IRMem(new IRBinOp(
                    IRBinOp.OpType.ADD,
                    new IRTemp(t2),
                    new IRConst(8*(i+1)))),
                    ire2 );
            seq_list2.add(move_elmnt2);
        }

        IRSeq ir_seq2 = new IRSeq(seq_list2);

        IRESeq allo_arr2 =  new IRESeq(ir_seq2, new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(t2), new IRConst(WORD_BYTES)));
        // END ALLOCATE ARR2

        String size_reg = nxtTemp();
        IRExpr get_new_size = new IRBinOp(IRBinOp.OpType.ADD,
                new IRMem(new IRBinOp(
                        IRBinOp.OpType.SUB, new IRTemp(t1), new IRConst(8))),
                new IRMem(new IRBinOp(
                        IRBinOp.OpType.SUB, new IRTemp(t1), new IRConst(8))));




    }

    @Override
    public IRExpr visit(PlusBinop node) {
        Expr e1 = node.getLeftExpr();
        Expr e2 = node.getRightExpr();
        IRExpr ire1 = e1.accept(this);
        IRExpr ire2 = e2.accept(this);

        // if both arrays, add
        // if one unknown array and other array, return array
        if  (e1.getNodeType().isArray() && e2.getNodeType().isArray()) {
            if (e1.getNodeType().isUnknownArray() && !e2.getNodeType().isUnknownArray()) {
                return ire2;
            } else if (!e1.getNodeType().isUnknownArray() && e2.getNodeType().isUnknownArray()) {
                return ire1;
            } else {
                return plusArrays((ArrayValueLiteral) e1, (ArrayValueLiteral) e2, ire1, ire2);
            }
        }

        // if both ints, return irbinop
        // if one unknown and other int, return int
        if (e1.getNodeType().getType() == Type.TypeCheckingType.INT && e2.getNodeType().getType() == Type.TypeCheckingType.INT){
            return new IRBinOp(IRBinOp.OpType.ADD, ire1, ire2);    
        } else if (e1.getNodeType().isUnknown() && !e2.getNodeType().isUnknownArray()) {
            return new IRBinOp(IRBinOp.OpType.ADD, new IRConst(0), ire2);
        } else {
            return new IRBinOp(IRBinOp.OpType.ADD, ire1, new IRConst(0));
        }
    }

    @Override
    public IRNode visit(IntegerComparisonBinop node) {
//        < , <= , > , >=
        Expr e1 = node.getLeftExpr();
        Expr e2 = node.getRightExpr();

        IRExpr ire1 = e1.accept(this);
        IRExpr ire2 = e2.accept(this);
        IRBinOp.OpType op = node.getOpType();

        return new IRBinOp(op, ire1, ire2);
    }

    @Override
    public IRExpr visit(EquivalenceBinop node) {
        IRExpr l = node.getLeftExpr().accept(this);
        IRExpr r = node.getRightExpr().accept(this);

        IRBinOp.OpType op = node.getOpType();

        return new IRBinOp(op, l, r);
    }

    @Override
    public IRExpr visit(LogicalBinop node) {
        String l1 = nxtLabel();
        String l2 = nxtLabel();
        String lend = nxtLabel();
        String x = nxtTemp();
        Expr e1 = node.getLeftExpr();
        Expr e2 = node.getRightExpr();
        switch (node.getBinopType()){
            case AND:

                return new IRESeq(new IRSeq(new IRMove(new IRTemp(x), new IRConst(0)),
                        new IRCJump((IRExpr) e1.accept(this), l1, lend),
                        new IRLabel(l1), new IRCJump((IRExpr) e2.accept(this), l2, lend),
                        new IRLabel(l2), new IRMove(new IRTemp(x), new IRConst(1)),
                        new IRLabel(lend)),
                        new IRTemp(x));
            case OR:

                return new IRESeq(new IRSeq(new IRMove(new IRTemp(x), new IRConst(1)),
                        new IRCJump((IRExpr) e1.accept(this), lend, l1),
                        new IRLabel(l1), new IRCJump((IRExpr) e2.accept(this), lend, l2),
                        new IRLabel(l2), new IRMove(new IRTemp(x), new IRConst(0)),
                        new IRLabel(lend)),
                        new IRTemp(x));
            default:
                throw new Error("NOT LOGICAL BINOP");
        }
    }

    @Override
    public IRExpr visit(NotUnop node) {
        IRExpr ire = (IRExpr) node.accept(this);
        return new IRBinOp(IRBinOp.OpType.XOR, new IRConst(1), ire);
    }

    @Override
    public IRExpr visit(IntegerNegExpr node) {
        IRExpr ire = (IRExpr) node.accept(this);
        return new IRBinOp(IRBinOp.OpType.SUB, new IRConst(0), ire);
    }

    @Override
    public IRExpr visit(BoolLiteral node) {
        return new IRConst(node.boolVal ? 1 : 0);
    }

    @Override
    public IRExpr visit(IntLiteral node) {
        return new IRConst(node.number);
    }

    @Override
    public IRExpr visit(Length node) {
        String x = nxtTemp();
        IRMem mem = new IRMem(new IRBinOp(IRBinOp.OpType.SUB, (IRExpr) node.getArg().accept(this), new IRConst(WORD_BYTES)));
        IRMove move = new IRMove(new IRTemp(x), mem);
        IRESeq seq = new IRESeq(move, new IRTemp(x));
        return seq;
    }

    @Override
    public IRExpr visit(FunctionCallExpr node) {
        IRName func = new IRName(genABIFunc(node.getFunctionSig(),node.getId()));
        ArrayList<IRExpr> paramListIR = new ArrayList<>();
        for (Expr param: node.getArgs()){
            paramListIR.add((IRExpr) param.accept(this));
        }
        return new IRCall(func,paramListIR);
    }

    @Override
    public IRExpr visit(Id node) { // x = a; this is only a
        return new IRTemp(node.toString());
    }

    @Override
    public IRExpr visit(ArrayValueLiteral node) { // Gonna have to be DATA if String
        String t = nxtTemp();   // temp label for malloc
        ArrayList<Expr> values = node.getValues();
        long n = values.size();
        String l = nxtTemp();

        // reg[l] <- length
        IRMove length_to_l = new IRMove(new IRTemp(l), new IRConst(n));

        // 8*n+8
        IRBinOp size = new IRBinOp(IRBinOp.OpType.ADD,
                new IRBinOp(IRBinOp.OpType.MUL,
                        new IRTemp(l),
                        new IRConst(WORD_BYTES)),
                new IRConst(WORD_BYTES));

        // CALL(NAME(malloc), size)
        IRCall alloc_call = new IRCall(new IRName("_xi_alloc"), size);

        // reg[t] <- call malloc
        IRMove malloc_move = new IRMove(new IRTemp(t), alloc_call);

        IRMove size_move = new IRMove(new IRMem(new IRTemp(t)), new IRTemp(l));

        List<IRStmt> seq_list = new ArrayList<>(List.of(length_to_l, malloc_move, size_move));

        for(int i = 0; i < n; i++) {
            IRExpr ire = (IRExpr) values.get(i).accept(this);
            IRMove move_elmnt = new IRMove(new IRMem(new IRBinOp(
                    IRBinOp.OpType.ADD,
                    new IRTemp(t),
                    new IRConst(8*(i+1)))),
                    ire );
            seq_list.add(move_elmnt);
        }

        IRSeq ir_seq = new IRSeq(seq_list);

        return new IRESeq(ir_seq,
                new IRBinOp(IRBinOp.OpType.ADD,
                        new IRTemp(t),
                        new IRConst(WORD_BYTES)));

    }
    @Override
    public IRExpr visit(ArrayAccessExpr node) {
        IRExpr arrIR = node.getOrgArray().accept(this);
        assert(node.getIndicies().size() >= 1);
        IRExpr firstInd = node.getIndicies().get(0).accept(this);
        String ta = nxtTemp();
        String ti = nxtTemp();
        String lok = nxtLabel();
        IRESeq sol = new IRESeq( // 1d array need loop for further
                new IRSeq(
                    new IRMove(new IRTemp(ta),arrIR),
                    new IRMove(new IRTemp(ti),firstInd),
                    new IRCJump(
                        new IRBinOp(IRBinOp.OpType.ULT,
                                new IRTemp(ti),
                                new IRMem(
                                        new IRBinOp(IRBinOp.OpType.SUB,
                                                new IRTemp(ta),
                                                new IRConst(8)))),
                    lok,OUT_OF_BOUNDS), new IRLabel(lok)),
                new IRMem(
                        new IRBinOp(
                            IRBinOp.OpType.ADD,
                            new IRTemp(ta),
                            new IRBinOp(IRBinOp.OpType.MUL,new IRTemp(ti),new IRConst(8))
                        )));
        if (node.getIndicies().size() == 1){
            return sol;
        }
        return accessRecursive(1, node, sol);
    }
//
    private IRESeq accessRecursive(int ind, ArrayAccessExpr node, IRESeq ire){
        IRExpr curInd = node.getIndicies().get(ind).accept(this);
        String ta = nxtTemp();
        String ti = nxtTemp();
        String lok = nxtLabel();
        IRESeq sol = new IRESeq( // 1d array need loop for further
                new IRSeq(
                        new IRMove(new IRTemp(ta),ire),
                        new IRMove(new IRTemp(ti),curInd),
                        new IRCJump(
                                new IRBinOp(IRBinOp.OpType.ULT,
                                        new IRTemp(ti),
                                        new IRMem(
                                                new IRBinOp(IRBinOp.OpType.SUB,
                                                        new IRTemp(ta),
                                                        new IRConst(8)))),
                                lok,OUT_OF_BOUNDS), new IRLabel(lok)),
                new IRMem(
                        new IRBinOp(IRBinOp.OpType.ADD,
                                new IRTemp(ta),
                                new IRBinOp(IRBinOp.OpType.MUL,new IRTemp(ti),new IRConst(8))
                        )));
        if (ind == node.getIndicies().size() -1){
            return sol;
        }
        return accessRecursive(ind + 1, node, sol);
    }

    @Override
    public IRStmt visit(Block node) {
        ArrayList<Stmt> statements = node.getStatementList();
        ArrayList<IRStmt> IRstmtList = new ArrayList<IRStmt>();
        for (Stmt stmt: statements) {
            IRstmtList.add((IRStmt) stmt.accept(this));
        }
        return new IRSeq(IRstmtList);
    }

    @Override
    public IRStmt visit(IfElse node) {
        IRStmt iFStatement = (IRStmt) node.getIfState().accept(this);
        IRStmt elseStatement = (IRStmt) node.getElseState().accept(this);
        String lt = nxtLabel();
        String lf = nxtLabel();
        String lafter = nxtLabel();
        IRStmt condStmt = booleanAsControlFlow(node.getGuard(),lt,lf);
        IRJump endJmp = new IRJump(new IRName(lafter));
        return new IRSeq(condStmt,
                new IRLabel(lt),
                iFStatement,
                endJmp,
                new IRLabel(lf),
                elseStatement,
                new IRLabel(lafter)
        );
    }

    @Override
    public IRStmt visit(IfOnly node) {
        String l1 = nxtLabel();
        String l2 = nxtLabel();
        IRStmt condStmt = booleanAsControlFlow(node.guard,l1,l2);
        IRStmt statement = (IRStmt) node.ifState.accept(this);
        return new IRSeq(condStmt,new IRLabel(l1),statement, new IRLabel(l2));
    }

    @Override
    public IRStmt visit(ProcedureCall node) {
        IRName func = new IRName(genABIFunc(node.getFunctionSig(),node.getIdentifier()));
        ArrayList<IRExpr> paramListIR = new ArrayList<>();
        for (Expr param: node.getParamList()){
            paramListIR.add((IRExpr) param.accept(this));
        }
        return new IRExp(new IRCall(func,paramListIR));
    }

    @Override
    public IRStmt visit(Return node) {
        ArrayList<Expr> retList = node.getReturnArgList();
        ArrayList<IRExpr> IRRet = new ArrayList<>();
        for (Expr ret: retList){
            IRRet.add((IRExpr) ret.accept(this));
        }
        return new IRReturn(IRRet);
    }
    @Override
    public IRStmt visit(While node) {
        String lh = nxtLabel();
        String l1 = nxtLabel();
        String le = nxtLabel();
        IRStmt condStmt = booleanAsControlFlow(node.getGuard(),l1,le);
        IRStmt bodyStmt = (IRStmt) node.getStmt().accept(this);
        return new IRSeq(
                new IRLabel(lh),
                condStmt,
                new IRLabel(l1),
                bodyStmt,
                new IRJump(new IRName(lh)),
                new IRLabel(le));
    }
    @Override
    public IRStmt visit(DeclAssignStmt node) {
//        if (node.getDecl() instanceof UnderScore){
//        }
        IRNode left =  node.getDecl().accept(this); // just in case we need to initalize
        IRExpr right = (IRExpr) node.getExpression().accept(this);
        if (right instanceof IRCall) {
            return new IRMove(new IRTemp(node.getDecl().identifier.toString()),new IRTemp("_RV1"));
        }
        return new IRMove(new IRTemp(node.getDecl().identifier.toString()),right);
    }

    @Override
    public IRStmt visit(DeclNoAssignStmt node) {
        if (!(node.getDecl() instanceof  AnnotatedTypeDecl)){
            throw new InternalCompilerError("no assign can only be annotated");
        }
        AnnotatedTypeDecl atd = (AnnotatedTypeDecl) node.getDecl();
        return atd.accept(this);
        //Annotated Type Decl

        // ArrAccessDecl Can't

        // No Type Decl  Can't

        // UnderScore Can't
    }

    @Override
    public IRStmt visit(MultiDeclAssignStmt node) {
        return null;
    }

    @Override
    public IRStmt visit(Globdecl node) { // gonna have to be IRDATA
        return null;
    }

    @Override
    public IRStmt visit(Method node) {
        return null;
    }

    @Override
    public IRStmt visit(MultiGlobalDecl node) {
        return null;
    }

    @Override
    public IRStmt visit(AnnotatedTypeDecl node) { // could be IREXPR
        AnnotatedTypeDecl atd = node;
        if (atd.type.isArray()){
            if (atd.type.dimensions.allEmpty){ // random init is fine
                return new IRMove(new IRTemp(atd.identifier.toString()),new IRMem(new IRConst(0)));
            }else{
                throw new InternalCompilerError("Gotta create init array malloc thing");
            }
        }else if (atd.type.isBasic()){
            return new IRMove(new IRTemp(atd.identifier.toString()),new IRConst(0));
        }
        throw new InternalCompilerError("Annotated can only be array or basic");
    }

    @Override
    public IRExpr visit(ArrAccessDecl node) {
        return null;
    }

    @Override
    public IRExpr visit(NoTypeDecl node) {
        return new IRTemp(node.getIdentifier().toString());
    }

    @Override
    public IRExpr visit(UnderScore node) {
        return new IRTemp("_");
    }

    @Override
    public IRStmt visit(Use node) {
        return null;
    }

    @Override
    public IRCompUnit visit(Program node) {
        return null;
    }

//    @Override
//    public IRStmt visit(Type node) {
//        return null;
//    }

    private IRStmt booleanAsControlFlow(Expr e, String lt, String lf) {
        if (e instanceof BoolLiteral) { // C[true/false, t, f]  = JUMP(NAME(t/f))
            boolean val = ((BoolLiteral) e).boolVal;
            return new IRJump(new IRName(val ? lt : lf));
        } else if (e instanceof AndBinop){ // C[e1 & e2, t, f]  = SEQ(C[e1,l1,f],l1,C[e2,t,f])
            Expr e1 = ((AndBinop) e).getLeftExpr();
            Expr e2 = ((AndBinop) e).getRightExpr();
            String l1 = nxtLabel();
            IRStmt first = booleanAsControlFlow(e1,l1,lf);
            IRStmt second = booleanAsControlFlow(e2,lt,lf);
            return new IRSeq(first,new IRLabel(l1),second);
        }else if (e instanceof OrBinop){ // C[e1 | e2, t, f]  = SEQ(C[e1,t,l1],l1,C[e2,t,f])
            Expr e1 = ((OrBinop) e).getLeftExpr();
            Expr e2 = ((OrBinop) e).getRightExpr();
            String l1 = nxtLabel();
            IRStmt first = booleanAsControlFlow(e1,lt,l1);
            IRStmt second = booleanAsControlFlow(e2,lt,lf);
            return new IRSeq(first,new IRLabel(l1),second);
        }else if (e instanceof NotUnop){ // C[!e, t, f]  = C[e, f, t]
            return booleanAsControlFlow(e,lf,lt);
        }
        IRExpr cond = (IRExpr) e.accept(this);         // C[e, t, f]  = CJUMP(E[e], t, f)
        return new IRCJump(cond, lt, lf);
    }

    private String genABIFunc(Type funcType, Id funcName){
        if (funcType.getType() != Type.TypeCheckingType.FUNC){
            throw new Error("HOW ARE WE HERE");
        }
        String replaceName = funcName.toString().replaceAll("_","__");
        String inputABIName = genABIArr(funcType.inputTypes,true);
        String outputABIName = genABIArr(funcType.outputTypes,false);
        return "_I" + replaceName + outputABIName + inputABIName;
    }
    private String genABIArr(ArrayList<Type> arrTypes, boolean isInput){
        if (arrTypes.size() == 0){
            return isInput ? "": "p";
        }
        else if (arrTypes.size() == 1){
            return genABIType(arrTypes.get(0));
        }else{
            StringBuilder temp = new StringBuilder(isInput ? "" : "t" + arrTypes.size());
            for (Type t: arrTypes){
                temp.append(genABIType(t));
            }
            return temp.toString();
        }
    }
    private String genABIType(Type t){
        if (t.isBasic()){
            return (t.getType() == Type.TypeCheckingType.INT) ? "i": "b";
        }else if (t.isArray()){
            StringBuilder build = new StringBuilder();
            for (int i = 0 ; i< t.dimensions.getDim();i++){
                build.append("a");
            }
            build.append((t.getType() == Type.TypeCheckingType.INTARRAY) ? "i":"b");
            return build.toString();
        }else{
            throw new Error("WE SHOULD NOT BE IN GENTYPE");
        }
    }
    private IRNode recursiveMalloc(int index, Dimension d){
//        if (index == d.getDim()){
//            return new IRMove(,new IRConst(0));
//        }
        return null;

    }
}
