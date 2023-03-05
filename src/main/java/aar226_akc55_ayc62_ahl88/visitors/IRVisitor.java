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
import aar226_akc55_ayc62_ahl88.newast.expr.binop.boolbop.EquivalenceBinop;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.boolbop.IntegerComparisonBinop;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.boolbop.LogicalBinop;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.intbop.IntOutBinop;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.intbop.PlusBinop;
import aar226_akc55_ayc62_ahl88.newast.expr.unop.booluop.NotUnop;
import aar226_akc55_ayc62_ahl88.newast.expr.unop.intuop.IntegerNegExpr;
import aar226_akc55_ayc62_ahl88.newast.stmt.*;
import aar226_akc55_ayc62_ahl88.newast.stmt.declstmt.DeclAssignStmt;
import aar226_akc55_ayc62_ahl88.newast.stmt.declstmt.DeclNoAssignStmt;
import aar226_akc55_ayc62_ahl88.newast.stmt.declstmt.MultiDeclAssignStmt;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRExpr;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;

public class IRVisitor implements Visitor<IRNode>{
    @Override
    public IRNode visit(IntOutBinop node) {
        return null;
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
        return null;
    }

    @Override
    public IRNode visit(EquivalenceBinop node) {
        IRExpr l = (IRExpr) node.getLeftExpr().accept(this);
        IRExpr r = (IRExpr) node.getRightExpr().accept(this);
    }

    @Override
    public IRNode visit(LogicalBinop node) {
        return null;
    }

    @Override
    public IRNode visit(NotUnop node) {
        return null;
    }

    @Override
    public IRNode visit(IntegerNegExpr node) {
        return null;
    }

    @Override
    public IRNode visit(BoolLiteral node) {
        return null;
    }

    @Override
    public IRNode visit(IntLiteral node) {
        return null;
    }

    @Override
    public IRNode visit(Length node) {
        return null;
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
        return null;
    }

    @Override
    public IRNode visit(IfElse node) {
        return null;
    }

    @Override
    public IRNode visit(IfOnly node) {
        return null;
    }

    @Override
    public IRNode visit(ProcedureCall node) {
        return null;
    }

    @Override
    public IRNode visit(Return node) {
        return null;
    }

    @Override
    public IRNode visit(While node) {
        return null;
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
}
