
//----------------------------------------------------
// The following code was generated by CUP v0.11b 20150326
//----------------------------------------------------

package aar226_akc55_ayc62_ahl88;

import java_cup.runtime.*;

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
    "\000\002\000\002\002\004\000\002\002\024" });

  /** Access to production table. */
  @Override
  public short[][] production_table() {return _production_table;}

  /** Parse-action table. */
  protected static final short[][] _action_table = 
    unpackFromStrings(new String[] {
    "\000\025\000\004\021\004\001\002\000\004\004\007\001" +
    "\002\000\004\002\006\001\002\000\004\002\001\001\002" +
    "\000\004\004\010\001\002\000\004\044\011\001\002\000" +
    "\004\004\012\001\002\000\004\050\013\001\002\000\004" +
    "\012\014\001\002\000\004\042\015\001\002\000\004\043" +
    "\016\001\002\000\004\042\017\001\002\000\004\043\020" +
    "\001\002\000\004\045\021\001\002\000\004\046\022\001" +
    "\002\000\004\004\023\001\002\000\004\044\024\001\002" +
    "\000\004\005\025\001\002\000\004\045\026\001\002\000" +
    "\004\047\027\001\002\000\004\002\000\001\002" });

  /** Access to parse-action table. */
  @Override
  public short[][] action_table() {return _action_table;}

  /** {@code reduce_goto} table. */
  protected static final short[][] _reduce_table = 
    unpackFromStrings(new String[] {
    "\000\025\000\004\002\004\001\001\000\002\001\001\000" +
    "\002\001\001\000\002\001\001\000\002\001\001\000\002" +
    "\001\001\000\002\001\001\000\002\001\001\000\002\001" +
    "\001\000\002\001\001\000\002\001\001\000\002\001\001" +
    "\000\002\001\001\000\002\001\001\000\002\001\001\000" +
    "\002\001\001\000\002\001\001\000\002\001\001\000\002" +
    "\001\001\000\002\001\001\000\002\001\001" });

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

    /** Method with the actual generated action code for actions 0 to 1. */
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
                Object start_val = CUP$parser$stack.elementAt(CUP$parser$top-1).<Object> value();
                RESULT = start_val;
                CUP$parser$result = parser.getSymbolFactory().newSymbol("$START",0, CUP$parser$stack.elementAt(CUP$parser$top-1), CUP$parser$stack.peek(), RESULT);
            }
            /* ACCEPT */
            CUP$parser$parser.done_parsing();
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 1: // program ::= USE IDENTIFIER IDENTIFIER OPEN_PAREN IDENTIFIER COLON INT OPEN_BRACKET CLOSE_BRACKET OPEN_BRACKET CLOSE_BRACKET CLOSE_PAREN OPEN_BRACE IDENTIFIER OPEN_PAREN STRING_LITERAL CLOSE_PAREN CLOSE_BRACE 
            {
                Object RESULT = null;

                CUP$parser$result = parser.getSymbolFactory().newSymbol("program",0, CUP$parser$stack.elementAt(CUP$parser$top-17), CUP$parser$stack.peek(), RESULT);
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
