package aar226_akc55_ayc62_ahl88.visitors;

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

import java.util.ArrayList;

public class IRVisitor implements Visitor<IRNode>{

    private static final int WORD_BYTES = 8;
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
    public IRNode visit(IntOutBinop node) {
//        DIVIDE, HIGHMULT, MINUS, MODULO, TIMES
        Expr e1 = node.getLeftExpr();
        Expr e2 = node.getRightExpr();

        IRExpr ire1 = (IRExpr) e1.accept(this);
        IRExpr ire2 = (IRExpr) e2.accept(this);
        IRBinOp.OpType op = node.getOpType();

        return new IRBinOp(op, ire1, ire2);
    }

    @Override
    public IRNode visit(PlusBinop node) {
        IRExpr l = (IRExpr) node.getLeftExpr().accept(this);
        IRExpr r = (IRExpr) node.getRightExpr().accept(this);

        return new IRBinOp(IRBinOp.OpType.ADD, l, r);

        // if both ints, return irbinop
        // if one unknown and other int, return int
        // if both arrays, add
        // if one unknown array and other array, return array
    }

    @Override
    public IRNode visit(IntegerComparisonBinop node) {
//        < , <= , > , >=
        Expr e1 = node.getLeftExpr();
        Expr e2 = node.getRightExpr();

        IRExpr ire1 = (IRExpr) e1.accept(this);
        IRExpr ire2 = (IRExpr) e2.accept(this);
        IRBinOp.OpType op = node.getOpType();

        return new IRBinOp(op, ire1, ire2);
    }

    @Override
    public IRNode visit(EquivalenceBinop node) {
        IRExpr l = (IRExpr) node.getLeftExpr().accept(this);
        IRExpr r = (IRExpr) node.getRightExpr().accept(this);

        IRBinOp.OpType op = node.getOpType();

        return new IRBinOp(op, l, r);
    }

    @Override
    public IRNode visit(LogicalBinop node) {
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
    public IRNode visit(NotUnop node) {
        IRExpr ire = (IRExpr) node.accept(this);
        return new IRBinOp(IRBinOp.OpType.XOR, new IRConst(1), ire);
    }

    @Override
    public IRNode visit(IntegerNegExpr node) {
        IRExpr ire = (IRExpr) node.accept(this);
        return new IRBinOp(IRBinOp.OpType.SUB, new IRConst(0), ire);
    }

    @Override
    public IRNode visit(BoolLiteral node) {
        return new IRConst(node.boolVal ? 1 : 0);
    }

    @Override
    public IRNode visit(IntLiteral node) {
        return new IRConst(node.number);
    }

    @Override
    public IRNode visit(Length node) {
        IRMem mem = new IRMem(new IRBinOp(IRBinOp.OpType.ADD, (IRExpr) node.getArg().accept(this), new IRConst(-WORD_BYTES)));
        IRMove move = new IRMove(new IRTemp(nxtTemp()), mem);
        return move;
    }

    @Override
    public IRNode visit(FunctionCallExpr node) {
        return null;
    }

    @Override
    public IRNode visit(Id node) {
        return null;
    }

    @Override
    public IRNode visit(ArrayValueLiteral node) {
        return null;
    }
    @Override
    public IRNode visit(ArrayAccessExpr node) {
        return null;
    }

    @Override
    public IRNode visit(Block node) {
        ArrayList<Stmt> statements = node.getStatementList();
        ArrayList<IRStmt> IRstmtList = new ArrayList<IRStmt>();
        for (Stmt stmt: statements) {
            IRstmtList.add((IRStmt) stmt.accept(this));
        }
        return new IRSeq(IRstmtList);
    }

    @Override
    public IRNode visit(IfElse node) {
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
    public IRNode visit(IfOnly node) {
        String l1 = nxtLabel();
        String l2 = nxtLabel();
        IRStmt condStmt = booleanAsControlFlow(node.guard,l1,l2);
        IRStmt statement = (IRStmt) node.ifState.accept(this);
        return new IRSeq(condStmt,new IRLabel(l1),statement, new IRLabel(l2));
    }

    @Override
    public IRNode visit(ProcedureCall node) {
        return null;
    }

    @Override
    public IRNode visit(Return node) {
        ArrayList<Expr> retList = node.getReturnArgList();
        ArrayList<IRExpr> IRRet = new ArrayList<>();
        for (Expr ret: retList){
            IRRet.add((IRExpr) ret.accept(this));
        }
        return new IRReturn(IRRet);
    }
    @Override
    public IRNode visit(While node) {
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
    public IRNode visit(DeclAssignStmt node) {
        return null;
    }

    @Override
    public IRNode visit(DeclNoAssignStmt node) {
        return null;
    }

    @Override
    public IRNode visit(MultiDeclAssignStmt node) {
        return null;
    }

    @Override
    public IRNode visit(Globdecl node) {
        return null;
    }

    @Override
    public IRNode visit(Method node) {
        return null;
    }

    @Override
    public IRNode visit(MultiGlobalDecl node) {
        return null;
    }

    @Override
    public IRNode visit(AnnotatedTypeDecl node) {
        return null;
    }

    @Override
    public IRNode visit(ArrAccessDecl node) {
        return null;
    }

    @Override
    public IRNode visit(NoTypeDecl node) {
        return null;
    }

    @Override
    public IRNode visit(UnderScore node) {
        return null;
    }

    @Override
    public IRNode visit(Use node) {
        return null;
    }

    @Override
    public IRNode visit(Program node) {
        return null;
    }

    @Override
    public IRNode visit(Type node) {
        return null;
    }

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
}
