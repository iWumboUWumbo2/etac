package aar226_akc55_ayc62_ahl88.visitors;

import aar226_akc55_ayc62_ahl88.newast.Dimension;
import aar226_akc55_ayc62_ahl88.newast.Program;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.Use;
import aar226_akc55_ayc62_ahl88.newast.declarations.*;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class IRVisitor implements Visitor<IRNode>{

    private static final int WORD_BYTES = 8;
    private static final String OUT_OF_BOUNDS = "_xi_out_of_bounds";
    private int labelCnt;
    private int tempCnt;
    private final String compUnitName;
    private ArrayList<String> globalIds;
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
        IRSeq malloc_move1 = new IRSeq(new IRExp(alloc_call1),new IRMove(new IRTemp(t1), new IRTemp("_RV1")));
        // reg[t] <- call malloc
//        IRMove malloc_move1 = new IRMove(new IRTemp(t1), alloc_call1);

        List<IRStmt> seq_list1 = new ArrayList<>(List.of(length_to_l1, malloc_move1));

        for(int i = 0; i < n1; i++) {
            IRExpr ire1 = (IRExpr) values1.get(i).accept(this);
            IRMove move_elmnt1 = new IRMove(new IRMem(new IRBinOp(
                    IRBinOp.OpType.ADD,
                    new IRTemp(t1),
                    new IRConst(8*(i+1)))),
                    ire1 );
            seq_list1.add(move_elmnt1);
        }

        seq_list1.add(new IRMove(new IRTemp(t1),
                new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(t1), new IRConst(WORD_BYTES))));
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
        IRSeq malloc_move2 = new IRSeq(new IRExp(alloc_call2),new IRMove(new IRTemp(t2), new IRTemp("_RV1")));

        List<IRStmt> seq_list2 = new ArrayList<IRStmt>(List.of(length_to_l2, malloc_move2));

        for(int i = 0; i < n2; i++) {
            IRExpr ire2 = (IRExpr) values2.get(i).accept(this);
            IRMove move_elmnt2 = new IRMove(new IRMem(new IRBinOp(
                    IRBinOp.OpType.ADD,
                    new IRTemp(t2),
                    new IRConst(8*(i+1)))),
                    ire2 );
            seq_list2.add(move_elmnt2);
        }

        seq_list2.add(new IRMove(new IRTemp(t2),
                new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(t2), new IRConst(WORD_BYTES))));
        // END ALLOCATE ARR2

        String size_reg = nxtTemp();
        String t3 = nxtTemp();

        // R[size_reg] <= MEM[t1-8] + MEM[t2-8]
        IRExpr get_new_size = new IRBinOp(IRBinOp.OpType.ADD,
                new IRMem(new IRBinOp(
                        IRBinOp.OpType.SUB, new IRTemp(t1), new IRConst(8))),
                new IRMem(new IRBinOp(
                        IRBinOp.OpType.SUB, new IRTemp(t2), new IRConst(8))));

        IRMove get_size_move = new IRMove(new IRTemp(size_reg), get_new_size);

        // 8*n+8
        IRBinOp size = new IRBinOp(IRBinOp.OpType.ADD,
                new IRBinOp(IRBinOp.OpType.MUL,
                        new IRTemp(l2),
                        new IRConst(WORD_BYTES)),
                new IRConst(WORD_BYTES));

        // CALL(NAME(malloc), size)
        IRCall alloc_call = new IRCall(new IRName("_xi_alloc"), size);
        IRSeq malloc_move = new IRSeq(new IRExp(alloc_call),new IRMove(new IRTemp(t3), new IRTemp("_RV1")));

        List<IRStmt> seq_list3 = new ArrayList<IRStmt>(seq_list1);
        seq_list3.addAll(seq_list2);
        seq_list3.addAll(List.of(get_size_move, malloc_move));

        // MEM[8*(t3+i)] <= t1 + i
        for(int i = 0; i < n1; i++) {
            IRMove move_elmnt3 = new IRMove(new IRMem(
                    new IRBinOp(
                            IRBinOp.OpType.ADD,
                            new IRTemp(t3),
                            new IRConst(8*(i)))),
                    new IRBinOp(
                            IRBinOp.OpType.ADD,
                            new IRTemp(t1),
                            new IRConst(8*i)));
            seq_list3.add(move_elmnt3);
        }

        // MEM[8*(t3+n1)] <= t2 + i
        for(int i = 0; i < n2; i++) {
            IRMove move_elmnt3 = new IRMove(new IRMem(
                    new IRBinOp(
                            IRBinOp.OpType.ADD,
                            new IRTemp(t3),
                            new IRConst(8*(i+n1)))),
                    new IRBinOp(
                            IRBinOp.OpType.ADD,
                            new IRTemp(t2),
                            new IRConst(8*i)));
            seq_list3.add(move_elmnt3);
        }

        IRSeq ir_seq3 = new IRSeq(seq_list3);

        return new IRESeq(ir_seq3,
                new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(t3), new IRConst(WORD_BYTES)));
    }

    @Override
    public IRExpr visit(PlusBinop node) {

        // TODO FINISH PLUS ANGELA
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
                // ESEQ
                // SEQ -> ACCEPT E1 -> MOVE E1 to new TEMP -> ACCEPT E2 -> MOVE E2 to new TEMP ->
                // EXPR is the second ESEQ from plus arrays
//                return plusArrays((ArrayValueLiteral) e1, (ArrayValueLiteral) e2, ire1, ire2); // FIX
                return plusArrays((ArrayValueLiteral) e1, (ArrayValueLiteral) e2); // FIX
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
        return new IRESeq(new IRExp(new IRCall(func,paramListIR)),new IRTemp("_RV1"));
    }

    @Override
    public IRExpr visit(Id node) { // x = a; this is only a
        if (globalIds.contains(node.toString())){
            return new IRTemp("_" + node.toString());
        }
        return new IRTemp(node.toString());
    }

    @Override
    public IRExpr visit(ArrayValueLiteral node) { // Going to have to be DATA if String
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
        IRMove malloc_move = new IRMove(new IRTemp(t), new IRTemp("_RV1"));

        IRMove size_move = new IRMove(new IRMem(new IRTemp(t)), new IRTemp(l));

        List<IRStmt> seq_list = new ArrayList<>(List.of(length_to_l, new IRExp(alloc_call), malloc_move, size_move));

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
        return accessRecur(0,node.getIndicies(),arrIR);
    }
