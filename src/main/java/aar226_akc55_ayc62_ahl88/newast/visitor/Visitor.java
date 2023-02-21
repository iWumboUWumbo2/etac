package aar226_akc55_ayc62_ahl88.newast.visitor;


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

}

