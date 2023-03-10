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

/**
 * Type Checking Visitor Class
 * */
public class TypeCheckerVisitor implements Visitor<Void>{
    @Override
    public Void visit(IntOutBinop node) {
        return null;
    }

    @Override
    public Void visit(PlusBinop node) {
        return null;
    }

    @Override
    public Void visit(IntegerComparisonBinop node) {
        return null;
    }

    @Override
    public Void visit(EquivalenceBinop node) {
        return null;
    }

    @Override
    public Void visit(LogicalBinop node) {
        return null;
    }

    @Override
    public Void visit(NotUnop node) {
        return null;
    }

    @Override
    public Void visit(IntegerNegExpr node) {
        return null;
    }

    @Override
    public Void visit(BoolLiteral node) {
        return null;
    }

    @Override
    public Void visit(IntLiteral node) {
        return null;
    }

    @Override
    public Void visit(Length node) {
        return null;
    }

    @Override
    public Void visit(FunctionCallExpr node) {
        return null;
    }

    @Override
    public Void visit(Id node) {
        return null;
    }

    @Override
    public Void visit(ArrayValueLiteral node) {
        return null;
    }

    @Override
    public Void visit(ArrayAccessExpr node) {
        return null;
    }

    @Override
    public Void visit(Block node) {
        return null;
    }

    @Override
    public Void visit(IfElse node) {
        return null;
    }

    @Override
    public Void visit(IfOnly node) {
        return null;
    }

    @Override
    public Void visit(ProcedureCall node) {
        return null;
    }

    @Override
    public Void visit(Return node) {
        return null;
    }

    @Override
    public Void visit(While node) {
        return null;
    }

    @Override
    public Void visit(DeclAssignStmt node) {
        return null;
    }

    @Override
    public Void visit(DeclNoAssignStmt node) {
        return null;
    }

    @Override
    public Void visit(MultiDeclAssignStmt node) {
        return null;
    }

    @Override
    public Void visit(Globdecl node) {
        return null;
    }

    @Override
    public Void visit(Method node) {
        return null;
    }

    @Override
    public Void visit(MultiGlobalDecl node) {
        return null;
    }

    @Override
    public Void visit(AnnotatedTypeDecl node) {
        return null;
    }

    @Override
    public Void visit(ArrAccessDecl node) {
        return null;
    }

    @Override
    public Void visit(NoTypeDecl node) {
        return null;
    }

    @Override
    public Void visit(UnderScore node) {
        return null;
    }

    @Override
    public Void visit(Use node) {
        return null;
    }

    @Override
    public Void visit(Program node) {
        return null;
    }

//    @Override
//    public Void visit(Type node) {
//        return null;
//    }
}
