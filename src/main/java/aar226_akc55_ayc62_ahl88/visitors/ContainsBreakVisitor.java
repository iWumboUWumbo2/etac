package aar226_akc55_ayc62_ahl88.visitors;

import aar226_akc55_ayc62_ahl88.newast.Program;
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
import aar226_akc55_ayc62_ahl88.newast.expr.binop.BinopExpr;
import aar226_akc55_ayc62_ahl88.newast.expr.binop.RecordAcessBinop;
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

public class ContainsBreakVisitor implements  Visitor<Boolean>{

    public ContainsBreakVisitor(){

    }
    @Override
    public Boolean visit(IntOutBinop node) {
        return false;
    }

    @Override
    public Boolean visit(PlusBinop node) {
        return false;
    }

    @Override
    public Boolean visit(IntegerComparisonBinop node) {
        return false;
    }

    @Override
    public Boolean visit(EquivalenceBinop node) {
        return false;
    }

    @Override
    public Boolean visit(LogicalBinop node) {
        return false;
    }

    @Override
    public Boolean visit(NotUnop node) {
        return false;
    }

    @Override
    public Boolean visit(IntegerNegExpr node) {
        return false;
    }

    @Override
    public Boolean visit(BoolLiteral node) {
        return false;
    }

    @Override
    public Boolean visit(IntLiteral node) {
        return false;
    }

    @Override
    public Boolean visit(Length node) {
        return false;
    }

    @Override
    public Boolean visit(FunctionCallExpr node) {
        return false;
    }

    @Override
    public Boolean visit(Id node) {
        return false;
    }

    @Override
    public Boolean visit(ArrayValueLiteral node) {
        return false;
    }

    @Override
    public Boolean visit(ArrayAccessExpr node) {
        return false;
    }

    @Override
    public Boolean visit(Block node) {
        boolean found = false;
        for (Stmt s : node.getStatementList()){
            found = found || s.accept(this);
        }
        return found;
    }

    @Override
    public Boolean visit(IfElse node) {
        return node.getIfState().accept(this)|| node.getElseState().accept(this);
    }

    @Override
    public Boolean visit(IfOnly node) {
        return node.ifState.accept(this);
    }

    @Override
    public Boolean visit(ProcedureCall node) {
        return false;
    }

    @Override
    public Boolean visit(Return node) {
        return false;
    }

    @Override
    public Boolean visit(While node) {
        return node.getStmt().accept(this);
    }

    @Override
    public Boolean visit(Break node) {
        return true;
    }

    @Override
    public Boolean visit(DeclAssignStmt node) {
        return false;
    }

    @Override
    public Boolean visit(DeclNoAssignStmt node) {
        return false;
    }

    @Override
    public Boolean visit(MultiDeclAssignStmt node) {
        return false;
    }

    @Override
    public Boolean visit(Globdecl node) {
        return false;
    }

    @Override
    public Boolean visit(MultiGlobalDecl node) {
        return false;
    }

    @Override
    public Boolean visit(Method node) {
        return visit(node.getBlock());
    }

    @Override
    public Boolean visit(AnnotatedTypeDecl node) {
        return false;
    }

    @Override
    public Boolean visit(ArrAccessDecl node) {
        return false;
    }

    @Override
    public Boolean visit(RecordAcessBinop node) {
        return false;
    }

    @Override
    public Boolean visit(NoTypeDecl node) {
        return false;
    }

    @Override
    public Boolean visit(UnderScore node) {
        return false;
    }

    @Override
    public Boolean visit(Null node) {
        return false;
    }

    @Override
    public Boolean visit(Use node) {
        return false;
    }

    @Override
    public Boolean visit(Program node) {
        return true;
    }
}
