package aar226_akc55_ayc62_ahl88.ast;

import java.util.ArrayList;
import java.util.Arrays;

class Program {
    private ArrayList<Use> uses;
    private ArrayList<Definition> definitions;

    public Program(ArrayList<Use> uses, ArrayList<Definition> definitions) {
        this.uses = uses;
        this.definitions = definitions;
    }
}

class Id {
    private String id;

    public Id(String id) {
        this.id = id;
    }
}

class Use {
    private Id id;

    public Use(Id id) {
        this.id = id;
    }

    public Use(String id) {
        this.id = new Id(id);
    }
}

class Definition {
    private Method method;
    private Globdecl globdecl;

}

class Method {
    private Id id;
    private ArrayList<Decl> decls;
    private ArrayList<Type> types;
    private Block block;
}

class Globdecl {
    private Id id;
    private Type type;
    private Value value;
}

class Decl {
    private Id id;
    private Type type;
}

class Block {
    private ArrayList<Stmt> stmts;
}

class Op {
    private ArrayList<Arg> args;
    private OpExpr op;
}

class Type {

}

class Value {

}

class Stmt {

}

class Arg {

}

interface Expr {

}

class IntLiteral implements Expr {
   private int i;
   private String raw;

   public IntLiteral(int i) {
       this.i = i;
   }

   public IntLiteral(String s) {

   }

   public IntLiteral(char c) {
       this.i = Character.getNumericValue(c);
   }
}

class BoolLiteral {
    private boolean b;

    public BoolLiteral(boolean b) {
        this.b = b;
    }
}

class OpExpr implements Expr {

}

class BinaryExpr extends OpExpr {
    private String bop;
    private Expr e1, e2;
    public BinaryExpr(String bop, Expr e1, Expr e2) {
        this.bop = bop;
        this.e1 = e1;
        this.e2 = e2;
    }
}

class MinusExpr extends BinaryExpr {
    public MinusExpr(Expr e1, Expr e2){
        super("-", e1, e2);
    }
}
class TimesExpr extends BinaryExpr {
    public TimesExpr(Expr e1, Expr e2){
        super("*", e1, e2);
    }
}
class Hi_MultExpr extends BinaryExpr {
    public Hi_MultExpr(Expr e1, Expr e2){
        super("*>>", e1, e2);
    }
}
class DivideExpr extends BinaryExpr {
    public DivideExpr(Expr e1, Expr e2){
        super("/", e1, e2);
    }
}
class ModuloExpr extends BinaryExpr {
    public ModuloExpr(Expr e1, Expr e2){
        super("%", e1, e2);
    }
}
class PlusExpr extends BinaryExpr {
    public PlusExpr(Expr e1, Expr e2){
        super("+", e1, e2);
    }
}
class LtExpr extends BinaryExpr {
    public LtExpr(Expr e1, Expr e2){
        super("<", e1, e2);
    }
}
class LeqExpr extends BinaryExpr {
    public LeqExpr(Expr e1, Expr e2){
        super("<=", e1, e2);
    }
}
class GeqExpr extends BinaryExpr {
    public GeqExpr(Expr e1, Expr e2){
        super(">=", e1, e2);
    }
}
class GtExpr extends BinaryExpr {
    public GtExpr(Expr e1, Expr e2){
        super(">", e1, e2);
    }
}
class EqualExpr extends BinaryExpr {
    public EqualExpr(Expr e1, Expr e2){
        super("==", e1, e2);
    }
}
class Not_EqualExpr extends BinaryExpr {
    public Not_EqualExpr(Expr e1, Expr e2){
        super("!=", e1, e2);
    }
}
class AndExpr extends BinaryExpr {
    public AndExpr(Expr e1, Expr e2){
        super("&", e1, e2);
    }
}
class OrExpr extends BinaryExpr {
    public OrExpr(Expr e1, Expr e2){
        super("|", e1, e2);
    }
}

class UnaryExpr extends OpExpr {
    private String uop;
    private Expr e;
    public UnaryExpr(String uop, Expr e) {
        this.uop = uop;
        this.e = e;
    }
}

class NotExpr extends UnaryExpr {
    public NotExpr(Expr e) {
        super("!", e);
    }
}

class IntegerNegExpr extends UnaryExpr {
    public IntegerNegExpr(Expr e) {
        super("-", e);
    }
}

class ListExpr implements Expr {
   private ArrayList<Expr> exprs;
   private String raw;

   public ListExpr(ArrayList<Expr> exprs) {
       this.exprs = exprs;
   }

   public ListExpr(String s) {
       exprs = new ArrayList<>();
       this.raw = s;
       for (char c : s.toCharArray()) {
          exprs.add(new IntLiteral(c));
       }
   }
}

class FunctionExpr implements Expr {

    public FunctionExpr() {

    }
}