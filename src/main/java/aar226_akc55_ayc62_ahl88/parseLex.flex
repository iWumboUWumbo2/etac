package aar226_akc55_ayc62_ahl88;

import aar226_akc55_ayc62_ahl88.Errors.LexicalError;
import java_cup.runtime.*;
import org.apache.commons.text.*;

%%

%public
%class EtaLex

%line
%column
%unicode
%cup

%{
    StringBuilder sb = new StringBuilder();
    int globalLineNum = 0;
    int globalColNum = 0;
    boolean inString = false;

    private Symbol symbol(int type) {
        return new Symbol(type, lineNumber(), column(),yytext());
    }

    private Symbol symbol(int type, Object value) {
        return new Symbol(type, lineNumber(), column(), value);
    }
    private Symbol symbol(int type, int line, int col, Object value) {
        return new Symbol(type, line, col, value);
    }
    // stack overflow example
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static boolean isEscape( int ch, boolean isChar){
        if (ch == 34){
            if (isChar){
                return false;
            }
            return true;
        }
        if (ch == 92 || ch == 10 || ch == 39 ||
        ch == 9 || ch == 8 || ch == 12){
            return true;
        }
        return false;
    }

    public int lineNumber() { return yyline + 1; }
    public int column() { return yycolumn + 1; }

    public Symbol outputChar(int ch){
        if (ch > 0x10FFFF || ch < 0x0){
            throw new LexicalError(lineNumber()
                  ,column(),"Invalid Unicode Character ");
        } else if (isEscape(ch,true)){
            return symbol(sym.CHARACTER_LITERAL,globalLineNum,globalColNum,
            StringEscapeUtils.escapeJava(new String(Character.toChars(ch))));
        }else if (ch >= 0x20 && ch <= 0x7E){
            return symbol(sym.CHARACTER_LITERAL, globalLineNum,globalColNum, new String(Character.toChars(ch)));
        }else{
            return symbol(sym.CHARACTER_LITERAL,globalLineNum,globalColNum,"\\x{"+Integer.toHexString(ch)+ "}");
        }
    }

%}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
Whitespace = {LineTerminator} | [ \t\f]

Letter = [a-zA-Z]
Digit = [0-9]
Identifier = {Letter}({Digit}|{Letter}|_|"'")*
Integer = "0"|[1-9]{Digit}*
Hex = [0-9a-fA-F]{1,6}
Boolean = "true"|"false"

Comment = "//"{InputCharacter}*({LineTerminator}?)

%state CHARACTER
%state STRING
%%

