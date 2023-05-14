package aar226_akc55_ayc62_ahl88.visitors;

import aar226_akc55_ayc62_ahl88.newast.Program;
import aar226_akc55_ayc62_ahl88.newast.Use;
import aar226_akc55_ayc62_ahl88.newast.declarations.*;
import aar226_akc55_ayc62_ahl88.newast.definitions.Globdecl;
import aar226_akc55_ayc62_ahl88.newast.definitions.Method;
import aar226_akc55_ayc62_ahl88.newast.definitions.MultiGlobalDecl;
import aar226_akc55_ayc62_ahl88.newast.expr.*;
import aar226_akc55_ayc62_ahl88.newast.expr.arrayaccessexpr.ArrayAccessExpr;
import aar226_akc55_ayc62_ahl88.newast.expr.arrayliteral.ArrayValueLiteral;
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

    // used for !
    T visit(NotUnop node);

    // used for Negation -
    T visit(IntegerNegExpr node);

    // used for boolean literals "true", "false"
    T visit(BoolLiteral node);

    // used for integer literals "1", "2", etc.
    T visit(IntLiteral node);

    // used for length function
    T visit(Length node);

    // used for function calls
    T visit(FunctionCallExpr node);

    // used for identifiers
    T visit(Id node);

    // used for array value literals
    T visit(ArrayValueLiteral node);

    // used for array access expression
    T visit(ArrayAccessExpr node);

    // used for blocks
    T visit(Block node);

    // used for if else statements
    T visit(IfElse node);

    // used for if statements
    T visit(IfOnly node);

    // used for procedure calls
    T visit(ProcedureCall node);

    // used for returns
    T visit(Return node);

    // used for while statements
    T visit(While node);

    // used for break statements
    T visit(Break node);

    // used for single declaration assigns
    T visit(DeclAssignStmt node);

    // used for single declaration without assignment
    T visit(DeclNoAssignStmt node);

    // used for multiple declaration assigns
    T visit(MultiDeclAssignStmt node);

    // used for single global declarations
    T visit(Globdecl node);

    // used for multiple global declarations
    T visit(MultiGlobalDecl node);

    // used for method definitions
    T visit(Method node);

    // used for declarations with specified type
    T visit(AnnotatedTypeDecl node);

    // used for array access declarations
    T visit(ArrAccessDecl node);

    // used for record access expressions
    T visit(RecordAcessBinop node);

    // used for declarations without specified type
    T visit(NoTypeDecl node);

    // used for underscore assignment
    T visit(UnderScore node);

    // used for null type
    T visit(Null node);

    // used for use statements
    T visit(Use node);

    // used for program statements
    T visit(Program node);
}