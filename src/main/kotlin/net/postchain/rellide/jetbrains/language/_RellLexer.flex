package net.postchain.rellide.jetbrains.language;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.ERROR_ELEMENT;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static net.postchain.rellide.jetbrains.language.psi.RellTypes.*;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.math.RoundingMode;

%%

%{
  private static final int MAX_ALLOWED_DIGITS = 131072;
  private static final int MAX_ALLOWED_HEX_DIGITS = 999;
  private static final BigInteger MAX_VALUE = BigInteger.TEN.pow(MAX_ALLOWED_DIGITS);
  private static final BigInteger BIG_INTEGER_MAX_VALUE = MAX_VALUE.subtract(BigInteger.ONE);
  private static final BigInteger BIG_INTEGER_MIN_VALUE = MAX_VALUE.add(BigInteger.ONE).negate();
  private static final int DECIMAL_FRAC_DIGITS = 20;
  private static final int DECIMAL_PRECISION = MAX_ALLOWED_DIGITS + DECIMAL_FRAC_DIGITS;
  private static final BigDecimal DECIMAL_MIN_VALUE = BigDecimal.ONE.divide(BigDecimal.TEN.pow(DECIMAL_FRAC_DIGITS));
  private static final BigDecimal DECIMAL_MAX_VALUE = BigDecimal.TEN.pow(DECIMAL_PRECISION).subtract(BigDecimal.ONE)
          .divide(BigDecimal.TEN.pow(DECIMAL_FRAC_DIGITS));
  private static final int MAX_DECIMAL_LITERAL_LENGTH = 1000;
  private static final BigDecimal POSITIVE_MIN = BigDecimal.ONE.divide(BigDecimal.TEN.pow(DECIMAL_FRAC_DIGITS + 1));
  private static final BigDecimal NEGATIVE_MIN = POSITIVE_MIN.negate();
  private static final BigDecimal UPPER_LIMIT = BigDecimal.TEN.pow(MAX_ALLOWED_DIGITS);
  private static final BigDecimal LOWER_LIMIT = UPPER_LIMIT.negate();

  public _RellLexer() {
    this((java.io.Reader)null);
  }
%}

%{
    public boolean hasHexPrefix(String text) {
        return text.startsWith("0x");
    }

    public boolean isIntegerOutOfRange(String text) {
        try {
            boolean isHex = hasHexPrefix(text);
            if (isHex) {
                Long.parseLong(text.substring(2), 16);
            } else {
                Long.parseLong(text, 10);
            }
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    public boolean isBigIntegerOutOfRange(String text) {
        boolean isHex = hasHexPrefix(text);
        if (text.length() > MAX_ALLOWED_HEX_DIGITS) {
            return true;
        }
        BigInteger parsedBigInteger = isHex ? new BigInteger(text.substring(2), 16) : new BigInteger(text, 10);
        return parsedBigInteger.compareTo(BIG_INTEGER_MAX_VALUE) == 1 ||
               parsedBigInteger.compareTo(BIG_INTEGER_MIN_VALUE) == -1;
    }

    public BigDecimal scale(BigDecimal v) {
        BigDecimal t = v;
        if (t.compareTo(NEGATIVE_MIN) >= 0 && t.compareTo(POSITIVE_MIN) <= 0) {
            return BigDecimal.ZERO;
        } else if (t.compareTo(LOWER_LIMIT) <= 0 || t.compareTo(UPPER_LIMIT) >= 0) {
            return null;
        }

        int scale = t.scale();
        if (scale > DECIMAL_FRAC_DIGITS) {
            t = v.setScale(DECIMAL_FRAC_DIGITS, RoundingMode.HALF_UP);
            if (t.compareTo(LOWER_LIMIT) <= 0 || t.compareTo(UPPER_LIMIT) >= 0) {
                return null;
            }
        }
        return t;
    }

    public boolean isDecimalOutOfRange(String text) {
        int len = text.length();
        if (len > MAX_DECIMAL_LITERAL_LENGTH) {
            return true;
        }
        BigDecimal parsedDecimal = null;
        try {
            parsedDecimal = new BigDecimal(text);
        } catch (NumberFormatException e) {
            return true;
        }
        return scale(parsedDecimal) == null;
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
SL_COMMENT="//".*
ML_COMMENT="/"\*([^*]|[\r\n]|(\*+([^*/]|[\r\n])))*\*+"/"
WS=[ \t\r\n]+
ID=[a-zA-Z_][a-zA-Z_0-9]*
DECNUM=[0-9]+
HEXDIG=[0-9A-Fa-f]
BYTES=x(('([_0-9a-fA-F][_0-9a-fA-F])*')|(\"([_0-9a-fA-F][_0-9a-fA-F])*\"))
STRBAD=\\|'\u0000' .. '\u001F'
STRING=(\"(\t|\\[btnfr\"'\\]|\\u[0-9A-Fa-f]{4}|[^\"\\\u0000-\u001F])*\")|('(\t|\\[btnfr\"'\\]|\\u[0-9A-Fa-f]{4}|[^\'\\\u0000-\u001F])*')
STRING_NOT_CLOSED=(\"(\t|\\[btnfr\"'\\]|\\u[0-9A-Fa-f]{4}|[^\"\\\u0000-\u001F])*)|('(\t|\\[btnfr\"'\\]|\\u[0-9A-Fa-f]{4}|[^\'\\\u0000-\u001F])*)
INVALID_DECIMAL=[0-9]+\.[a-zA-Z]+
DECIMAL=[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?
COMMON_INT={DECNUM}| '0' 'x' {HEXDIG}+
BIG_INTEGER=([0-9]+|0x[0-9A-Fa-f]+)L
HEXDIGNUM=0[ \t\n\x0B\f\r]*x[ \t\n\x0B\f\r]*[0-9A-Fa-f]+

%%
<YYINITIAL> {
  {WHITE_SPACE}         { return WHITE_SPACE; }

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
  {INVALID_DECIMAL}     { return ERROR_ELEMENT; }
  {ID}                  { return ID; }
  {BIG_INTEGER}         {
                            yypushback(1);
                            String matched = yytext().toString();
                            if (isBigIntegerOutOfRange(matched)) {
                                return ERROR_ELEMENT;
                            }
                            return DECNUM;
                        }
  {DECNUM}              {
                            if (isIntegerOutOfRange(yytext().toString())) {
                                return ERROR_ELEMENT;
                            }
                            return DECNUM;
                        }
  {HEXDIGNUM}           {
                            if (isIntegerOutOfRange(yytext().toString())) {
                                return ERROR_ELEMENT;
                            }
                            return HEXDIGNUM;
                        }
  {BYTES}               { return BYTES; }
  {DECIMAL}             {
                            if (isDecimalOutOfRange(yytext().toString())) {
                                return ERROR_ELEMENT;
                            }
                            return DECIMAL;
                        }
  {STRING_NOT_CLOSED}   { return STRING_NOT_CLOSED; }
  {STRBAD}              { return STRBAD; }
  {STRING}              { return STRING; }
  {SPACE}               { return SPACE; }
}


[^] { return BAD_CHARACTER; }