<YYINITIAL> {
    {Whitespace}  { /* ignoring whitespace */ }
    {Comment}     { /* ignoring comment */ }

    // types
    "int"     { return symbol(sym.INT); }
    "bool"    { return symbol(sym.BOOL); }

    "9223372036854775808" {return symbol(sym.MAX_NUM);}

    // need error checking changed to no try catch
    {Integer}     {
          try{
              return symbol(sym.INTEGER_LITERAL, Long.parseLong("-" + yytext()));
          }catch(Exception e){
              throw new LexicalError(lineNumber()
              ,column() ,"invalid Integer");
          }
      }

    {Boolean}     { return symbol(sym.BOOL_LITERAL, Boolean.parseBoolean(yytext())); }

    // keywords
    "use"     { return symbol(sym.USE); }
    "if"      { return symbol(sym.IF); }
    "while"   { return symbol(sym.WHILE); }
    "else"    { return symbol(sym.ELSE); }
    "return"  { return symbol(sym.RETURN); }
    "length"  { return symbol(sym.LENGTH); }

    // operators
    "-"       { return symbol(sym.MINUS); }
    "!"       { return symbol(sym.NOT);   }
    "*"       { return symbol(sym.TIMES); }
    "*>>"     { return symbol(sym.HI_MULT); }
    "/"       { return symbol(sym.DIVIDE); }
    "%"       { return symbol(sym.MODULO); }
    "+"       { return symbol(sym.PLUS); }
    "<"       { return symbol(sym.LT); }
    "<="      { return symbol(sym.LEQ); }
    ">"       { return symbol(sym.GT); }
    ">="      { return symbol(sym.GEQ); }
    "=="      { return symbol(sym.EQUAL); }
    "!="      { return symbol(sym.NOT_EQUAL); }
    "&"       { return symbol(sym.AND); }
    "|"       { return symbol(sym.OR); }

    // seperators
    ":"       { return symbol(sym.COLON); }
    ";"       { return symbol(sym.SEMICOLON); }
    ","       { return symbol(sym.COMMA); }
    "("       { return symbol(sym.OPEN_PAREN); }
    ")"       { return symbol(sym.CLOSE_PAREN); }
    "["       { return symbol(sym.OPEN_BRACKET); }
    "]"       { return symbol(sym.CLOSE_BRACKET); }
    "{"       { return symbol(sym.OPEN_BRACE); }
    "}"       { return symbol(sym.CLOSE_BRACE); }

    // other
    "="       { return symbol(sym.GETS); }
    "_"       { return symbol(sym.UNDERSCORE); }

    \'      {   globalLineNum = lineNumber();
                globalColNum = column();
                yybegin(CHARACTER); }
    \"      {   globalLineNum = lineNumber();
                globalColNum = column();
                inString = true;
                sb.setLength(0);
                yybegin(STRING); }

    {Identifier}  { globalLineNum = lineNumber();
                    globalColNum = column();
                    return symbol(sym.IDENTIFIER,globalLineNum, globalColNum,yytext()); }
}
<CHARACTER> {
    \"\'   { yybegin(YYINITIAL);
          return symbol(sym.CHARACTER_LITERAL, StringEscapeUtils.escapeJava("\""));
      }
    \\\"\'   { yybegin(YYINITIAL);
          return symbol(sym.CHARACTER_LITERAL, StringEscapeUtils.escapeJava("\""));
      }
    [^\n\r\'\\]\' {
        yybegin(YYINITIAL);
        byte[] bytearr = yytext().substring(0,yytext().length()-1).getBytes("UTF-32");
        int ch = Integer.parseInt(String.valueOf(bytesToHex(bytearr)),16);
        return outputChar(ch);
      }
    \\n\' {
          yybegin(YYINITIAL);
          return symbol(sym.CHARACTER_LITERAL, StringEscapeUtils.escapeJava("\n"));
      }
    \\t\' {
              yybegin(YYINITIAL);
              return symbol(sym.CHARACTER_LITERAL, StringEscapeUtils.escapeJava("\t"));
          }

    \\\\\' {
              yybegin(YYINITIAL);
              return symbol(sym.CHARACTER_LITERAL, StringEscapeUtils.escapeJava("\\"));
          }

    \\\'\' {
             yybegin(YYINITIAL);
             return symbol(sym.CHARACTER_LITERAL, StringEscapeUtils.escapeJava("'"));
         }
    \\x\{{Hex}\}\'     {
                yybegin(YYINITIAL);
                int ch = Integer.parseInt(yytext().substring(3, yytext().length() - 2), 16);
                return outputChar(ch);
          }
    \\.\'           { yybegin(YYINITIAL);
                        throw new LexicalError(globalLineNum,globalColNum,"invalid escape character " + yytext());}
    [^] {
          yybegin(YYINITIAL);
          throw new LexicalError(globalLineNum,globalColNum,"Invalid character constant " + yytext());
      }
}
<STRING> {
    {LineTerminator} {
          yybegin(YYINITIAL);
          throw new LexicalError(globalLineNum ,globalColNum ,"Unterminated string");
      }

    \"  {
          yybegin(YYINITIAL);
          String s = sb.toString();
          inString = false;
          return symbol(sym.STRING_LITERAL,globalLineNum, globalColNum, s);
      }
    \'   { sb.append("\\" + StringEscapeUtils.escapeJava("'"));}
    \\\' { sb.append("\\" + StringEscapeUtils.escapeJava("'")); }
    \\\\ { sb.append(StringEscapeUtils.escapeJava("\\")); }
    \\n  { sb.append(StringEscapeUtils.escapeJava("\n"));}
    \\t  { sb.append(StringEscapeUtils.escapeJava("\t"));}
    \\\"  { sb.append("\""); }

    \\x\{{Hex}\} {
                                 int ch = Integer.parseInt(yytext().substring(3, yytext().length() - 1), 16);
                                 if (ch > 0x10FFFF || ch < 0x0){
                                     throw new LexicalError(lineNumber(),column() ,"Invalid Unicode Character ");
                                 }else if (isEscape(ch,false)){
                                    sb.append(StringEscapeUtils.escapeJava(new String(Character.toChars(ch))));
                                 }else if (ch >= 0x20 && ch <= 0x7E){
                                    sb.append(Character.toChars(ch));
                                 }else{
                                    sb.append("\\x{"+Integer.toHexString(ch)+ "}");
                                 }
                           }
    \\.           { yybegin(YYINITIAL);
                        throw new LexicalError(globalLineNum,globalColNum,"invalid escape character " + yytext());}
    [^\"] {
        byte[] bytearr = yytext().getBytes("UTF-32");
        int ch = Integer.parseInt(String.valueOf(bytesToHex(bytearr)),16);
        if (ch > 0x10FFFF || ch < 0x0){
            throw new LexicalError(lineNumber(),column(),"Invalid Unicode Character ");
        }else if (isEscape(ch,false)){
            sb.append("\\x{"+Integer.toHexString(ch)+ "}");
        }else if (ch >= 0x20 && ch <= 0x7E){
           sb.append(new String(Character.toChars(ch)));
        }else{
           sb.append("\\x{"+Integer.toHexString(ch)+ "}");
        }
    }
}
<<EOF>> {   if (inString){
            throw new LexicalError(globalLineNum,globalColNum,"Unterminated string");
            }
            return symbol(sym.EOF); }
/* error */
[^]     { throw new LexicalError(lineNumber(),column() ,
      "Illegal character <"+yytext()+">"); }