//

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
        // might need to do call stmt
        IRExpr right = (IRExpr) node.getExpression().accept(this);
        IRExpr exec = node.getExpression() instanceof FunctionCallExpr ?
                new IRESeq(new IRExp(right),new IRTemp("_RV1")): right;

        if (node.getDecl() instanceof AnnotatedTypeDecl){
            AnnotatedTypeDecl atd = (AnnotatedTypeDecl) node.getDecl();
            if (atd.type.isArray()){
                if (atd.type.dimensions.allEmpty){ // random init is fine
                    return new IRMove(new IRTemp(atd.identifier.toString()),exec);
                }else{
                    IRExpr iden = atd.getIdentifier().accept(this); // x:int[e1][e2][e3]
                    return initArrayDecl(0,atd.type.dimensions,iden); // passed in temp x
//                    throw new InternalCompilerError("Gotta create init array malloc thing");
                }
            }else if (atd.type.isBasic()){
                return new IRMove(new IRTemp(atd.identifier.toString()),exec);
            }
            throw new InternalCompilerError("Annotated can only be array or basic");
        }else if (node.getDecl() instanceof ArrAccessDecl){
            ArrAccessDecl aad = (ArrAccessDecl) node.getDecl();
            assert(aad.getIndices().size() >= 1);
            if (aad.getFuncParams() ==  null){ // a[e1][e2]
                IRExpr arrIdIR = aad.getIdentifier().accept(this);
                IRExpr memComponent = accessRecur(0,aad.getIndices(), arrIdIR);
                return new IRMove(memComponent,exec);
            }else{ // g1(e1,e2)[4][5]
                String funcName = genABIFunc(aad.getFunctionSig(),aad.getIdentifier());
                ArrayList<IRExpr> argsList = new ArrayList<>();
                for (Expr param: aad.getFuncParams()){
                    argsList.add((IRExpr) param.accept(this));
                }
                IRCall funcCall = new IRCall(new IRName(funcName),argsList);
                IRESeq sideEffects = new IRESeq(new IRExp(funcCall), new IRTemp("_RV1"));
                IRExpr memComponent = accessRecur(0,aad.getIndices(), sideEffects);
                return new IRMove(memComponent,exec);
            }// find a[e1][e2]
        }else if (node.getDecl() instanceof NoTypeDecl){
            return new IRMove(node.getDecl().identifier.accept(this),exec);
        }else if (node.getDecl() instanceof UnderScore){
            return new IRExp(exec);
        }
        throw new InternalCompilerError("NOT A DECL?");
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
//        List<IRNode> left = node.getDecls().stream().map(d -> d.accept(this)).toList();
//        node.getDecls().forEach(d -> d.accept(this));
        List<IRExpr> right = node.getExpressions().stream().map(expr -> expr.accept(this)).toList();
        ArrayList<IRExpr> exec = new ArrayList<>();
        ArrayList<Expr> exprs = node.getExpressions();
        ArrayList<IRStmt> order =new ArrayList<>();
        ArrayList<String> tempNames = new ArrayList<>();
        if (exprs.get(0) instanceof FunctionCallExpr) {
            assert (right.size() == 1);
            order.add(new IRExp(right.get(0)));
            for (int i = 0; i < node.getDecls().size(); i++) {
                String tempName = nxtTemp();
                IRMove curMove = new IRMove(new IRTemp(tempName), new IRTemp("_RV" + (i + 1)));
                order.add(curMove);
                tempNames.add(tempName);
            }
        }else{
            for (int i = 0; i< node.getDecls().size();i++){
                IRExpr e = right.get(i);
                String tempName = nxtTemp();
                IRMove curMove = new IRMove(new IRTemp(tempName),e);
                order.add(curMove);
                tempNames.add(tempName);
            }
        }
        for (int i = 0 ;i< node.getDecls().size();i++){ // need to check if global VAR
            String curTemp = tempNames.get(i);
            Decl d = node.getDecls().get(i);
            if (d instanceof AnnotatedTypeDecl atd){
                if (atd.type.isArray()){
                    if (atd.type.dimensions.allEmpty){ // random init is fine
                        order.add(new IRMove(new IRTemp(atd.identifier.toString()), new IRTemp(curTemp)));
                    }else{
                        IRExpr iden = atd.getIdentifier().accept(this); // x:int[e1][e2][e3]
                        order.add(initArrayDecl(0,atd.type.dimensions,iden)); // passed in temp x
//                            throw new InternalCompilerError("Gotta create init array malloc thing");
                    }
                }else if (atd.type.isBasic()){
                    order.add(new IRMove(new IRTemp(atd.identifier.toString()),new IRTemp(curTemp)));
                }else {
                    throw new InternalCompilerError("Annotated can only be array or basic");
                }
            }else if (d instanceof ArrAccessDecl aad){
                assert(aad.getIndices().size() >= 1);
                if (aad.getFuncParams() ==  null){ // a[e1][e2]
                    IRExpr arrIdIR = aad.getIdentifier().accept(this);
                    IRExpr memComponent = accessRecur(0,aad.getIndices(), arrIdIR);
                    order.add (new IRMove(memComponent,new IRTemp(curTemp)));
                }else{ // g1(e1,e2)[4][5]
                    String funcName = genABIFunc(aad.getFunctionSig(),aad.getIdentifier());
                    ArrayList<IRExpr> argsList = new ArrayList<>();
                    for (Expr param: aad.getFuncParams()){
                        argsList.add((IRExpr) param.accept(this));
                    }
                    IRCall funcCall = new IRCall(new IRName(funcName),argsList);
                    IRESeq sideEffects = new IRESeq(new IRExp(funcCall), new IRTemp("_RV1"));
                    IRExpr memComponent = accessRecur(0,aad.getIndices(), sideEffects);
                    order.add(new IRMove(memComponent,new IRTemp(curTemp)));
                }// find a[e1][e2]
            }else if (d instanceof NoTypeDecl){
                order.add(new IRMove(d.identifier.accept(this),new IRTemp(curTemp))); // might need to check for Globals
            }else if (d instanceof UnderScore){
                order.add(new IRExp(new IRTemp(curTemp)));
            }else {
                throw new InternalCompilerError("NOT A DECL?");
            }
        }
        return new IRSeq(order);
    }

    @Override
    public IRStmt visit(Globdecl node) { // gonna have to be IRDATA
        // Don't visit create function that adds to Global MAP
        return null;
    }

    @Override
    public IRFuncDecl visit(Method node) {
        ArrayList<IRStmt> stmtList = new ArrayList<>();
        // MOVE ARGS INTO PARAMS
        for (int i = 0;i < node.getDecls().size();i++){
            AnnotatedTypeDecl atd = node.getDecls().get(i);
            stmtList.add(new IRMove(new IRTemp(atd.identifier.toString()),new IRTemp("_ARG" + (i+1))));
        }

        // EXECUTE BLOCK
        stmtList.add(node.getBlock().accept(this)); // might need to move return inside
        // ADD RET IF NEEDED
        if (node.getBlock().getNodeType().getType() == Type.TypeCheckingType.UNIT){
            stmtList.add(new IRReturn());
        }

        String abiName = genABIFunc(node.getFunctionSig(), node.getId());
        // CREATE NODE
        return new IRFuncDecl(abiName, new IRSeq(stmtList));
    }

    @Override
    public IRStmt visit(MultiGlobalDecl node) {
    // Don't visit create function that adds to Global MAP
        return null;
    }

    @Override // Visit only on No Assign
    public IRStmt visit(AnnotatedTypeDecl node) { // could be IREXPR
        AnnotatedTypeDecl atd = node;
        if (atd.type.isArray()){
            if (atd.type.dimensions.allEmpty){ // random init is fine
                return new IRMove(new IRTemp(atd.identifier.toString()),new IRConst(0));
            }else{
                IRExpr iden = atd.getIdentifier().accept(this); // x:int[e1][e2][e3]
                return initArrayDecl(0,atd.type.dimensions,iden); // passed in temp x
            }
        }else if (atd.type.isBasic()){
            return new IRMove(new IRTemp(atd.identifier.toString()),new IRConst(0));
        }
        throw new InternalCompilerError("Annotated can only be array or basic");
    }

    @Override
    public IRExpr visit(ArrAccessDecl node) {// no need to visit
        return null;
    }

    @Override
    public IRExpr visit(NoTypeDecl node) {// no need to visit
        return new IRTemp(node.getIdentifier().toString());
    }

    @Override
    public IRExpr visit(UnderScore node) {// no need to visit
        return new IRTemp("_");
    }

    @Override
    public IRStmt visit(Use node) { // no need to visit
        return null;
    }

    @Override
    public IRCompUnit visit(Program node) {

        // Arun TODO
        // Create  Comp Unit
        IRCompUnit compUnit = new IRCompUnit(compUnitName);
        globalIds = node.getGlobalsID();
        node.getDefinitions().forEach(definition -> {
            // Add Single Global Decls to the DATA MAP USE FUNC BELOW
            if (definition instanceof Globdecl) {
                compUnit.appendData(initSingleGlobal((Globdecl) definition));

            }
            // Add multi global decls to the DATA map use the Func Below
            else if (definition instanceof MultiGlobalDecl) {
                initMultiGlobal((MultiGlobalDecl) definition).forEach(compUnit::appendData);
            }
            // Add Function Decls and Name
            else if (definition instanceof Method) {
                compUnit.appendFunc(((Method) definition).accept(this));
            }
        });

        // Return Comp Unit

        return compUnit;
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
    private IRStmt initArrayDecl(int ind, Dimension d, IRExpr curHead){ // this is for a:int[4][3][] etc
        // a:int[e1][e2][][]
        if (ind == d.getDim() || d.getIndices().get(ind) == null){
            return new IRMove(new IRMem(curHead),new IRConst(0)); // base case x: int[] x <- random val
        }

        Expr curExp = d.getIndices().get(ind);
        IRExpr irExp = curExp.accept(this);

        String tn = nxtTemp();
        String tm = nxtTemp();

        // reg[l] <- length
        IRMove length_to_l1 = new IRMove(new IRTemp(tn), irExp);

        // 8*n+8
        IRBinOp size1 = new IRBinOp(IRBinOp.OpType.ADD,
                new IRBinOp(IRBinOp.OpType.MUL,
                        new IRTemp(tn),
                        new IRConst(WORD_BYTES)),
                new IRConst(WORD_BYTES));

        // call alloc and move RV1 into val
        IRCall alloc_call1 = new IRCall(new IRName("_xi_alloc"), size1);
        IRSeq malloc_move1 = new IRSeq(new IRExp(alloc_call1),new IRMove(new IRTemp(tm), new IRTemp("_RV1")));

        // move len into -1
        IRMove move_len = new IRMove(new IRMem(new IRTemp(tm)),new IRTemp(tn));

        // increment pointer to head
        IRBinOp add_8 = new IRBinOp(IRBinOp.OpType.ADD,new IRTemp(tm), new IRConst(WORD_BYTES));
        IRMove inc_pointer_to_head = new IRMove(curHead,add_8);
        // do all the top level shit first
        IRSeq top_level_Order = new IRSeq(length_to_l1,malloc_move1,move_len,inc_pointer_to_head);


        // now time to recrusively alloc
        String lh = nxtLabel();
        String l1 = nxtLabel();
        String le = nxtLabel();
        String counter = nxtTemp();
        IRBinOp guard = new IRBinOp(IRBinOp.OpType.LT,new IRTemp(counter), irExp);
        // set counter = 0;
        IRMove set0Coutner = new IRMove(new IRTemp(counter), new IRConst(0));
        IRLabel whileHead = new IRLabel(lh);
        // check if counter < irExp
        IRCJump loopCheck = new IRCJump(guard,l1,le);
        IRLabel whileBody = new IRLabel(l1);
        // create memory location for head of new array
        IRMem memHead = new IRMem(new IRBinOp(IRBinOp.OpType.ADD,
                curHead,
                new IRBinOp(IRBinOp.OpType.MUL,
                    new IRTemp(counter),
                    new IRConst(WORD_BYTES))));
        // if we were at int[4][5] we now are in the "5" after recur executes after 5 it will be in "nothing" which means base case
        IRStmt recur = initArrayDecl(ind+1, d, memHead);
        // increment counter
        IRMove inc = new IRMove(new IRTemp(counter),new IRBinOp(IRBinOp.OpType.ADD,new IRTemp(counter), new IRConst(1)));
        // jump back to loop head
        IRJump go_back_to_head = new IRJump(new IRName(lh));
        IRLabel afterLoop = new IRLabel(le);

        IRSeq loopComponent = new IRSeq(set0Coutner,whileHead,loopCheck,whileBody,recur,inc,go_back_to_head,afterLoop);

        return new IRSeq(top_level_Order,loopComponent);
        // malloc 4

        // for each of those Elements Recursively


        // Create IR STMT recursively

        // Malloc Then Move

        // Go through all elements and Malloc and Move Again

    }

    private IRData initSingleGlobal(Globdecl node){
        Expr e = node.getValue();
        AnnotatedTypeDecl d = node.getDecl();
        IRData irdata;
        long[] data;
        String name = "_" + d.getIdentifier();

        if (e == null) {
            irdata = new IRData(name, new long[]{});
            return irdata;
        }

        if (e.getNodeType().getType() == Type.TypeCheckingType.INT) {
            data = new long[]{((IntLiteral) e).getLong()};
            irdata = new IRData(name, data);
        } else if (e.getNodeType().getType() == Type.TypeCheckingType.BOOL) {
            Boolean b = ((BoolLiteral) e).getBoolVal();
            if (b) {
                data = new long[]{1};
            } else {
                data = new long[]{0};
            }
            irdata = new IRData(name, data);
        } else {
            throw new InternalCompilerError("Unable to global assign this type.");
        }
        return irdata;
        // Check if right side is null or not for initalized Value
        // FOLLOW ABI For Naming Conventions
        // Put Data into Single Global Decl
        // Return IR DATA
    }

    private ArrayList<IRData> initMultiGlobal(MultiGlobalDecl node){

        // Make sure each one lines up correctly

        // FOLLOW ABI For Naming Conventions

        // PUT DATA INTO Building GLOBAL DECLS

        // Return Multiple Global Decls

        // HELP ANGELA After finishing This.

        ArrayList<AnnotatedTypeDecl> decls = node.getDecls();
        ArrayList<Expr> exprs = node.getExpressions();

        ArrayList<IRData> irDataList = new ArrayList<>(decls.size());
        for (int i = 0; i < decls.size(); i++) {
            Globdecl glob = new Globdecl(decls.get(i), exprs.get(i), -1 ,-1);
            irDataList.set(i, initSingleGlobal(glob));
        }

        return irDataList;
    }
    private IRExpr accessRecur(int ind, ArrayList<Expr> indexes, IRExpr expr) {
        if (ind == indexes.size()) {
            return expr;
        }
        IRExpr curInd = indexes.get(ind).accept(this);
        String ta = nxtTemp();
        String ti = nxtTemp();
        String lok = nxtLabel();
        IRESeq sol = new IRESeq( // 1d array need loop for further
                new IRSeq(
                        new IRMove(new IRTemp(ta), expr),
                        new IRMove(new IRTemp(ti), curInd),
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
        return accessRecur(ind + 1, indexes, sol);
    }


}
