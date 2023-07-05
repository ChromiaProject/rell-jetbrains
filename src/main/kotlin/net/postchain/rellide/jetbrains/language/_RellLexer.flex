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
ML_COMMENT="/"\*([^*]|[\r\n]|(\*+([^*/]|[\r\n])))*\*+"/"
WS=(' '|'\t'|'\r'|'\n')+
ID=[a-zA-Z_$][a-zA-Z_$0-9]*
DECNUM=[0-9]+
HEXDIG=[0-9]|[A-F]|[a-f]
BYTES=x(('[_0-9a-fA-F]+')|(\"[_0-9a-fA-F]+\"))
STRBAD=\\|'\u0000' .. '\u001F'
STRING=(\"([^\"\r\n\\]|\\.)*\")|('([^'\r\n\\]|\\.)*')
DECIMAL=[-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?
COMMON_INT={DECNUM}| '0' 'x' {HEXDIG}+
BIG_INTEGER={COMMON_INT} 'L'

%%
<YYINITIAL> {
  {WHITE_SPACE}         { return WHITE_SPACE; }

  //"limit"               { return X_TK_LIMIT; }
  //"offset"              { return X_TK_OFFSET; }
  "->"                  { return X_TK_ARROW; }
  ")"                   { return X_TK_RPAR; }
  "}"                   { return X_TK_RCURL; }
  "]"                   { return X_TK_RBRACK; }

  "("               { return X_TK_LPAR; }
  "{"               { return X_TK_LCURL ; }
  "["               { return X_TK_LBRACK ; }
  "@"               { return X_TK_AT; }
  "$"               { return X_DOLLAR_EXPR; }
  ":"               { return X_TK_COLON; }
  ";"               { return X_TK_SEMI ; }
  ","               { return X_TK_COMMA ; }
  "."               { return X_TK_DOT ; }
  "?"               { return X_TK_QUESTION ; }
  "?:"               { return X_BINARY_OPERATOR ; }
  "?."               { return X_BASE_EXPR_TAIL_SAFE_MEMBER ; }
  "!!"               { return X_BASE_EXPR_TAIL_NOT_NULL ; }
  "??"               { return X_UNARY_POSTFIX_OPERATOR ; }
  "^"               { return X_TK_CARET ; }

  "=="               { return X_BINARY_OPERATOR ; }
  "!="               { return X_BINARY_OPERATOR ; }
  "<"               { return X_BINARY_OPERATOR ; }
  ">"               { return X_BINARY_OPERATOR ; }
  "<="               { return X_BINARY_OPERATOR ; }
  ">="               { return X_BINARY_OPERATOR ; }
  "==="               { return X_BINARY_OPERATOR ; }
  "!=="               { return X_BINARY_OPERATOR ; }

  "+"               { return X_BINARY_OPERATOR ; }
  "-"               { return X_BINARY_OPERATOR ; }
  "*"               { return X_BINARY_OPERATOR ; }
  "/"               { return X_BINARY_OPERATOR ; }
  "%"               { return X_BINARY_OPERATOR ; }
  "++"               { return X_INCREMENT_OPERATOR ; }
  "--"               { return X_INCREMENT_OPERATOR ; }

  "and"               { return X_BINARY_OPERATOR ; }
  "or"               { return X_BINARY_OPERATOR ; }
  "not"               { return X_UNARY_PREFIX_OPERATOR ; }

  "="               { return X_ASSIGN_OP ; }
  "+="               { return X_ASSIGN_OP ; }
  "-="               { return X_ASSIGN_OP ; }
  "*="               { return X_ASSIGN_OP ; }
  "/="               { return X_ASSIGN_OP ; }
  "%="               { return X_ASSIGN_OP ; }

  "abstract"               { return X_MODIFIER ; }
  "break"               { return X_TK_BREAK ; }
  "class"               { return X_ENTITY_KEYWORD; }
  "continue"               { return X_TK_CONTINUE ; }
  "create"               { return X_TK_CREATE ; }
  "delete"               { return X_TK_DELETE ; }
  "else"               { return X_TK_ELSE ; }
  "entity"               { return X_ENTITY_KEYWORD; }
  "enum"               { return X_TK_ENUM; }
  "false"              { return X_LITERAL_EXPR; }
  "true"              { return X_LITERAL_EXPR; }
  "for"               { return X_TK_FOR ; }
  "function"               { return X_TK_FUNCTION ; }
  "guard"               { return X_TK_GUARD ; }
  "if"               { return X_TK_IF; }
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
  "struct"               { return X_STRUCT_KEYWORD ; }
  "update"               { return X_TK_UPDATE ; }
  "val"               { return X_TK_VAL ; }
  "var"               { return X_VAR_VAL ; }
  "virtual"               { return X_TK_VIRTUAL ; }
  "when"               { return X_TK_WHEN ; }
  "while"               { return X_TK_WHILE ; }

  {SL_COMMENT}          { return SL_COMMENT; }
  {ML_COMMENT}          { return ML_COMMENT; }
  {WS}                  { return WS; }
  {ID}                  { return ID; }
  {DECNUM}              { return DECNUM; }
  {BIG_INTEGER}         { return BIG_INTEGER; }
  {BYTES}               { return BYTES; }
  {DECIMAL}             { return DECIMAL;}
  {STRBAD}              { return STRBAD; }
  {STRING}              { return STRING; }
  {SPACE}               { return SPACE; }
}


[^] { return BAD_CHARACTER; }