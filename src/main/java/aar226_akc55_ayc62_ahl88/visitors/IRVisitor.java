package aar226_akc55_ayc62_ahl88.visitors;

import aar226_akc55_ayc62_ahl88.Main;
import aar226_akc55_ayc62_ahl88.OptimizationTypes;
import aar226_akc55_ayc62_ahl88.asm.Expressions.ASMTempExpr;
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
import org.apache.commons.text.StringEscapeUtils;

import javax.naming.Name;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class IRVisitor implements Visitor<IRNode>{
    private static final int WORD_BYTES = 8;
    private static final String OUT_OF_BOUNDS = "_eta_out_of_bounds";
    private int labelCnt;
    private int tempCnt;
    private int stringCnt;
    private final String compUnitName;
    private ArrayList<String> globalIds;
    private boolean constantFold;
    private ArrayList<IRData> string_consts;
    public IRVisitor(String name) {
        labelCnt = 0;
        tempCnt = 0;
        stringCnt = 1;
        compUnitName = name;
        string_consts = new ArrayList<>();
        constantFold = Main.opts.isSet(OptimizationTypes.CONSTANT_FOLDING);
    }
    private String nxtLabel() {
        return String.format("l%d", (labelCnt++));
    }
    private String nxtTemp() {
        return String.format("t%d", (tempCnt++));
    }
    private String nxtString() {
        return String.format("string_const%d", (stringCnt++));
    }

    @Override
    public IRExpr visit(IntOutBinop node) {
//        DIVIDE, HIGHMULT, MINUS, MODULO, TIMES
        Expr e1 = node.getLeftExpr();
        Expr e2 = node.getRightExpr();

        IRExpr ire1 = e1.accept(this);
        IRExpr ire2 = e2.accept(this);
        IRBinOp.OpType op = node.getOpType();

        if (constantFold && ire1.isConstant() && ire2.isConstant()) {
            long e1int = ire1.constant();
            long e2int = ire2.constant();

            switch (op) {
                case DIV: if (e2int != 0) {
                    return new IRConst(e1int / e2int);
                }
                    throw new Error("DIVIDE BY ZERO");
                case HMUL: BigInteger a = BigInteger.valueOf(e1int).multiply(BigInteger.valueOf(e2int));
                            return new IRConst(a.shiftRight(64).longValue());
                case SUB: return new IRConst(e1int - e2int);
                case MOD: if (e2int != 0) {
                    return new IRConst(e1int % e2int);
                }
                    throw new Error("DIVIDE BY ZERO");
                case MUL: return new IRConst(e1int * e2int);
                default: throw new Error("NOT INTEGER ARITHMETIC BINOP");
            }
        }
        return new IRBinOp(op, ire1, ire2);
    }

    private IRExpr plusArrays(IRExpr ire1, IRExpr ire2){
        String size1 = nxtTemp();
        String size2 = nxtTemp();
        String size3 = nxtTemp();

        String savedire1 = nxtTemp();
        String savedire2 = nxtTemp();
        IRMove calc1 = new IRMove(new IRTemp(savedire1),ire1);
        IRMove calc2 = new IRMove(new IRTemp(savedire2),ire2);
        IRMove get_size1 = new IRMove(new IRTemp(size1), new IRMem( //store size1
                new IRBinOp(IRBinOp.OpType.SUB, new IRTemp(savedire1), new IRConst(WORD_BYTES))));
        IRMove get_size2 = new IRMove(new IRTemp(size2), new IRMem( // store size2
                new IRBinOp(IRBinOp.OpType.SUB,new IRTemp(savedire2), new IRConst(WORD_BYTES))));
        IRMove get_size3 = new IRMove(new IRTemp(size3), // size3 <= size1 + size2
                new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(size1), new IRTemp(size2)));

        // 8*n+8
        IRBinOp malloc_size = new IRBinOp(IRBinOp.OpType.ADD,
                new IRBinOp(IRBinOp.OpType.MUL,
                        new IRTemp(size3),
                        new IRConst(WORD_BYTES)),
                new IRConst(WORD_BYTES));

        String head_pointer = nxtTemp();
        IRCallStmt alloc_call = new IRCallStmt(new IRName("_eta_alloc"), 1L, malloc_size);
//        IRMove malloc_move = new IRMove(new IRTemp(head_pointer),alloc_call);
        IRSeq malloc_move = new IRSeq(alloc_call,new IRMove(new IRTemp(head_pointer), new IRTemp("_RV1")));

        IRMove move_len = new IRMove(new IRMem(new IRTemp(head_pointer)),new IRTemp(size3));
        // move len into -1

        // increment pointer to head
        IRBinOp add_8 = new IRBinOp(IRBinOp.OpType.ADD,new IRTemp(head_pointer), new IRConst(WORD_BYTES));
        IRMove inc_pointer_to_head = new IRMove(new IRTemp(head_pointer),add_8);
        // do all the top level shit first
        IRSeq top_level_Order = new IRSeq(calc1,calc2,get_size1,get_size2,get_size3,malloc_move,move_len,inc_pointer_to_head);

        /* START LOOP 1 */
        // now time to recrusively alloc
        String lh = nxtLabel();
        String l1 = nxtLabel();
        String le = nxtLabel();
        String counter = nxtTemp();
        IRBinOp guard = new IRBinOp(IRBinOp.OpType.LT,new IRTemp(counter), new IRTemp(size1));
        // set counter = 0;
        IRMove set0Counter = new IRMove(new IRTemp(counter), new IRConst(0));
        IRLabel whileHead = new IRLabel(lh);
        // check if counter < irExp
        IRCJump loopCheck = new IRCJump(guard,l1,le);
        IRLabel whileBody = new IRLabel(l1);
        // create memory location for destination
        IRMem leftMem = new IRMem(
                new IRBinOp(IRBinOp.OpType.ADD,
                        new IRBinOp(IRBinOp.OpType.MUL,new IRConst(WORD_BYTES),new IRTemp(counter)),
                        new IRTemp(head_pointer))
        );

        IRMem rightMem = new IRMem(
                new IRBinOp(IRBinOp.OpType.ADD,
                        new IRBinOp(IRBinOp.OpType.MUL,new IRConst(WORD_BYTES),new IRTemp(counter)),
                        new IRTemp(savedire1))
        );

        IRMove load_element = new IRMove(leftMem, rightMem);
        IRMove inc_counter = new IRMove(new IRTemp(counter),
                new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(counter), new IRConst(1)));
        // jump back to loop head
        IRJump go_back_to_head = new IRJump(new IRName(lh));
        IRLabel afterLoop = new IRLabel(le);

        IRSeq loopComponent1 = new IRSeq(set0Counter,whileHead,loopCheck,whileBody,
                load_element,inc_counter,go_back_to_head,afterLoop);
        /* END LOOP 1 */

        /* START LOOP 2 */
        String lh2 = nxtLabel();
        String l12 = nxtLabel();
        String le2 = nxtLabel();
        String counter2 = nxtTemp();
        IRBinOp guard2 = new IRBinOp(IRBinOp.OpType.LT,new IRTemp(counter2), new IRTemp(size2));
        // set counter = 0;
        IRMove set0Counter2 = new IRMove(new IRTemp(counter2), new IRConst(0));
        IRLabel whileHead2 = new IRLabel(lh2);
        // check if counter < irExp
        IRCJump loopCheck2 = new IRCJump(guard2,l12,le2);
        IRLabel whileBody2 = new IRLabel(l12);
        // create memory location for destination
        IRMem leftMem2 = new IRMem(
                new IRBinOp(IRBinOp.OpType.ADD,
                        new IRBinOp(IRBinOp.OpType.MUL,new IRConst(WORD_BYTES),
                                new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(counter2), new IRTemp(size1))),
                        new IRTemp(head_pointer))
        );

        IRMem rightMem2 = new IRMem(
                new IRBinOp(IRBinOp.OpType.ADD,
                        new IRBinOp(IRBinOp.OpType.MUL,new IRConst(WORD_BYTES),new IRTemp(counter2)),
                        new IRTemp(savedire2))
        );

        IRMove load_element2 = new IRMove(leftMem2, rightMem2);
        IRMove inc_counter2 = new IRMove(new IRTemp(counter2),
                new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(counter2), new IRConst(1)));
        // jump back to loop head
        IRJump go_back_to_head2 = new IRJump(new IRName(lh2));
        IRLabel afterLoop2 = new IRLabel(le2);

        IRSeq loopComponent2 = new IRSeq(set0Counter2,whileHead2,loopCheck2,whileBody2,
                load_element2,inc_counter2,go_back_to_head2,afterLoop2);
        /* END LOOP 2 */


        IRSeq final_seq = new IRSeq(top_level_Order,loopComponent1, loopComponent2);
        return new IRESeq(final_seq, new IRTemp(head_pointer));
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
            } else {
                // ESEQ
                // SEQ -> ACCEPT E1 -> MOVE E1 to new TEMP -> ACCEPT E2 -> MOVE E2 to new TEMP ->
                // EXPR is the second ESEQ from plus arrays
                return plusArrays(ire1,ire2);
            }
        }

        // if both ints, return irbinop
        // if one unknown and other int, return int
        if (constantFold && ire1.isConstant() && ire2.isConstant()) {
            return new IRConst(ire1.constant() + ire2.constant());
        } else {
                return new IRBinOp(IRBinOp.OpType.ADD, ire1, ire2);
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

        if (constantFold && ire1.isConstant() && ire2.isConstant()) {
            long e1int = ire1.constant();
            long e2int = ire2.constant();

            return switch (op) {
                case LT -> new IRConst(e1int < e2int ? 1 : 0);
                case LEQ -> new IRConst(e1int <= e2int ? 1 : 0);
                case GT -> new IRConst(e1int > e2int ? 1 : 0);
                case GEQ -> new IRConst(e1int >= e2int ? 1 : 0);
                default -> throw new Error("NOT INTEGER COMPARISON BINOP");
            };
        }
        return new IRBinOp(op, ire1, ire2);
    }

    @Override
    public IRExpr visit(EquivalenceBinop node) {
        Expr e1 = node.getLeftExpr();
        Expr e2 = node.getRightExpr();

        IRExpr ire1 = e1.accept(this);
        IRExpr ire2 = e2.accept(this);
        IRBinOp.OpType op = node.getOpType();

        if (constantFold && ire1.isConstant() && ire2.isConstant()) {
            long e1int = ire1.constant();
            long e2int = ire2.constant();
            return switch (op) {
                case EQ -> new IRConst(e1int == e2int ? 1 : 0);
                case NEQ -> new IRConst(e1int != e2int ? 1 : 0);
                default -> throw new Error("NOT EQUIVALENCE COMPARISON BINOP");
            };
        }
        return new IRBinOp(op, ire1, ire2);
    }

    @Override
    public IRExpr visit(LogicalBinop node) {
        String l1 = nxtLabel();
        String l2 = nxtLabel();
        String lend = nxtLabel();
        String x = nxtTemp();
        Expr e1 = node.getLeftExpr();
        Expr e2 = node.getRightExpr();
        IRExpr ire1 = e1.accept(this);
        IRExpr ire2 = e2.accept(this);

        if (constantFold && ire1.isConstant() && ire2.isConstant()) {
            return switch (node.getBinopType()) {
                case AND -> new IRConst((ire1.constant() == 1)  && (ire2.constant() == 1)  ? 1 : 0);
                case OR -> new IRConst((ire1.constant() == 1)  || (ire2.constant() == 1)  ? 1 : 0);
                default -> throw new Error("NOT LOGICAL BINOP");
            };
        }

        return switch (node.getBinopType()) {
            case AND -> new IRESeq(new IRSeq(new IRMove(new IRTemp(x), new IRConst(0)),
                    new IRCJump(ire1, l1, lend),
                    new IRLabel(l1), new IRCJump(ire2, l2, lend),
                    new IRLabel(l2), new IRMove(new IRTemp(x), new IRConst(1)),
                    new IRLabel(lend)),
                    new IRTemp(x));
            case OR -> new IRESeq(new IRSeq(new IRMove(new IRTemp(x), new IRConst(1)),
                    new IRCJump(ire1, lend, l1),
                    new IRLabel(l1), new IRCJump(ire2, lend, l2),
                    new IRLabel(l2), new IRMove(new IRTemp(x), new IRConst(0)),
                    new IRLabel(lend)),
                    new IRTemp(x));
            default -> throw new Error("NOT LOGICAL BINOP");
        };
    }

    @Override
    public IRExpr visit(NotUnop node) {
        IRExpr ire = node.getE().accept(this);

        if (constantFold && ire.isConstant()) {
            return new IRConst(1-ire.constant());
        }
        return new IRBinOp(IRBinOp.OpType.XOR, new IRConst(1), ire);
    }

    @Override
    public IRExpr visit(IntegerNegExpr node) {
        IRExpr ire = node.getE().accept(this);
        if (constantFold && ire.isConstant()) {
            return new IRConst(-1 * ire.constant());
        }
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
        IRMem mem = new IRMem(new IRBinOp(IRBinOp.OpType.SUB, node.getArg().accept(this), new IRConst(WORD_BYTES)));
        IRMove move = new IRMove(new IRTemp(x), mem);
        return new IRESeq(move, new IRTemp(x));
    }

    @Override
    public IRExpr visit(FunctionCallExpr node) {
        IRName func = new IRName(genABIFunc(node.getFunctionSig(),node.getId()));
        ArrayList<IRExpr> paramListIR = new ArrayList<>();
        for (Expr param: node.getArgs()){
            paramListIR.add(param.accept(this));
        }
        long num = node.getFunctionSig().outputTypes.size();
        return new IRESeq(new IRCallStmt(func,num,paramListIR),new IRTemp("_RV1"));
//        return new IRESeq(new IRExp(new IRCall(func,paramListIR)),new IRTemp("_RV1"));
    }

    @Override
    public IRExpr visit(Id node) { // x = andy; this is only a
        if (globalIds.contains(node.toString())){
            return new IRMem(new IRName( "_" + node.toString()));
        }
        return new IRTemp(node.toString());
    }

    @Override
    public IRExpr visit(ArrayValueLiteral node) { // Going to have to be DATA if String

        if (node.getRaw() != null){ // it is a string
            String stringName = nxtString();
            long[] res  = new long[node.getValues().size()+1];
            String escapeString  = StringEscapeUtils.unescapeJava(node.getRaw());
            res[0] = escapeString.length();
            for (int i = 0; i< escapeString.length();i++){
                char c = escapeString.charAt(i);
                res[i+1] = (int) c;
            }
            IRData str =  new IRData(stringName,res); // we never use lmao
            string_consts.add(str);
        }
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
        IRCallStmt alloc_call = new IRCallStmt(new IRName("_eta_alloc"),1L, size);

        // reg[t] <- call malloc
        IRMove malloc_move = new IRMove(new IRTemp(t), new IRTemp("_RV1"));

        IRMove size_move = new IRMove(new IRMem(new IRTemp(t)), new IRTemp(l));

        List<IRStmt> seq_list = new ArrayList<>(List.of(length_to_l, alloc_call, malloc_move, size_move));

        for(int i = 0; i < n; i++) {
            IRExpr ire = values.get(i).accept(this);
            IRMove move_elmnt = new IRMove(new IRMem(new IRBinOp(
                    IRBinOp.OpType.ADD,
                    new IRTemp(t),
                    new IRConst(8L*(i+1)))),
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
        ArrayList<IRStmt> IRstmtList = new ArrayList<>();
        for (Stmt stmt: statements) {
            IRstmtList.add(stmt.accept(this));
        }
        return new IRSeq(IRstmtList);
    }

    @Override
    public IRStmt visit(IfElse node) {
        IRStmt iFStatement = node.getIfState().accept(this);
        IRStmt elseStatement = node.getElseState().accept(this);
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
        IRStmt statement = node.ifState.accept(this);
        return new IRSeq(condStmt,new IRLabel(l1),statement, new IRLabel(l2));
    }

    @Override
    public IRStmt visit(ProcedureCall node) {
        IRName func = new IRName(genABIFunc(node.getFunctionSig(),node.getIdentifier()));
        ArrayList<IRExpr> paramListIR = new ArrayList<>();
        for (Expr param: node.getParamList()){
            paramListIR.add(param.accept(this));
        }
        return new IRCallStmt(func,1L,paramListIR);
    }

    @Override
    public IRStmt visit(Return node) {
        ArrayList<Expr> retList = node.getReturnArgList();
        ArrayList<IRExpr> IRRet = new ArrayList<>();
        for (Expr ret: retList){
            IRRet.add(ret.accept(this));
        }
        return new IRReturn(IRRet);
    }
    @Override
    public IRStmt visit(While node) {
        String lh = nxtLabel();
        String l1 = nxtLabel();
        String le = nxtLabel();
        IRStmt condStmt = booleanAsControlFlow(node.getGuard(),l1,le);
        IRStmt bodyStmt = node.getStmt().accept(this);
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
        // might need to do call stmt
        IRExpr right = node.getExpression().accept(this);
        IRExpr exec = node.getExpression() instanceof FunctionCallExpr ?
                new IRESeq(new IRExp(right),new IRTemp("_RV1")): right;

        if (node.getDecl() instanceof AnnotatedTypeDecl atd){
            if (atd.type.isArray()){
                if (atd.type.dimensions.allEmpty){ // random init is fine
                    return new IRMove(new IRTemp(atd.identifier.toString()),exec);
                }else{
                    IRExpr iden = atd.getIdentifier().accept(this); // x:int[e1][e2][e3]
                    ArrayList<String> dimTemps = new ArrayList<>();
                    ArrayList<IRStmt> dimValues = new ArrayList<>();
                    for (Expr curExpr: atd.type.dimensions.indices){
                        if (curExpr != null) {
                            String tempVal = nxtTemp();
                            IRExpr irExp = curExpr.accept(this);
                            IRMove saveExpr = new IRMove(new IRTemp(tempVal), irExp);
                            dimValues.add(saveExpr);
                            dimTemps.add(tempVal);
                        }else{
                            dimTemps.add(null);
                        }
                    }
                    ArrayList<IRStmt> initSequence = new ArrayList<>();
                    initSequence.addAll(dimValues); // add a[foo(2,b)] add the side effects from the foos
                    initSequence.add(initArrayDecl(0,dimTemps, iden)); // actually allocate
                    return new IRSeq(initSequence); // passed in temp x
//                    throw new InternalCompilerError("Gotta create init array malloc thing");
                }
            }else if (atd.type.isBasic()){
                return new IRMove(new IRTemp(atd.identifier.toString()),exec);
            }
            throw new InternalCompilerError("Annotated can only be array or basic");
        }else if (node.getDecl() instanceof ArrAccessDecl aad){
            assert(aad.getIndices().size() >= 1);
            if (aad.getFuncParams() ==  null){ // a[e1][e2]
                IRExpr arrIdIR = aad.getIdentifier().accept(this);
                IRExpr memComponent = accessRecur(0,aad.getIndices(), arrIdIR);
                return new IRMove(memComponent,exec);
            }else{ // g1(e1,e2)[4][5]
                String funcName = genABIFunc(aad.getFunctionSig(),aad.getIdentifier());
                ArrayList<IRExpr> argsList = new ArrayList<>();
                for (Expr param: aad.getFuncParams()){
                    argsList.add(param.accept(this));
                }
                IRCallStmt funcCall = new IRCallStmt(new IRName(funcName),1L,argsList);
                IRESeq sideEffects = new IRESeq(funcCall, new IRTemp("_RV1"));
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
        if (!(node.getDecl() instanceof AnnotatedTypeDecl atd)){
            throw new InternalCompilerError("no assign can only be annotated");
        }
        return atd.accept(this);
        //Annotated Type Decl

        // ArrAccessDecl Can't

        // No Type Decl  Can't

        // UnderScore Can't
    }

    @Override
    public IRStmt visit(MultiDeclAssignStmt node) {
        List<IRExpr> right = node.getExpressions().stream().map(expr -> expr.accept(this)).toList();
        ArrayList<Expr> exprs = node.getExpressions();
        ArrayList<IRStmt> order =new ArrayList<>();
        ArrayList<String> tempNames = new ArrayList<>();
        if (exprs.get(0) instanceof FunctionCallExpr && exprs.size() == 1) {
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
                        ArrayList<String> dimTemps = new ArrayList<>();
                        ArrayList<IRStmt> dimValues = new ArrayList<>();
                        for (Expr curExpr: atd.type.dimensions.indices){
                            if (curExpr != null) {
                                String tempVal = nxtTemp();
                                IRExpr irExp = curExpr.accept(this);
                                IRMove saveExpr = new IRMove(new IRTemp(tempVal), irExp);
                                dimValues.add(saveExpr);
                                dimTemps.add(tempVal);
                            }else{
                                dimTemps.add(null);
                            }
                        }
                        ArrayList<IRStmt> initSequence = new ArrayList<>();
                        initSequence.addAll(dimValues); // add a[foo(2,b)] add the side effects from the foos
                        initSequence.add(initArrayDecl(0,dimTemps, iden)); // actually allocate
                        order.add(new IRSeq(initSequence)); // passed in temp x
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
                        argsList.add(param.accept(this));
                    }
                    IRCallStmt funcCall = new IRCallStmt(new IRName(funcName),1L,argsList);
                    IRESeq sideEffects = new IRESeq(funcCall, new IRTemp("_RV1"));
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
    public IRStmt visit(Globdecl node) { // going have to be IRDATA
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
        IRFuncDecl ret =  new IRFuncDecl(abiName, new IRSeq(stmtList));
        ret.functionSig = node.getFunctionSig();
        return ret;
    }

    @Override
    public IRStmt visit(MultiGlobalDecl node) {
    // Don't visit create function that adds to Global MAP
        return null;
    }

    @Override // Visit only on No Assign
    public IRStmt visit(AnnotatedTypeDecl node) { // could be IREXPR
        if (node.type.isArray()){
            if (node.type.dimensions.allEmpty){ // random init is fine
                return new IRMove(new IRTemp(node.identifier.toString()),new IRConst(0));
            }else{
                IRExpr iden = node.getIdentifier().accept(this); // x:int[e1][e2][e3]
                ArrayList<String> dimTemps = new ArrayList<>();
                ArrayList<IRStmt> dimValues = new ArrayList<>();
                for (Expr curExpr: node.type.dimensions.indices){
                    if (curExpr != null) {
                        String tempVal = nxtTemp();
                        IRExpr irExp = curExpr.accept(this);
                        IRMove saveExpr = new IRMove(new IRTemp(tempVal), irExp);
                        dimValues.add(saveExpr);
                        dimTemps.add(tempVal);
                    }else{
                        dimTemps.add(null);
                    }
                }
                ArrayList<IRStmt> initSequence = new ArrayList<>();
                initSequence.addAll(dimValues); // add a[foo(2,b)] add the side effects from the foos
                initSequence.add(initArrayDecl(0,dimTemps, iden)); // actually allocate
                return new IRSeq(initSequence); // passed in temp x
            }
        }else if (node.type.isBasic()){
            return new IRMove(new IRTemp(node.identifier.toString()),new IRConst(0));
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

        for (IRData ird: string_consts){
            compUnit.appendData(ird);
        }

        // Return Comp Unit

        return compUnit;
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
        }else if (e instanceof NotUnop nt){ // C[!e, t, f]  = C[e, f, t]
            return booleanAsControlFlow(nt.getE(),lf,lt);
        }
        IRExpr cond = e.accept(this);         // C[e, t, f]  = CJUMP(E[e], t, f)
        return new IRCJump(cond, lt, lf);
    }

    private String genABIFunc(Type funcType, Id funcName){
        if (funcType.getType() != Type.TypeCheckingType.FUNC){
            throw new Error("HOW ARE WE HERE");
        }
        String replaceName = funcName.toString().replaceAll("_","__");
        String inputABIName = genABIArr(funcType.inputTypes,true);
        String outputABIName = genABIArr(funcType.outputTypes,false);
        return "_I" + replaceName +"_"+ outputABIName + inputABIName;
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
    private IRStmt initArrayDecl(int ind, ArrayList<String> temps, IRExpr curHead){ // this is for a:int[4][3][] etc
        // a:int[e1][e2][][]
        if (ind == temps.size() || temps.get(ind) == null){
//            System.out.println(ind);
//            System.out.println(d.getDim());
//            System.out.println(ind == d.getDim());
//            System.out.println("here");
            return new IRMove(curHead,new IRConst(0)); // base case x: int[] x <- random val
        }

//        Expr curExp = d.getIndices().get(ind);
//        IRExpr irExp = curExp.accept(this);


        String tn = nxtTemp();
        String tm = nxtTemp();

        String noDup = nxtTemp();
        IRMove tempNoDup = new IRMove(new IRTemp(noDup),new IRTemp(temps.get(ind)));
        // reg[l] <- length
        IRMove length_to_l1 = new IRMove(new IRTemp(tn), new IRTemp(noDup));

        // 8*n+8
        IRBinOp size1 = new IRBinOp(IRBinOp.OpType.ADD,
                new IRBinOp(IRBinOp.OpType.MUL,
                        new IRTemp(tn),
                        new IRConst(WORD_BYTES)),
                new IRConst(WORD_BYTES));

        // call alloc and move RV1 into val
        IRCallStmt alloc_call1 = new IRCallStmt(new IRName("_eta_alloc"),1L, size1);
        IRSeq malloc_move1 = new IRSeq(alloc_call1,new IRMove(new IRTemp(tm), new IRTemp("_RV1")));

        // move len into -1
        IRMove move_len = new IRMove(new IRMem(new IRTemp(tm)),new IRTemp(tn));

        // increment pointer to head
        IRBinOp add_8 = new IRBinOp(IRBinOp.OpType.ADD,new IRTemp(tm), new IRConst(WORD_BYTES));
        IRMove inc_pointer_to_head = new IRMove(curHead,add_8);
        // do all the top level shit first
        IRSeq top_level_Order = new IRSeq(tempNoDup,length_to_l1,malloc_move1,move_len,inc_pointer_to_head);


        // now time to recrusively alloc
        String lh = nxtLabel();
        String l1 = nxtLabel();
        String le = nxtLabel();
        String counter = nxtTemp();
        IRBinOp guard = new IRBinOp(IRBinOp.OpType.LT,new IRTemp(counter), new IRTemp(noDup));
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
        IRStmt recur = initArrayDecl(ind+1, temps, memHead);
        // increment counter
        IRMove inc = new IRMove(new IRTemp(counter),new IRBinOp(IRBinOp.OpType.ADD,new IRTemp(counter), new IRConst(1)));
        // jump back to loop head
        IRJump go_back_to_head = new IRJump(new IRName(lh));
        IRLabel afterLoop = new IRLabel(le);

        IRSeq loopComponent = new IRSeq(set0Coutner,whileHead,loopCheck,whileBody,recur,inc,go_back_to_head,afterLoop);

        return new IRSeq(top_level_Order,loopComponent);
    }

    private IRData initSingleGlobal(Globdecl node){
        Expr e = node.getValue();
        AnnotatedTypeDecl d = node.getDecl();
        IRData irdata;
        long[] data;
        String name = "_" + d.getIdentifier();

        if (e == null) {
            irdata = new IRData(name,  new long[]{0});
            return irdata;
        }

        if (e.getNodeType().getType() == Type.TypeCheckingType.INT) {
            data = new long[]{((IntLiteral) e).getLong()};
            irdata = new IRData(name, data);
        } else if (e.getNodeType().getType() == Type.TypeCheckingType.BOOL) {
            boolean b = ((BoolLiteral) e).getBoolVal();
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
    }

    private ArrayList<IRData> initMultiGlobal(MultiGlobalDecl node){

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
        String ler = nxtLabel();
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
                                lok,ler),
                        new IRLabel(ler),
                        new IRCallStmt(new IRName(OUT_OF_BOUNDS), 0L,new ArrayList<>()),
                        new IRLabel(lok)
                ),
                new IRMem(
                        new IRBinOp(IRBinOp.OpType.ADD,
                                new IRTemp(ta),
                                new IRBinOp(IRBinOp.OpType.MUL,new IRTemp(ti),new IRConst(8))
                        )));
        return accessRecur(ind + 1, indexes, sol);
    }

    public ASMTempExpr tileCost(IRTemp t1, IRTemp t2) {
        return null;
    }

    public ASMTempExpr tileCost(IRTemp t, IRConst c) {
        return null;
    }

    public ASMTempExpr tileCost(IRTemp t, IRMem m) {
        return null;
    }
    public ASMTempExpr tileCost(IRTemp t, IRBinOp b) {
        return null;
    }
    public ASMTempExpr tileCost(IRMem m, IRTemp t) {
        return null;
    }
    public ASMTempExpr tileCost(IRMem m1, IRMem m2) {
        return null;
    }
    public ASMTempExpr tileCost(IRMem m, IRConst c) {
        return null;
    }
    public ASMTempExpr tileCost(IRMem m, IRBinOp b) {
        return null;
    }
}
