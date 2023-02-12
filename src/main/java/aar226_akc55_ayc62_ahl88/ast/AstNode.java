package aar226_akc55_ayc62_ahl88.ast;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.*;
import java.util.ArrayList;

enum Exprs {
    Bop,
    Uop,
    FuncCall,
    IntLit,
    BoolLit,
    ListExpr

}

class Id extends Printer {
    private String id;
    public Id(String id) {
        this.id = id;
    }
    public String toString() {
        return id;
    }
    public void prettyPrint(SExpPrinter p) {
        p.printAtom(id);
    }

}

class Block {
    private ArrayList<Stmt> stmts;
}


class Stmt {

}

class Arg {

}

abstract class Expr {
    Exprs type;
    public Expr() {}
    public Exprs getType() {
        return type;
    }

    public void prettyPrint(SExpPrinter p) {
    }
}

class BinaryExpr extends Expr {
    private String bop;
    private Expr e1, e2;
    public BinaryExpr(String bop, Expr e1, Expr e2) {
        this.bop = bop;
        this.e1 = e1;
        this.e2 = e2;
        this.type = Exprs.Bop;
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

class UnaryExpr extends Expr {
    private String uop;
    private Expr e;
    public UnaryExpr(String uop, Expr e) {
        this.uop = uop;
        this.e = e;
        this.type = Exprs.Uop;
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

class ListExpr extends Expr {
   private ArrayList<Expr> exprs;
   private String raw;

   public ListExpr(ArrayList<Expr> exprs) {
       this.exprs = exprs;
       this.type = Exprs.ListExpr;
       this.raw = null;
   }

   public ListExpr(String s) {
       exprs = new ArrayList<>();
       this.raw = s;
       this.type = Exprs.ListExpr;
       for (char c : s.toCharArray()) {
          exprs.add(new IntLiteral(Character.toString(c)));
       }
   }

   public String toString () {
      if (raw != null) {
          return raw;
      } else {
          StringBuilder sb = new StringBuilder();
          for (int i = 0; i < sb.length() - 1; i++) {
              sb.append(exprs.get(i)).append(" ");
          }
          sb.append(exprs.get(sb.length() - 1));

          return sb.toString();
      }
   }

   public void prettyPrint(SExpPrinter p) {
       if (raw != null) {
           p.printAtom("\"");
           p.printAtom(raw);
           p.printAtom("\"");
       } else {
           p.startList();
           for (Expr e : exprs) {
               e.prettyPrint(p);
           }
           p.endList();
       }
   }
}

class FunctionCallExpr extends Expr {
    Id id;
    ArrayList<Expr> param;

    public FunctionCallExpr(Id id, ArrayList<Expr> param) {
       this.id = id;
       this.param = param;
       this.type = Exprs.FuncCall;
    }

    @Override
    public void prettyPrint(SExpPrinter p) {
        p.startList();
        p.printAtom(id.toString());
        param.forEach(e -> e.prettyPrint(p));
        p.endList();
    }
}