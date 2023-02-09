
package aar226_akc55_ayc62_ahl88;


import java_cup.runtime.*;

%%

%class Lexer

%line
%column

%cup

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }

    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
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

Comment = "//"{InputCharacter}*{LineTerminator}

%state CHARACTER
%state STRING
%%

<YYINITIAL> {
    {Whitespace}  { /* ignoring whitespace */ }
    {Comment}     { /* ignoring comment */ }

    // types
    "int"     { return symbol(sym.INT); }
    "bool"    { return symbol(sym.BOOL); }

    // need error checking changed to no try catch
    {Integer}     { return symbol(sym.INTEGER_LITERAL,Long.parseLong("-" + yytext()));}

    "false"       { return symbol(sym.FALSE); }
    "true"        { return symbol(sym.TRUE); }

    // keywords
//    "use"     { return new Token(TokenType.USE); }
//    "if"      { return new Token(TokenType.IF); }
//    "while"   { return new Token(TokenType.WHILE); }
//    "else"    { return new Token(TokenType.ELSE); }
//    "return"  { return new Token(TokenType.RETURN); }
//    "length"  { return new Token(TokenType.LENGTH); }

    // operators
    "-"       { return symbol(sym.MINUS); }
    "!"       { return symbol(sym.NOT);   }
    "*"       { return symbol(sym.TIMES); }
//    "*>>"     { return new Token(TokenType.HI_MULT); }
//    "/"       { return new Token(TokenType.DIV); }
//    "%"       { return new Token(TokenType.MOD); }
//    "+"       { return new Token(TokenType.PLUS); }
//    "<"       { return new Token(TokenType.LT); }
//    "<="      { return new Token(TokenType.LEQ); }
//    ">="      { return new Token(TokenType.GEQ); }
//    ">"       { return new Token(TokenType.GT); }
//    "=="      { return new Token(TokenType.EQ); }
//    "!="      { return new Token(TokenType.NEQ); }
//    "&"       { return new Token(TokenType.LAND); }
//    "|"       { return new Token(TokenType.LOR); }

    // seperators
//    ":"       { return new Token(TokenType.COLON); }
//    ";"       { return new Token(TokenType.SEMICOLON); }
//    ","       { return new Token(TokenType.COMMA); }
    "("       { return symbol(sym.OPEN_PAREN); }
    ")"       { return symbol(sym.CLOSE_PAREN); }
//    "["       { return new Token(TokenType.LBRACKET); }
//    "]"       { return new Token(TokenType.RBRACKET); }
//    "{"       { return new Token(TokenType.LCURLY); }
//    "}"       { return new Token(TokenType.RCURLY); }

    // other
//    "="       { return new Token(TokenType.ASSIGN); }
//    "_"       { return new Token(TokenType.THROWAWAY); }
}

<<EOF>> { return symbol(sym.EOF); }
/* error */
[^]                    { throw new Error("Illegal character <"+yytext()+">"); }