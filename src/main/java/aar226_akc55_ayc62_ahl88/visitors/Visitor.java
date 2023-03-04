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
import aar226_akc55_ayc62_ahl88.newast.expr.unop.booluop.BooleanUnop;
import aar226_akc55_ayc62_ahl88.newast.expr.unop.booluop.NotUnop;
import aar226_akc55_ayc62_ahl88.newast.expr.unop.intuop.IntUnop;
import aar226_akc55_ayc62_ahl88.newast.expr.unop.intuop.IntegerNegExpr;
import aar226_akc55_ayc62_ahl88.newast.interfaceNodes.EtiInterface;
import aar226_akc55_ayc62_ahl88.newast.interfaceNodes.Method_Interface;
import aar226_akc55_ayc62_ahl88.newast.stmt.*;
import aar226_akc55_ayc62_ahl88.newast.stmt.declstmt.DeclAssignStmt;
import aar226_akc55_ayc62_ahl88.newast.stmt.declstmt.DeclNoAssignStmt;
import aar226_akc55_ayc62_ahl88.newast.stmt.declstmt.MultiDeclAssignStmt;

public interface Visitor<T> {

    // used for DIVIDE, HIGHMULT, MINUS, MODULO, TIMES
    T visit(IntOutBinop node);
    // used for just PLUS
    T visit(PlusBinop node);
    // used for < , <= , > , >=
    T visit(IntegerComparisonBinop node);
    // used for ==, !=
    T visit(EquivalenceBinop node);
    // used for & , |
    T visit(LogicalBinop node);
    //  used for !
    T visit(NotUnop node);
    // used for Negation -
    T visit(IntegerNegExpr node);
    T visit(BoolLiteral node);

    T visit(IntLiteral node);

    T visit(Length node);

    T visit(FunctionCallExpr node);

    T visit(Id node);

    T visit(ArrayValueLiteral node);

    T visit(ArrayAccessExpr node);

    T visit(Block node);

    T visit(IfElse node);

    T visit(IfOnly node);

    T visit(ProcedureCall node);

    T visit(Return node);

    T visit(While node);

    T visit(DeclAssignStmt node);

    T visit(DeclNoAssignStmt node);

    T visit(MultiDeclAssignStmt node);

    T visit(Globdecl node);

    T visit(Method node);

    T visit(MultiGlobalDecl node);

    T visit(AnnotatedTypeDecl node);

    T visit(ArrAccessDecl node);

    T visit(NoTypeDecl node);

    T visit(UnderScore node);

    T visit(Use node);

    T visit(Program node);

    // unsure
    T visit(Type node);

    T visit(EtiInterface node);

    T visit(Method_Interface node);
    T visit(BooleanUnop node);
    T visit(IntUnop node);

}

