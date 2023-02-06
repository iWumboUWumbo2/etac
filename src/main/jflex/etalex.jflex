package aar226_akc55_ayc62_ahl88;
import org.apache.commons.text.*;
%%

%public
%class EtaLexer
%type Token
%function nextToken

%line
%column
%unicode
//%pack

%{
    enum TokenType {
      // keywords
      USE,
      IF,
      WHILE,
      ELSE,
      RETURN,
      LENGTH,

      ID,

      INT_LITERAL,
      INT_TYPE,

      BOOL_LITERAL,
      BOOL_TYPE,

      CHAR_LITERAL,
      STRING_LITERAL,

      MINUS,
      NOT,
      MULT,
      HI_MULT,
      DIV,
      MOD,
      PLUS,
      LT,
      LEQ,
      GEQ,
      GT,
      EQ,
      NEQ,
      LAND,
      LOR,

      COLON,
      SEMICOLON,
      COMMA,
      LPARA,
      RPARA,
      LBRACKET,
      RBRACKET,
      LCURLY,
      RCURLY,

      ASSIGN,
      THROWAWAY,

      ERROR
    }

    StringBuilder sb = new StringBuilder();
    int globalLineNum = 0;
    int globalColNum = 0;

    class Token {
        TokenType type;
        Object attribute;
        String text;
        int line;
        int col;

        Token(TokenType tt) {
            type = tt; attribute = null;
            this.text = yytext();
            this.line = lineNumber();
            this.col = column();
        }
        // added string consturctor
        Token(String text, TokenType tt, String attr) {
//            System.out.println("In string constructor");
            type = tt; attribute = attr;
            this.text = text + " " + attr;
            this.line = globalLineNum;
            this.col = globalColNum;
        }
        Token(String text, TokenType tt, Object attr) {
//            System.out.println("In general Constructor");
            type = tt; attribute = attr;
            this.text = text + " " + yytext();
            this.line = lineNumber();
            this.col = column();
        }
        Token(String errorString) {
            type = TokenType.ERROR;
            attribute = null;
            this.text = errorString;
            this.line = lineNumber();
            this.col = column();
        }
        Token(String errorString, String failedString) {
            type = TokenType.ERROR;
            attribute = null;
            this.text = errorString;
            this.line = globalLineNum;
            this.col = globalColNum;
        }
        public String toString() {
            return "" + type + "(" + attribute + ")";
        }
    }
    public int lineNumber() { return yyline + 1; }
    public int column() { return yycolumn + 1; }
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

Comment = "//"{InputCharacter}*{LineTerminator}

%state CHARACTER
%state STRING

%%

<YYINITIAL> {
    {Whitespace}  { /* ignoring whitespace */ }
    {Comment}     { /* ignoring comment */ }

    // types
    "int"     { return new Token(TokenType.INT_TYPE); }
    "bool"    { return new Token(TokenType.BOOL_TYPE); }

    // need error checking

    {Integer}     {
                try {

                    String temp = "-" + yytext();
                    //System.out.println(Long.parseLong(temp)); // MUST REMEMBER INTEGERS ARE NEGATIVE
                    return new Token("integer", TokenType.INT_LITERAL, Long.parseLong(temp));
                } catch (Exception e) {
                    return new Token("Integer parse error");
                }
           }
    {Boolean}     { return new Token("boolean", TokenType.BOOL_LITERAL, Boolean.parseBoolean(yytext())); }

    // keywords
    "use"     { return new Token(TokenType.USE); }
    "if"      { return new Token(TokenType.IF); }
    "while"   { return new Token(TokenType.WHILE); }
    "else"    { return new Token(TokenType.ELSE); }
    "return"  { return new Token(TokenType.RETURN); }
    "length"  { return new Token(TokenType.LENGTH); }

    // operators
    "-"       { return new Token(TokenType.MINUS); }
    "!"       { return new Token(TokenType.NOT); }
    "*"       { return new Token(TokenType.MULT); }
    "*>>"     { return new Token(TokenType.HI_MULT); }
    "/"       { return new Token(TokenType.DIV); }
    "%"       { return new Token(TokenType.MOD); }
    "+"       { return new Token(TokenType.PLUS); }
    "<"       { return new Token(TokenType.LT); }
    "<="      { return new Token(TokenType.LEQ); }
    ">="      { return new Token(TokenType.GEQ); }
    ">"       { return new Token(TokenType.GT); }
    "=="      { return new Token(TokenType.EQ); }
    "!="      { return new Token(TokenType.NEQ); }
    "&"       { return new Token(TokenType.LAND); }
    "|"       { return new Token(TokenType.LOR); }

    // seperators
    ":"       { return new Token(TokenType.COLON); }
    ";"       { return new Token(TokenType.SEMICOLON); }
    ","       { return new Token(TokenType.COMMA); }
    "("       { return new Token(TokenType.LPARA); }
    ")"       { return new Token(TokenType.RPARA); }
    "["       { return new Token(TokenType.LBRACKET); }
    "]"       { return new Token(TokenType.RBRACKET); }
    "{"       { return new Token(TokenType.LCURLY); }
    "}"       { return new Token(TokenType.RCURLY); }

    // other
    "="       { return new Token(TokenType.ASSIGN); }
    "_"       { return new Token(TokenType.THROWAWAY); }

    \'      { globalLineNum = lineNumber(); globalColNum = column(); yybegin(CHARACTER); }
    \"      { globalLineNum = lineNumber(); globalColNum = column(); sb.setLength(0); yybegin(STRING); }

    {Identifier}  { globalLineNum = lineNumber(); globalColNum = column();
          return new Token("id", TokenType.ID, yytext()); }

    // might need catch all case for extra failures
}

<CHARACTER> {
    [^\n\r\'\\]\' {
          yybegin(YYINITIAL);
          return new Token("character", TokenType.CHAR_LITERAL, Character.toString(yytext().charAt(0)));
      }
    \\n\' {
          yybegin(YYINITIAL);
          return new Token("character", TokenType.CHAR_LITERAL, StringEscapeUtils.escapeJava("\n"));
      }
    \\t\' {
              yybegin(YYINITIAL);
              return new Token("character", TokenType.CHAR_LITERAL, StringEscapeUtils.escapeJava("\t"));
          }

    \\\\\' {
              yybegin(YYINITIAL);
              return new Token("character", TokenType.CHAR_LITERAL, StringEscapeUtils.escapeJava("\\"));
          }

    \\\'\' {
             yybegin(YYINITIAL);
             return new Token("character", TokenType.CHAR_LITERAL, StringEscapeUtils.escapeJava("'"));
         }
    \\x\{{Hex}\}\'     {
                yybegin(YYINITIAL);
                int ch = Integer.parseInt(yytext().substring(3, yytext().length() - 2), 16);
                if (ch > 0x10FFFF || ch < 0x0)
                    return new Token("Invalid Unicode Character", "outside of Unicode Range Char");
                return new Token("character", TokenType.CHAR_LITERAL,
                StringEscapeUtils.escapeJava(new String(Character.toChars(ch))));
          }
    [^] {
          yybegin(YYINITIAL);
          return new Token("Invalid character constant",yytext());
      }
}

<STRING> {
    {LineTerminator} {
          yybegin(YYINITIAL);
          return new Token("Unterminated string",yytext());
      }

    \"  {
          yybegin(YYINITIAL);
          String s = sb.toString();
          return new Token("string", TokenType.STRING_LITERAL, s);
      }

    \\\"  { sb.append("\""); }

    \\x\{{Hex}\} {
                                 int ch = Integer.parseInt(yytext().substring(3, yytext().length() - 1), 16);
                                 if (ch > 0x10FFFF || ch < 0x0)
                                     return new Token("Invalid Unicode Character","outside of Unicode Range String");
                                 sb.append(Character.toChars(ch));
                           }

    [^\"] { sb.append(yytext()); }
}