package net.postchain.rellide.jetbrains.language;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static net.postchain.rellide.jetbrains.language.psi.RellTypes.*;

%%

%{
  public _RellLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _RellLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+

SPACE=[ \t\n\x0B\f\r]+
BOOLEANLITERAL=true|false
SL_COMMENT="//".*
ML_COMMENT="/"\*(.|\n)*\*"/"
WS=(' '|'\t'|'\r'|'\n')+
ID=[a-zA-Z_$][a-zA-Z_$0-9]*
DECNUM=('0'..'9')+
HEXDIG='0'..'9'|'A'..'F'|'a'..'f'
BYTES=x(('[_0-9a-fA-F]+')|(\"[_0-9a-fA-F]+\"))
STRBAD=\\|'\u0000' .. '\u001F'
STRING=(\"([^\"\r\n\\]|\\.)*\")|('([^'\r\n\\]|\\.)*')


%%
<YYINITIAL> {
  {WHITE_SPACE}         { return WHITE_SPACE; }

  "("                   { return X_TK_LPAR; }
  //")"                   { return X_TK_RPAR ; }
  "{"               { return X_TK_LCURL ; }
  //"}"               { return RCURL ; }
  "["               { return X_TK_LBRACK ; }
  //"]"               { return RBRACK ; }
  "@"               { return X_TK_AT; }
  "$"               { return X_DOLLAR_EXPR; }
  ":"               { return X_TK_COLON; }
  ";"               { return X_TK_SEMI ; }
  ","               { return X_TK_COMMA ; }
  "."               { return X_TK_DOT ; }
  "?:"               { return X_BINARY_OPERATOR ; }
  "?."               { return X_BINARY_OPERATOR ; }
  "!!"               { return X_BASE_EXPR_TAIL_NOT_NULL ; }
  "?"               { return X_TK_QUESTION ; }
  "??"               { return X_UNARY_POSTFIX_OPERATOR ; }
 // "->"               { return ARROW ; }
  "^"               { return X_TK_CARET ; }

  "=="               { return X_BINARY_OPERATOR ; }
  "!="               { return X_BINARY_OPERATOR ; }
  "<"               { return X_BINARY_OPERATOR ; }
  ">"               { return X_BINARY_OPERATOR ; }
  "<="               { return X_BINARY_OPERATOR ; }
  ">="               { return X_BINARY_OPERATOR ; }
  "==="               { return X_BINARY_OPERATOR ; }
  "!=="               { return X_BINARY_OPERATOR ; }

  "+"               { return X_TK_PLUS ; }
  "-"               { return X_BINARY_OPERATOR ; }
  "*"               { return X_TK_MUL ; }
  "/"               { return X_BINARY_OPERATOR ; }
  "%"               { return X_BINARY_OPERATOR ; }
  "++"               { return X_INCREMENT_OPERATOR ; }
  "--"               { return X_INCREMENT_OPERATOR ; }

  "and"               { return X_BINARY_OPERATOR ; }
  "or"               { return X_BINARY_OPERATOR ; }
  "not"               { return X_BINARY_OPERATOR ; }

  "="               { return X_ASSIGN_OP ; }
  "+="               { return X_ASSIGN_OP ; }
  "-="               { return X_ASSIGN_OP ; }
  "*="               { return X_ASSIGN_OP ; }
  "/="               { return X_ASSIGN_OP ; }
  "%="               { return X_ASSIGN_OP ; }

  //"abstract"               { return ABSTRACT ; }
  "break"               { return X_TK_BREAK ; }
  "class"               { return X_ENTITY_KEYWORD; }
  "continue"               { return X_TK_CONTINUE ; }
  "create"               { return X_TK_CREATE ; }
  "delete"               { return X_TK_DELETE ; }
  "else"               { return X_WHEN_CONDITION_ELSE; }
  "entity"               { return X_ENTITY_DEF; }
  "enum"               { return X_TK_ENUM; }
  "for"               { return X_TK_FOR ; }
  "function"               { return X_TK_FUNCTION ; }
  "guard"               { return X_TK_GUARD ; }
  "if"               { return X_TK_IF ; }
  "import"               { return X_TK_IMPORT ; }
  "in"               { return X_TK_IN ; }
  "include"               { return X_TK_INCLUDE ; }
  "index"               { return X_KEY_INDEX_KIND ; }
  "key"               { return X_KEY_INDEX_KIND ; }
  "limit"               { return X_AT_EXPR_LIMIT ; }
  "module"               { return X_TK_MODULE ; }
  "mutable"               { return X_TK_MUTABLE ; }
  "namespace"               { return X_TK_NAMESPACE ; }
  "null"               { return X_NULL_LITERAL_EXPR ; }
  "object"               { return X_TK_OBJECT ; }
  "offset"               { return X_AT_EXPR_OFFSET ; }
  "operation"               { return X_TK_OPERATION ; }
  "override"               { return X_MODIFIER ; }
  "query"               { return X_TK_QUERY ; }
  "record"               { return X_STRUCT_KEYWORD ; }
  "return"               { return X_TK_RETURN ; }
  "struct"               { return X_TK_STRUCT ; }

  "update"               { return X_TK_UPDATE ; }
  "val"               { return X_TK_VAL ; }
  "var"               { return X_VAR_VAL ; }
  "virtual"               { return X_TK_VIRTUAL ; }
  "when"               { return X_TK_WHEN ; }
  "while"               { return X_TK_WHILE ; }

  {BOOLEANLITERAL}      { return BOOLEANLITERAL; }
  {SL_COMMENT}          { return SL_COMMENT; }
  {ML_COMMENT}          { return ML_COMMENT; }
  {WS}                  { return WS; }
  {ID}                  { return ID; }
  {DECNUM}              { return DECNUM; }
  {HEXDIG}              { return HEXDIG; }
  {BYTES}               { return BYTES; }
  {STRBAD}              { return STRBAD; }
  {STRING}              { return STRING; }
  {SPACE}               { return SPACE; }
}


[^] { return BAD_CHARACTER; }
