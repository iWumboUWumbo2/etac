
//----------------------------------------------------
// The following code was generated by CUP v0.11b 20150326
//----------------------------------------------------

package aar226_akc55_ayc62_ahl88;

import java_cup.runtime.*;
import aar226_akc55_ayc62_ahl88.ast.*;
import java.util.ArrayList;

/** CUP v0.11b 20150326 generated parser.
  */
public class parser
 extends java_cup.runtime.lr_parser {

  @Override
  public final Class<?> getSymbolContainer() {
    return sym.class;
  }

  /** Default constructor. */
  @Deprecated
  public parser() {super();}

  /** Constructor which sets the default scanner. */
  @Deprecated
  public parser(java_cup.runtime.Scanner s) {super(s);}

  /** Constructor which sets the default scanner and a SymbolFactory. */
  public parser(java_cup.runtime.Scanner s, java_cup.runtime.SymbolFactory sf) {super(s,sf);}

  /** Production table. */
  protected static final short _production_table[][] = 
    unpackFromStrings(new String[] {
    "\000\044\000\002\002\004\000\002\002\004\000\002\003" +
    "\004\000\002\003\002\000\002\005\004\000\002\005\005" +
    "\000\002\006\004\000\002\006\002\000\002\007\003\000" +
    "\002\007\003\000\002\010\010\000\002\010\006\000\002" +
    "\011\005\000\002\011\003\000\002\011\002\000\002\012" +
    "\005\000\002\016\003\000\002\016\005\000\002\016\004" +
    "\000\002\017\003\000\002\017\003\000\002\017\004\000" +
    "\002\017\003\000\002\017\005\000\002\017\006\000\002" +
    "\017\004\000\002\017\003\000\002\020\003\000\002\020" +
    "\005\000\002\013\005\000\002\013\003\000\002\014\004" +
    "\000\002\014\004\000\002\015\005\000\002\015\006\000" +
    "\002\015\002" });

  /** Access to production table. */
  @Override
  public short[][] production_table() {return _production_table;}

  /** Parse-action table. */
  protected static final short[][] _action_table = 
    unpackFromStrings(new String[] {
    "\000\065\000\010\002\ufffe\004\ufffe\022\ufffe\001\002\000" +
    "\010\002\ufffa\004\ufffa\022\007\001\002\000\004\002\006" +
    "\001\002\000\004\002\001\001\002\000\004\004\066\001" +
    "\002\000\006\002\000\004\015\001\002\000\010\002\uffff" +
    "\004\uffff\022\uffff\001\002\000\006\002\ufff9\004\ufff9\001" +
    "\002\000\010\002\ufff8\004\ufff8\054\065\001\002\000\006" +
    "\002\ufffb\004\ufffb\001\002\000\006\046\037\052\040\001" +
    "\002\000\012\002\ufff1\004\ufff1\043\017\054\ufff1\001\002" +
    "\000\016\005\022\006\020\007\023\012\025\025\026\050" +
    "\021\001\002\000\014\002\uffed\004\uffed\051\uffed\053\uffed" +
    "\054\uffed\001\002\000\020\005\022\006\020\007\023\012" +
    "\025\025\026\050\021\051\031\001\002\000\014\002\uffe7" +
    "\004\uffe7\051\uffe7\053\uffe7\054\uffe7\001\002\000\014\002" +
    "\uffee\004\uffee\051\uffee\053\uffee\054\uffee\001\002\000\010" +
    "\002\ufff0\004\ufff0\054\ufff0\001\002\000\014\002\uffeb\004" +
    "\uffeb\051\uffeb\053\uffeb\054\uffeb\001\002\000\004\007\027" +
    "\001\002\000\014\002\uffec\004\uffec\051\uffec\053\uffec\054" +
    "\uffec\001\002\000\006\051\034\053\033\001\002\000\014" +
    "\002\uffe8\004\uffe8\051\uffe8\053\uffe8\054\uffe8\001\002\000" +
    "\006\051\uffe6\053\uffe6\001\002\000\020\005\022\006\020" +
    "\007\023\012\025\025\026\050\021\051\035\001\002\000" +
    "\014\002\uffea\004\uffea\051\uffea\053\uffea\054\uffea\001\002" +
    "\000\014\002\uffe9\004\uffe9\051\uffe9\053\uffe9\054\uffe9\001" +
    "\002\000\006\051\uffe5\053\uffe5\001\002\000\010\004\052" +
    "\047\ufff3\053\ufff3\001\002\000\006\013\043\014\041\001" +
    "\002\000\020\002\uffde\004\uffde\043\uffde\044\uffde\047\uffde" +
    "\053\uffde\054\uffde\001\002\000\016\002\ufff2\004\ufff2\043" +
    "\ufff2\047\ufff2\053\ufff2\054\ufff2\001\002\000\020\002\uffde" +
    "\004\uffde\043\uffde\044\uffde\047\uffde\053\uffde\054\uffde\001" +
    "\002\000\020\002\uffe2\004\uffe2\043\uffe2\044\045\047\uffe2" +
    "\053\uffe2\054\uffe2\001\002\000\006\007\047\045\046\001" +
    "\002\000\020\002\uffe0\004\uffe0\043\uffe0\044\uffe0\047\uffe0" +
    "\053\uffe0\054\uffe0\001\002\000\004\045\050\001\002\000" +
    "\020\002\uffdf\004\uffdf\043\uffdf\044\uffdf\047\uffdf\053\uffdf" +
    "\054\uffdf\001\002\000\020\002\uffe1\004\uffe1\043\uffe1\044" +
    "\045\047\uffe1\053\uffe1\054\uffe1\001\002\000\004\052\040" +
    "\001\002\000\006\047\056\053\055\001\002\000\006\047" +
    "\ufff4\053\ufff4\001\002\000\004\004\052\001\002\000\010" +
    "\002\ufff6\004\ufff6\052\057\001\002\000\006\013\043\014" +
    "\041\001\002\000\010\002\uffe3\004\uffe3\053\uffe3\001\002" +
    "\000\010\002\ufff7\004\ufff7\053\062\001\002\000\006\013" +
    "\043\014\041\001\002\000\010\002\uffe4\004\uffe4\053\uffe4" +
    "\001\002\000\006\047\ufff5\053\ufff5\001\002\000\010\002" +
    "\uffef\004\uffef\054\uffef\001\002\000\012\002\ufffd\004\ufffd" +
    "\022\ufffd\054\067\001\002\000\010\002\ufffc\004\ufffc\022" +
    "\ufffc\001\002" });

  /** Access to parse-action table. */
  @Override
  public short[][] action_table() {return _action_table;}

  /** {@code reduce_goto} table. */
  protected static final short[][] _reduce_table = 
    unpackFromStrings(new String[] {
    "\000\065\000\006\002\004\003\003\001\001\000\006\005" +
    "\010\006\007\001\001\000\002\001\001\000\002\001\001" +
    "\000\002\001\001\000\012\007\013\010\011\012\015\016" +
    "\012\001\001\000\002\001\001\000\002\001\001\000\002" +
    "\001\001\000\002\001\001\000\002\001\001\000\002\001" +
    "\001\000\004\017\023\001\001\000\002\001\001\000\006" +
    "\017\031\020\027\001\001\000\002\001\001\000\002\001" +
    "\001\000\002\001\001\000\002\001\001\000\002\001\001" +
    "\000\002\001\001\000\002\001\001\000\002\001\001\000" +
    "\002\001\001\000\004\017\035\001\001\000\002\001\001" +
    "\000\002\001\001\000\002\001\001\000\006\011\052\012" +
    "\053\001\001\000\004\014\041\001\001\000\004\015\050" +
    "\001\001\000\002\001\001\000\004\015\043\001\001\000" +
    "\002\001\001\000\002\001\001\000\002\001\001\000\002" +
    "\001\001\000\002\001\001\000\002\001\001\000\002\001" +
    "\001\000\002\001\001\000\002\001\001\000\004\012\063" +
    "\001\001\000\002\001\001\000\006\013\060\014\057\001" +
    "\001\000\002\001\001\000\002\001\001\000\004\014\062" +
    "\001\001\000\002\001\001\000\002\001\001\000\002\001" +
    "\001\000\002\001\001\000\002\001\001" });

  /** Access to {@code reduce_goto} table. */
  @Override
  public short[][] reduce_table() {return _reduce_table;}

  /** Instance of action encapsulation class. */
  protected CUP$parser$actions action_obj;

  /** Action encapsulation object initializer. */
  @Override
  protected void init_actions()
    {
      action_obj = new CUP$parser$actions(this);
    }

  /** Invoke a user supplied parse action. */
  @Override
  public java_cup.runtime.Symbol do_action(
    int                        act_num,
    java_cup.runtime.lr_parser parser,
    java.util.Stack<java_cup.runtime.Symbol> stack,
    int                        top)
    throws java.lang.Exception
  {
    /* call code in generated class */
    return action_obj.CUP$parser$do_action(act_num, parser, stack, top);
  }

  /** Indicates start state. */
  @Override
  public int start_state() {return 0;}
  /** Indicates start production. */
  @Override
  public int start_production() {return 0;}

  /** {@code EOF} Symbol index. */
  @Override
  public int EOF_sym() {return 0;}

  /** {@code error} Symbol index. */
  @Override
  public int error_sym() {return 1;}


/** Cup generated class to encapsulate user supplied action code.*/
class CUP$parser$actions {
    private final parser parser;

    /** Constructor */
    CUP$parser$actions(parser parser) {
        this.parser = parser;
    }

    /** Method with the actual generated action code for actions 0 to 35. */
    public final java_cup.runtime.Symbol CUP$parser$do_action_part00000000(
            int                        CUP$parser$act_num,
            java_cup.runtime.lr_parser CUP$parser$parser,
            java.util.Stack<java_cup.runtime.Symbol> CUP$parser$stack,
            int                        CUP$parser$top)
            throws java.lang.Exception {
            /* Symbol object for return from actions */
            java_cup.runtime.Symbol CUP$parser$result;

        /* select the action based on the action number */
        switch (CUP$parser$act_num) {
        /*. . . . . . . . . . . . . . . . . . . .*/
        case 0: // $START ::= program EOF 
            {
                Object RESULT = null;
                int start_valleft = CUP$parser$stack.elementAt(CUP$parser$top-1).left;
                int start_valright = CUP$parser$stack.elementAt(CUP$parser$top-1).right;
                Program start_val = CUP$parser$stack.elementAt(CUP$parser$top-1).<Program> value();
                RESULT = start_val;
                CUP$parser$result = parser.getSymbolFactory().newSymbol("$START",0, CUP$parser$stack.elementAt(CUP$parser$top-1), CUP$parser$stack.peek(), RESULT);
            }
            /* ACCEPT */
            CUP$parser$parser.done_parsing();
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 1: // program ::= importUseStar definitionStar 
            {
                Program RESULT = null;
                int iusleft = CUP$parser$stack.elementAt(CUP$parser$top-1).left;
                int iusright = CUP$parser$stack.elementAt(CUP$parser$top-1).right;
                ArrayList<Use> ius = CUP$parser$stack.elementAt(CUP$parser$top-1).<ArrayList<Use>> value();
                int dlleft = CUP$parser$stack.peek().left;
                int dlright = CUP$parser$stack.peek().right;
                ArrayList<Definition> dl = CUP$parser$stack.peek().<ArrayList<Definition>> value();
                RESULT = new Program(ius,dl);
                CUP$parser$result = parser.getSymbolFactory().newSymbol("program",0, CUP$parser$stack.elementAt(CUP$parser$top-1), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 2: // importUseStar ::= importUseStar use_component 
            {
                ArrayList<Use> RESULT = null;
                int iusleft = CUP$parser$stack.elementAt(CUP$parser$top-1).left;
                int iusright = CUP$parser$stack.elementAt(CUP$parser$top-1).right;
                ArrayList<Use> ius = CUP$parser$stack.elementAt(CUP$parser$top-1).<ArrayList<Use>> value();
                int uleft = CUP$parser$stack.peek().left;
                int uright = CUP$parser$stack.peek().right;
                Use u = CUP$parser$stack.peek().<Use> value();
                 ius.add(u); RESULT = ius;
                CUP$parser$result = parser.getSymbolFactory().newSymbol("importUseStar",1, CUP$parser$stack.elementAt(CUP$parser$top-1), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 3: // importUseStar ::= 
            {
                ArrayList<Use> RESULT = null;
                 RESULT = new ArrayList<Use>();
                CUP$parser$result = parser.getSymbolFactory().newSymbol("importUseStar",1, CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 4: // use_component ::= USE IDENTIFIER 
            {
                Use RESULT = null;
                int ileft = CUP$parser$stack.peek().left;
                int iright = CUP$parser$stack.peek().right;
                String i = CUP$parser$stack.peek().<String> value();
                           RESULT = new Use(i); 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("use_component",3, CUP$parser$stack.elementAt(CUP$parser$top-1), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 5: // use_component ::= USE IDENTIFIER SEMICOLON 
            {
                Use RESULT = null;
                int ileft = CUP$parser$stack.elementAt(CUP$parser$top-1).left;
                int iright = CUP$parser$stack.elementAt(CUP$parser$top-1).right;
                String i = CUP$parser$stack.elementAt(CUP$parser$top-1).<String> value();
                 RESULT = new Use(i); 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("use_component",3, CUP$parser$stack.elementAt(CUP$parser$top-2), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 6: // definitionStar ::= definitionStar definition 
            {
                ArrayList<Definition> RESULT = null;
                int dsleft = CUP$parser$stack.elementAt(CUP$parser$top-1).left;
                int dsright = CUP$parser$stack.elementAt(CUP$parser$top-1).right;
                ArrayList<Definition> ds = CUP$parser$stack.elementAt(CUP$parser$top-1).<ArrayList<Definition>> value();
                int dleft = CUP$parser$stack.peek().left;
                int dright = CUP$parser$stack.peek().right;
                Definition d = CUP$parser$stack.peek().<Definition> value();
                ds.add(d); RESULT = ds; 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("definitionStar",4, CUP$parser$stack.elementAt(CUP$parser$top-1), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 7: // definitionStar ::= 
            {
                ArrayList<Definition> RESULT = null;
                RESULT = new ArrayList<Definition>(); 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("definitionStar",4, CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 8: // definition ::= method 
            {
                Definition RESULT = null;
                int mleft = CUP$parser$stack.peek().left;
                int mright = CUP$parser$stack.peek().right;
                Method m = CUP$parser$stack.peek().<Method> value();
                 RESULT = m;
                CUP$parser$result = parser.getSymbolFactory().newSymbol("definition",5, CUP$parser$stack.peek(), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 9: // definition ::= globalDecl 
            {
                Definition RESULT = null;
                int gdleft = CUP$parser$stack.peek().left;
                int gdright = CUP$parser$stack.peek().right;
                Globdecl gd = CUP$parser$stack.peek().<Globdecl> value();
                RESULT = gd; 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("definition",5, CUP$parser$stack.peek(), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 10: // method ::= IDENTIFIER OPEN_PAREN emptyBracketDecl_star CLOSE_PAREN COLON emptyBracketType_plus 
            {
                Method RESULT = null;
                int ileft = CUP$parser$stack.elementAt(CUP$parser$top-5).left;
                int iright = CUP$parser$stack.elementAt(CUP$parser$top-5).right;
                String i = CUP$parser$stack.elementAt(CUP$parser$top-5).<String> value();
                int dsleft = CUP$parser$stack.elementAt(CUP$parser$top-3).left;
                int dsright = CUP$parser$stack.elementAt(CUP$parser$top-3).right;
                ArrayList<Decl> ds = CUP$parser$stack.elementAt(CUP$parser$top-3).<ArrayList<Decl>> value();
                int tsleft = CUP$parser$stack.peek().left;
                int tsright = CUP$parser$stack.peek().right;
                ArrayList<Type> ts = CUP$parser$stack.peek().<ArrayList<Type>> value();
                
    RESULT = new Method(i,ds,ts);
    
                CUP$parser$result = parser.getSymbolFactory().newSymbol("method",6, CUP$parser$stack.elementAt(CUP$parser$top-5), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 11: // method ::= IDENTIFIER OPEN_PAREN emptyBracketDecl_star CLOSE_PAREN 
            {
                Method RESULT = null;
                int ileft = CUP$parser$stack.elementAt(CUP$parser$top-3).left;
                int iright = CUP$parser$stack.elementAt(CUP$parser$top-3).right;
                String i = CUP$parser$stack.elementAt(CUP$parser$top-3).<String> value();
                int dsleft = CUP$parser$stack.elementAt(CUP$parser$top-1).left;
                int dsright = CUP$parser$stack.elementAt(CUP$parser$top-1).right;
                ArrayList<Decl> ds = CUP$parser$stack.elementAt(CUP$parser$top-1).<ArrayList<Decl>> value();
                 // Block b
        RESULT = new Method(i,ds,new ArrayList<Type>() ); // NONE
    
                CUP$parser$result = parser.getSymbolFactory().newSymbol("method",6, CUP$parser$stack.elementAt(CUP$parser$top-3), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 12: // emptyBracketDecl_star ::= emptyBracketDecl_star COMMA emptyBracketDecl 
            {
                ArrayList<Decl> RESULT = null;
                int declstarleft = CUP$parser$stack.elementAt(CUP$parser$top-2).left;
                int declstarright = CUP$parser$stack.elementAt(CUP$parser$top-2).right;
                ArrayList<Decl> declstar = CUP$parser$stack.elementAt(CUP$parser$top-2).<ArrayList<Decl>> value();
                int dleft = CUP$parser$stack.peek().left;
                int dright = CUP$parser$stack.peek().right;
                Decl d = CUP$parser$stack.peek().<Decl> value();
                declstar.add(d); RESULT = declstar; 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("emptyBracketDecl_star",7, CUP$parser$stack.elementAt(CUP$parser$top-2), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 13: // emptyBracketDecl_star ::= emptyBracketDecl 
            {
                ArrayList<Decl> RESULT = null;
                int dleft = CUP$parser$stack.peek().left;
                int dright = CUP$parser$stack.peek().right;
                Decl d = CUP$parser$stack.peek().<Decl> value();
                ArrayList<Decl> temp = new ArrayList<Decl>();
      temp.add(d);
      RESULT = temp;
      
                CUP$parser$result = parser.getSymbolFactory().newSymbol("emptyBracketDecl_star",7, CUP$parser$stack.peek(), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 14: // emptyBracketDecl_star ::= 
            {
                ArrayList<Decl> RESULT = null;
                 RESULT = new ArrayList<Decl>();
                CUP$parser$result = parser.getSymbolFactory().newSymbol("emptyBracketDecl_star",7, CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 15: // emptyBracketDecl ::= IDENTIFIER COLON emptyBracketType 
            {
                Decl RESULT = null;
                int ileft = CUP$parser$stack.elementAt(CUP$parser$top-2).left;
                int iright = CUP$parser$stack.elementAt(CUP$parser$top-2).right;
                String i = CUP$parser$stack.elementAt(CUP$parser$top-2).<String> value();
                int tleft = CUP$parser$stack.peek().left;
                int tright = CUP$parser$stack.peek().right;
                Type t = CUP$parser$stack.peek().<Type> value();
                 RESULT = new Decl(i,t); 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("emptyBracketDecl",8, CUP$parser$stack.elementAt(CUP$parser$top-2), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 16: // globalDecl ::= emptyBracketDecl 
            {
                Globdecl RESULT = null;
                int edleft = CUP$parser$stack.peek().left;
                int edright = CUP$parser$stack.peek().right;
                Decl ed = CUP$parser$stack.peek().<Decl> value();
                RESULT = new Globdecl(ed, null);
                CUP$parser$result = parser.getSymbolFactory().newSymbol("globalDecl",12, CUP$parser$stack.peek(), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 17: // globalDecl ::= emptyBracketDecl GETS value 
            {
                Globdecl RESULT = null;
                int edleft = CUP$parser$stack.elementAt(CUP$parser$top-2).left;
                int edright = CUP$parser$stack.elementAt(CUP$parser$top-2).right;
                Decl ed = CUP$parser$stack.elementAt(CUP$parser$top-2).<Decl> value();
                int vleft = CUP$parser$stack.peek().left;
                int vright = CUP$parser$stack.peek().right;
                Value v = CUP$parser$stack.peek().<Value> value();
                
        if (!ed.type.dimensions.allEmpty) {
            throw new Error("array with init len no Val");
        }
        if (ed.type.dimensions.getDim() != 0){
            throw new Error("global arr not init allowed");
        }
        RESULT  = new Globdecl(ed, v);
    
                CUP$parser$result = parser.getSymbolFactory().newSymbol("globalDecl",12, CUP$parser$stack.elementAt(CUP$parser$top-2), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 18: // globalDecl ::= globalDecl SEMICOLON 
            {
                Globdecl RESULT = null;
                int gdleft = CUP$parser$stack.elementAt(CUP$parser$top-1).left;
                int gdright = CUP$parser$stack.elementAt(CUP$parser$top-1).right;
                Globdecl gd = CUP$parser$stack.elementAt(CUP$parser$top-1).<Globdecl> value();
                RESULT = gd;
                CUP$parser$result = parser.getSymbolFactory().newSymbol("globalDecl",12, CUP$parser$stack.elementAt(CUP$parser$top-1), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 19: // value ::= INTEGER_LITERAL 
            {
                Value RESULT = null;
                int ileft = CUP$parser$stack.peek().left;
                int iright = CUP$parser$stack.peek().right;
                Long i = CUP$parser$stack.peek().<Long> value();
                 RESULT = new IntLiteral(i,true); 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("value",13, CUP$parser$stack.peek(), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 20: // value ::= CHARACTER_LITERAL 
            {
                Value RESULT = null;
                int cleft = CUP$parser$stack.peek().left;
                int cright = CUP$parser$stack.peek().right;
                String c = CUP$parser$stack.peek().<String> value();
                 RESULT = new IntLiteral(c); 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("value",13, CUP$parser$stack.peek(), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 21: // value ::= MINUS INTEGER_LITERAL 
            {
                Value RESULT = null;
                int ileft = CUP$parser$stack.peek().left;
                int iright = CUP$parser$stack.peek().right;
                Long i = CUP$parser$stack.peek().<Long> value();
                 RESULT = new IntLiteral(i,false); 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("value",13, CUP$parser$stack.elementAt(CUP$parser$top-1), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 22: // value ::= BOOL_LITERAL 
            {
                Value RESULT = null;
                int bleft = CUP$parser$stack.peek().left;
                int bright = CUP$parser$stack.peek().right;
                Boolean b = CUP$parser$stack.peek().<Boolean> value();
                 RESULT = new BoolLiteral(b); 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("value",13, CUP$parser$stack.peek(), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 23: // value ::= OPEN_BRACE arrayValueLiteral CLOSE_BRACE 
            {
                Value RESULT = null;
                int avlleft = CUP$parser$stack.elementAt(CUP$parser$top-1).left;
                int avlright = CUP$parser$stack.elementAt(CUP$parser$top-1).right;
                ArrayList<Value> avl = CUP$parser$stack.elementAt(CUP$parser$top-1).<ArrayList<Value>> value();
                RESULT = new ArrayValueLiteral(avl); 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("value",13, CUP$parser$stack.elementAt(CUP$parser$top-2), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 24: // value ::= OPEN_BRACE arrayValueLiteral COMMA CLOSE_BRACE 
            {
                Value RESULT = null;
                int avlleft = CUP$parser$stack.elementAt(CUP$parser$top-2).left;
                int avlright = CUP$parser$stack.elementAt(CUP$parser$top-2).right;
                ArrayList<Value> avl = CUP$parser$stack.elementAt(CUP$parser$top-2).<ArrayList<Value>> value();
                RESULT = new ArrayValueLiteral(avl); 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("value",13, CUP$parser$stack.elementAt(CUP$parser$top-3), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 25: // value ::= OPEN_BRACE CLOSE_BRACE 
            {
                Value RESULT = null;
                RESULT = new ArrayValueLiteral(new ArrayList<Value>()); 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("value",13, CUP$parser$stack.elementAt(CUP$parser$top-1), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 26: // value ::= STRING_LITERAL 
            {
                Value RESULT = null;
                int sleft = CUP$parser$stack.peek().left;
                int sright = CUP$parser$stack.peek().right;
                String s = CUP$parser$stack.peek().<String> value();
                 new ArrayValueLiteral(s); 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("value",13, CUP$parser$stack.peek(), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 27: // arrayValueLiteral ::= value 
            {
                ArrayList<Value> RESULT = null;
                int vleft = CUP$parser$stack.peek().left;
                int vright = CUP$parser$stack.peek().right;
                Value v = CUP$parser$stack.peek().<Value> value();
                
    ArrayList<Value> temp = new ArrayList<Value>();
    temp.add(v);
    RESULT = temp;
    
                CUP$parser$result = parser.getSymbolFactory().newSymbol("arrayValueLiteral",14, CUP$parser$stack.peek(), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 28: // arrayValueLiteral ::= arrayValueLiteral COMMA value 
            {
                ArrayList<Value> RESULT = null;
                int avlleft = CUP$parser$stack.elementAt(CUP$parser$top-2).left;
                int avlright = CUP$parser$stack.elementAt(CUP$parser$top-2).right;
                ArrayList<Value> avl = CUP$parser$stack.elementAt(CUP$parser$top-2).<ArrayList<Value>> value();
                int vleft = CUP$parser$stack.peek().left;
                int vright = CUP$parser$stack.peek().right;
                Value v = CUP$parser$stack.peek().<Value> value();
                avl.add(v); RESULT = avl; 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("arrayValueLiteral",14, CUP$parser$stack.elementAt(CUP$parser$top-2), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 29: // emptyBracketType_plus ::= emptyBracketType_plus COMMA emptyBracketType 
            {
                ArrayList<Type> RESULT = null;
                int tsleft = CUP$parser$stack.elementAt(CUP$parser$top-2).left;
                int tsright = CUP$parser$stack.elementAt(CUP$parser$top-2).right;
                ArrayList<Type> ts = CUP$parser$stack.elementAt(CUP$parser$top-2).<ArrayList<Type>> value();
                int tleft = CUP$parser$stack.peek().left;
                int tright = CUP$parser$stack.peek().right;
                Type t = CUP$parser$stack.peek().<Type> value();
                 ts.add(t); RESULT = ts; 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("emptyBracketType_plus",9, CUP$parser$stack.elementAt(CUP$parser$top-2), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 30: // emptyBracketType_plus ::= emptyBracketType 
            {
                ArrayList<Type> RESULT = null;
                int tleft = CUP$parser$stack.peek().left;
                int tright = CUP$parser$stack.peek().right;
                Type t = CUP$parser$stack.peek().<Type> value();
                 ArrayList<Type> typeList = new ArrayList<Type>();
                      typeList.add(t);
                      RESULT = typeList;
                      
                CUP$parser$result = parser.getSymbolFactory().newSymbol("emptyBracketType_plus",9, CUP$parser$stack.peek(), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 31: // emptyBracketType ::= INT emptyDimension_star 
            {
                Type RESULT = null;
                int dleft = CUP$parser$stack.peek().left;
                int dright = CUP$parser$stack.peek().right;
                Dimension d = CUP$parser$stack.peek().<Dimension> value();
                RESULT = new Type(true,d); 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("emptyBracketType",10, CUP$parser$stack.elementAt(CUP$parser$top-1), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 32: // emptyBracketType ::= BOOL emptyDimension_star 
            {
                Type RESULT = null;
                int dleft = CUP$parser$stack.peek().left;
                int dright = CUP$parser$stack.peek().right;
                Dimension d = CUP$parser$stack.peek().<Dimension> value();
                RESULT = new Type(false,d); 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("emptyBracketType",10, CUP$parser$stack.elementAt(CUP$parser$top-1), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 33: // emptyDimension_star ::= emptyDimension_star OPEN_BRACKET CLOSE_BRACKET 
            {
                Dimension RESULT = null;
                int dsleft = CUP$parser$stack.elementAt(CUP$parser$top-2).left;
                int dsright = CUP$parser$stack.elementAt(CUP$parser$top-2).right;
                Dimension ds = CUP$parser$stack.elementAt(CUP$parser$top-2).<Dimension> value();
                
    ds.increment();
    ds.foundEmpty = true;
    ds.indices.add(null);
    RESULT = ds;
                CUP$parser$result = parser.getSymbolFactory().newSymbol("emptyDimension_star",11, CUP$parser$stack.elementAt(CUP$parser$top-2), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 34: // emptyDimension_star ::= emptyDimension_star OPEN_BRACKET INTEGER_LITERAL CLOSE_BRACKET 
            {
                Dimension RESULT = null;
                int dsleft = CUP$parser$stack.elementAt(CUP$parser$top-3).left;
                int dsright = CUP$parser$stack.elementAt(CUP$parser$top-3).right;
                Dimension ds = CUP$parser$stack.elementAt(CUP$parser$top-3).<Dimension> value();
                int ileft = CUP$parser$stack.elementAt(CUP$parser$top-1).left;
                int iright = CUP$parser$stack.elementAt(CUP$parser$top-1).right;
                Long i = CUP$parser$stack.elementAt(CUP$parser$top-1).<Long> value();
                
        if (ds.foundEmpty) throw new Error("Filled index after empty");
        ds.allEmpty = false;
        IntLiteral iL = new IntLiteral(i,true);
        ds.indices.add(iL.getLong());
        ds.increment(); // [][2]
        RESULT = ds;
                CUP$parser$result = parser.getSymbolFactory().newSymbol("emptyDimension_star",11, CUP$parser$stack.elementAt(CUP$parser$top-3), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 35: // emptyDimension_star ::= 
            {
                Dimension RESULT = null;
                RESULT = new Dimension(0); 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("emptyDimension_star",11, CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /* . . . . . .*/
        default:
            throw new Exception(
                  "Invalid action number " + CUP$parser$act_num + " found in internal parse table");

        }
    } /* end of method */

    /** Method splitting the generated action code into several parts. */
    public final java_cup.runtime.Symbol CUP$parser$do_action(
            int                        CUP$parser$act_num,
            java_cup.runtime.lr_parser CUP$parser$parser,
            java.util.Stack<java_cup.runtime.Symbol> CUP$parser$stack,
            int                        CUP$parser$top)
            throws java.lang.Exception {
            return CUP$parser$do_action_part00000000(
                           CUP$parser$act_num,
                           CUP$parser$parser,
                           CUP$parser$stack,
                           CUP$parser$top);
    }
}

}
