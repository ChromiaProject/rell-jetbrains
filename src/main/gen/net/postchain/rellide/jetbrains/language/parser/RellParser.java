// This is a generated file. Not intended for manual editing.
package net.postchain.rellide.jetbrains.language.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static net.postchain.rellide.jetbrains.language.psi.RellTypes.*;
import static net.postchain.rellide.jetbrains.language.parser.RellParserUtil.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class RellParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return X_RootParser(b, l + 1);
  }

  /* ********************************************************** */
  // COMMON_INT 'L'
  public static boolean BIG_INTEGER(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BIG_INTEGER")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BIG_INTEGER, "<big integer>");
    r = COMMON_INT(b, l + 1);
    r = r && consumeToken(b, "L");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // DECNUM | '0' 'x' HEXDIG+
  public static boolean COMMON_INT(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "COMMON_INT")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COMMON_INT, "<common int>");
    r = consumeToken(b, DECNUM);
    if (!r) r = COMMON_INT_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '0' 'x' HEXDIG+
  private static boolean COMMON_INT_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "COMMON_INT_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "0");
    r = r && consumeToken(b, "x");
    r = r && COMMON_INT_1_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // HEXDIG+
  private static boolean COMMON_INT_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "COMMON_INT_1_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, HEXDIG);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, HEXDIG)) break;
      if (!empty_element_parsed_guard_(b, "COMMON_INT_1_2", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // COMMON_INT
  public static boolean NUMBER(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NUMBER")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NUMBER, "<number>");
    r = COMMON_INT(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '\t' | '\\' ('b'|'t'|'n'|'f'|'r'|'"'|"'"|'\\' | 'u' HEXDIG HEXDIG HEXDIG HEXDIG)
  public static boolean STRCHAR(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "STRCHAR")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STRCHAR, "<strchar>");
    r = consumeToken(b, "\\t");
    if (!r) r = STRCHAR_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '\\' ('b'|'t'|'n'|'f'|'r'|'"'|"'"|'\\' | 'u' HEXDIG HEXDIG HEXDIG HEXDIG)
  private static boolean STRCHAR_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "STRCHAR_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "\\\\");
    r = r && STRCHAR_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'b'|'t'|'n'|'f'|'r'|'"'|"'"|'\\' | 'u' HEXDIG HEXDIG HEXDIG HEXDIG
  private static boolean STRCHAR_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "STRCHAR_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "b");
    if (!r) r = consumeToken(b, "t");
    if (!r) r = consumeToken(b, "n");
    if (!r) r = consumeToken(b, "f");
    if (!r) r = consumeToken(b, "r");
    if (!r) r = consumeToken(b, "\"");
    if (!r) r = consumeToken(b, "'");
    if (!r) r = consumeToken(b, "\\\\");
    if (!r) r = STRCHAR_1_1_8(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'u' HEXDIG HEXDIG HEXDIG HEXDIG
  private static boolean STRCHAR_1_1_8(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "STRCHAR_1_1_8")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "u");
    r = r && consumeTokens(b, 0, HEXDIG, HEXDIG, HEXDIG, HEXDIG);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_QualifiedName
  public static boolean X_AbsoluteImportModule(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AbsoluteImportModule")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_QualifiedName(b, l + 1);
    exit_section_(b, m, X_ABSOLUTE_IMPORT_MODULE, r);
    return r;
  }

  /* ********************************************************** */
  // (X_Modifier)* X_AnyDef
  public static boolean X_AnnotatedDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AnnotatedDef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_ANNOTATED_DEF, "<x annotated def>");
    r = X_AnnotatedDef_0(b, l + 1);
    r = r && X_AnyDef(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_Modifier)*
  private static boolean X_AnnotatedDef_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AnnotatedDef_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_AnnotatedDef_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_AnnotatedDef_0", c)) break;
    }
    return true;
  }

  // (X_Modifier)
  private static boolean X_AnnotatedDef_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AnnotatedDef_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_Modifier(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '@' X_Name (X_AnnotationArgs)?
  public static boolean X_Annotation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_Annotation")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_ANNOTATION, "<x annotation>");
    r = consumeToken(b, "@");
    r = r && X_Name(b, l + 1);
    r = r && X_Annotation_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_AnnotationArgs)?
  private static boolean X_Annotation_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_Annotation_2")) return false;
    X_Annotation_2_0(b, l + 1);
    return true;
  }

  // (X_AnnotationArgs)
  private static boolean X_Annotation_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_Annotation_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_AnnotationArgs(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_AnnotationArgValue
  //    | X_AnnotationArgName
  public static boolean X_AnnotationArg(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AnnotationArg")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_ANNOTATION_ARG, "<x annotation arg>");
    r = X_AnnotationArgValue(b, l + 1);
    if (!r) r = X_AnnotationArgName(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_QualifiedName
  public static boolean X_AnnotationArgName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AnnotationArgName")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_QualifiedName(b, l + 1);
    exit_section_(b, m, X_ANNOTATION_ARG_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // X_LiteralExpr
  public static boolean X_AnnotationArgValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AnnotationArgValue")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_ANNOTATION_ARG_VALUE, "<x annotation arg value>");
    r = X_LiteralExpr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '(' (X_AnnotationArg (',' X_AnnotationArg)*)? ')'
  public static boolean X_AnnotationArgs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AnnotationArgs")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_ANNOTATION_ARGS, "<x annotation args>");
    r = consumeToken(b, "(");
    r = r && X_AnnotationArgs_1(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_AnnotationArg (',' X_AnnotationArg)*)?
  private static boolean X_AnnotationArgs_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AnnotationArgs_1")) return false;
    X_AnnotationArgs_1_0(b, l + 1);
    return true;
  }

  // X_AnnotationArg (',' X_AnnotationArg)*
  private static boolean X_AnnotationArgs_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AnnotationArgs_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_AnnotationArg(b, l + 1);
    r = r && X_AnnotationArgs_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' X_AnnotationArg)*
  private static boolean X_AnnotationArgs_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AnnotationArgs_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_AnnotationArgs_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_AnnotationArgs_1_0_1", c)) break;
    }
    return true;
  }

  // ',' X_AnnotationArg
  private static boolean X_AnnotationArgs_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AnnotationArgs_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && X_AnnotationArg(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_QualifiedName (X_tkQUESTION)?
  public static boolean X_AnonAttrHeader(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AnonAttrHeader")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_QualifiedName(b, l + 1);
    r = r && X_AnonAttrHeader_1(b, l + 1);
    exit_section_(b, m, X_ANON_ATTR_HEADER, r);
    return r;
  }

  // (X_tkQUESTION)?
  private static boolean X_AnonAttrHeader_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AnonAttrHeader_1")) return false;
    X_AnonAttrHeader_1_0(b, l + 1);
    return true;
  }

  // (X_tkQUESTION)
  private static boolean X_AnonAttrHeader_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AnonAttrHeader_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_tkQUESTION(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_EntityDef
  //    | X_ObjectDef
  //    | X_StructDef
  //    | X_EnumDef
  //    | X_FunctionDef
  //    | X_NamespaceDef
  //    | X_ImportDef
  //    | X_OpDef
  //    | X_QueryDef
  //    | X_IncludeDef
  //    | X_ConstantDef
  public static boolean X_AnyDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AnyDef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_ANY_DEF, "<x any def>");
    r = X_EntityDef(b, l + 1);
    if (!r) r = X_ObjectDef(b, l + 1);
    if (!r) r = X_StructDef(b, l + 1);
    if (!r) r = X_EnumDef(b, l + 1);
    if (!r) r = X_FunctionDef(b, l + 1);
    if (!r) r = X_NamespaceDef(b, l + 1);
    if (!r) r = X_ImportDef(b, l + 1);
    if (!r) r = X_OpDef(b, l + 1);
    if (!r) r = X_QueryDef(b, l + 1);
    if (!r) r = X_IncludeDef(b, l + 1);
    if (!r) r = X_ConstantDef(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '='
  //    | '+='
  //    | '-='
  //    | '*='
  //    | '/='
  //    | '%='
  public static boolean X_AssignOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AssignOp")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_ASSIGN_OP, "<x assign op>");
    r = consumeToken(b, "=");
    if (!r) r = consumeToken(b, "+=");
    if (!r) r = consumeToken(b, "-=");
    if (!r) r = consumeToken(b, "*=");
    if (!r) r = consumeToken(b, "/=");
    if (!r) r = consumeToken(b, "%=");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_BaseExpr X_AssignOp X_Expression ';'
  public static boolean X_AssignStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AssignStmt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_ASSIGN_STMT, "<x assign stmt>");
    r = X_BaseExpr(b, l + 1);
    r = r && X_AssignOp(b, l + 1);
    r = r && X_Expression(b, l + 1);
    r = r && consumeToken(b, ";");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_tkAT X_tkQUESTION
  //    | X_tkAT X_tkMUL
  //    | X_tkAT X_tkPLUS
  //    | '@'
  public static boolean X_AtExprAt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprAt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_AT_EXPR_AT, "<x at expr at>");
    r = X_AtExprAt_0(b, l + 1);
    if (!r) r = X_AtExprAt_1(b, l + 1);
    if (!r) r = X_AtExprAt_2(b, l + 1);
    if (!r) r = consumeToken(b, "@");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // X_tkAT X_tkQUESTION
  private static boolean X_AtExprAt_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprAt_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_tkAT(b, l + 1);
    r = r && X_tkQUESTION(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // X_tkAT X_tkMUL
  private static boolean X_AtExprAt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprAt_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_tkAT(b, l + 1);
    r = r && X_tkMUL(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // X_tkAT X_tkPLUS
  private static boolean X_AtExprAt_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprAt_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_tkAT(b, l + 1);
    r = r && X_tkPLUS(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_AtExprFromSingle
  //    | X_AtExprFromMulti
  public static boolean X_AtExprFrom(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprFrom")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_AT_EXPR_FROM, "<x at expr from>");
    r = X_AtExprFromSingle(b, l + 1);
    if (!r) r = X_AtExprFromMulti(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (X_Name ':')? X_QualifiedName
  public static boolean X_AtExprFromItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprFromItem")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_AtExprFromItem_0(b, l + 1);
    r = r && X_QualifiedName(b, l + 1);
    exit_section_(b, m, X_AT_EXPR_FROM_ITEM, r);
    return r;
  }

  // (X_Name ':')?
  private static boolean X_AtExprFromItem_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprFromItem_0")) return false;
    X_AtExprFromItem_0_0(b, l + 1);
    return true;
  }

  // X_Name ':'
  private static boolean X_AtExprFromItem_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprFromItem_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_Name(b, l + 1);
    r = r && consumeToken(b, ":");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkLPAR X_AtExprFromItem (',' X_AtExprFromItem)* ')'
  public static boolean X_AtExprFromMulti(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprFromMulti")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_AT_EXPR_FROM_MULTI, "<x at expr from multi>");
    r = X_tkLPAR(b, l + 1);
    r = r && X_AtExprFromItem(b, l + 1);
    r = r && X_AtExprFromMulti_2(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (',' X_AtExprFromItem)*
  private static boolean X_AtExprFromMulti_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprFromMulti_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_AtExprFromMulti_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_AtExprFromMulti_2", c)) break;
    }
    return true;
  }

  // ',' X_AtExprFromItem
  private static boolean X_AtExprFromMulti_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprFromMulti_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && X_AtExprFromItem(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_QualifiedName
  public static boolean X_AtExprFromSingle(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprFromSingle")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_QualifiedName(b, l + 1);
    exit_section_(b, m, X_AT_EXPR_FROM_SINGLE, r);
    return r;
  }

  /* ********************************************************** */
  // 'limit' X_ExpressionRef
  public static boolean X_AtExprLimit(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprLimit")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_AT_EXPR_LIMIT, "<x at expr limit>");
    r = consumeToken(b, "limit");
    r = r && X_ExpressionRef(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'limit' X_ExpressionRef (X_AtExprOffset)?
  //    | 'offset' X_ExpressionRef (X_AtExprLimit)?
  public static boolean X_AtExprModifiers(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprModifiers")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_AT_EXPR_MODIFIERS, "<x at expr modifiers>");
    r = X_AtExprModifiers_0(b, l + 1);
    if (!r) r = X_AtExprModifiers_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'limit' X_ExpressionRef (X_AtExprOffset)?
  private static boolean X_AtExprModifiers_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprModifiers_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "limit");
    r = r && X_ExpressionRef(b, l + 1);
    r = r && X_AtExprModifiers_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_AtExprOffset)?
  private static boolean X_AtExprModifiers_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprModifiers_0_2")) return false;
    X_AtExprModifiers_0_2_0(b, l + 1);
    return true;
  }

  // (X_AtExprOffset)
  private static boolean X_AtExprModifiers_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprModifiers_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_AtExprOffset(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'offset' X_ExpressionRef (X_AtExprLimit)?
  private static boolean X_AtExprModifiers_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprModifiers_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "offset");
    r = r && X_ExpressionRef(b, l + 1);
    r = r && X_AtExprModifiers_1_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_AtExprLimit)?
  private static boolean X_AtExprModifiers_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprModifiers_1_2")) return false;
    X_AtExprModifiers_1_2_0(b, l + 1);
    return true;
  }

  // (X_AtExprLimit)
  private static boolean X_AtExprModifiers_1_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprModifiers_1_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_AtExprLimit(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'offset' X_ExpressionRef
  public static boolean X_AtExprOffset(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprOffset")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_AT_EXPR_OFFSET, "<x at expr offset>");
    r = consumeToken(b, "offset");
    r = r && X_ExpressionRef(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_AtExprWhatSimple
  //    | X_AtExprWhatComplex
  public static boolean X_AtExprWhat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprWhat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_AT_EXPR_WHAT, "<x at expr what>");
    r = X_AtExprWhatSimple(b, l + 1);
    if (!r) r = X_AtExprWhatComplex(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '(' X_AtExprWhatComplexItem (',' X_AtExprWhatComplexItem)* ')'
  public static boolean X_AtExprWhatComplex(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprWhatComplex")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_AT_EXPR_WHAT_COMPLEX, "<x at expr what complex>");
    r = consumeToken(b, "(");
    r = r && X_AtExprWhatComplexItem(b, l + 1);
    r = r && X_AtExprWhatComplex_2(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (',' X_AtExprWhatComplexItem)*
  private static boolean X_AtExprWhatComplex_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprWhatComplex_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_AtExprWhatComplex_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_AtExprWhatComplex_2", c)) break;
    }
    return true;
  }

  // ',' X_AtExprWhatComplexItem
  private static boolean X_AtExprWhatComplex_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprWhatComplex_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && X_AtExprWhatComplexItem(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (X_Annotation)* (X_AtExprWhatName)? X_ExpressionRef
  public static boolean X_AtExprWhatComplexItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprWhatComplexItem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_AT_EXPR_WHAT_COMPLEX_ITEM, "<x at expr what complex item>");
    r = X_AtExprWhatComplexItem_0(b, l + 1);
    r = r && X_AtExprWhatComplexItem_1(b, l + 1);
    r = r && X_ExpressionRef(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_Annotation)*
  private static boolean X_AtExprWhatComplexItem_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprWhatComplexItem_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_AtExprWhatComplexItem_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_AtExprWhatComplexItem_0", c)) break;
    }
    return true;
  }

  // (X_Annotation)
  private static boolean X_AtExprWhatComplexItem_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprWhatComplexItem_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_Annotation(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_AtExprWhatName)?
  private static boolean X_AtExprWhatComplexItem_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprWhatComplexItem_1")) return false;
    X_AtExprWhatComplexItem_1_0(b, l + 1);
    return true;
  }

  // (X_AtExprWhatName)
  private static boolean X_AtExprWhatComplexItem_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprWhatComplexItem_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_AtExprWhatName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_Name '='
  public static boolean X_AtExprWhatName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprWhatName")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_Name(b, l + 1);
    r = r && consumeToken(b, "=");
    exit_section_(b, m, X_AT_EXPR_WHAT_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // ('.' X_Name)+
  public static boolean X_AtExprWhatSimple(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprWhatSimple")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_AT_EXPR_WHAT_SIMPLE, "<x at expr what simple>");
    r = X_AtExprWhatSimple_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!X_AtExprWhatSimple_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_AtExprWhatSimple", c)) break;
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '.' X_Name
  private static boolean X_AtExprWhatSimple_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprWhatSimple_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ".");
    r = r && X_Name(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '{' (X_ExpressionRef (',' X_ExpressionRef)*)? '}'
  public static boolean X_AtExprWhere(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprWhere")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_AT_EXPR_WHERE, "<x at expr where>");
    r = consumeToken(b, "{");
    r = r && X_AtExprWhere_1(b, l + 1);
    r = r && consumeToken(b, "}");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_ExpressionRef (',' X_ExpressionRef)*)?
  private static boolean X_AtExprWhere_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprWhere_1")) return false;
    X_AtExprWhere_1_0(b, l + 1);
    return true;
  }

  // X_ExpressionRef (',' X_ExpressionRef)*
  private static boolean X_AtExprWhere_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprWhere_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_ExpressionRef(b, l + 1);
    r = r && X_AtExprWhere_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' X_ExpressionRef)*
  private static boolean X_AtExprWhere_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprWhere_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_AtExprWhere_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_AtExprWhere_1_0_1", c)) break;
    }
    return true;
  }

  // ',' X_ExpressionRef
  private static boolean X_AtExprWhere_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprWhere_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && X_ExpressionRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkDOT X_Name
  public static boolean X_AttrExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AttrExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_ATTR_EXPR, "<x attr expr>");
    r = X_tkDOT(b, l + 1);
    r = r && X_Name(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_NameTypeAttrHeader
  //    | X_AnonAttrHeader
  public static boolean X_AttrHeader(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AttrHeader")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_NameTypeAttrHeader(b, l + 1);
    if (!r) r = X_AnonAttrHeader(b, l + 1);
    exit_section_(b, m, X_ATTR_HEADER, r);
    return r;
  }

  /* ********************************************************** */
  // X_BaseAttributeDefinition ';'
  public static boolean X_AttributeDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AttributeDefinition")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_ATTRIBUTE_DEFINITION, "<x attribute definition>");
    r = X_BaseAttributeDefinition(b, l + 1);
    r = r && consumeToken(b, ";");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (X_tkMUTABLE)? X_AttrHeader ('=' X_ExpressionRef)?
  public static boolean X_BaseAttributeDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseAttributeDefinition")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_BASE_ATTRIBUTE_DEFINITION, "<x base attribute definition>");
    r = X_BaseAttributeDefinition_0(b, l + 1);
    r = r && X_AttrHeader(b, l + 1);
    r = r && X_BaseAttributeDefinition_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkMUTABLE)?
  private static boolean X_BaseAttributeDefinition_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseAttributeDefinition_0")) return false;
    X_BaseAttributeDefinition_0_0(b, l + 1);
    return true;
  }

  // (X_tkMUTABLE)
  private static boolean X_BaseAttributeDefinition_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseAttributeDefinition_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_tkMUTABLE(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('=' X_ExpressionRef)?
  private static boolean X_BaseAttributeDefinition_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseAttributeDefinition_2")) return false;
    X_BaseAttributeDefinition_2_0(b, l + 1);
    return true;
  }

  // '=' X_ExpressionRef
  private static boolean X_BaseAttributeDefinition_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseAttributeDefinition_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "=");
    r = r && X_ExpressionRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_BaseExprHead (X_BaseExprTail)*
  public static boolean X_BaseExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_BASE_EXPR, "<x base expr>");
    r = X_BaseExprHead(b, l + 1);
    r = r && X_BaseExpr_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_BaseExprTail)*
  private static boolean X_BaseExpr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseExpr_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_BaseExpr_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_BaseExpr_1", c)) break;
    }
    return true;
  }

  // (X_BaseExprTail)
  private static boolean X_BaseExpr_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseExpr_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_BaseExprTail(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_GenericTypeExpr
  //    | X_NameExpr
  //    | X_DollarExpr
  //    | X_AttrExpr
  //    | X_BigIntExpr
  //    | X_IntExpr
  //    | X_DecimalExpr
  //    | X_StringExpr
  //    | X_BytesExpr
  //    | 'false'
  //    | 'true'
  //    | X_NullLiteralExpr
  //    | X_ParenthesesExpr
  //    | X_CreateExpr
  //    | X_ListLiteralExpr
  //    | X_EmptyMapLiteralExpr
  //    | X_NonEmptyMapLiteralExpr
  //    | X_MirrorStructExpr
  //    | X_VirtualTypeExpr
  public static boolean X_BaseExprHead(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseExprHead")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_BASE_EXPR_HEAD, "<x base expr head>");
    r = X_GenericTypeExpr(b, l + 1);
    if (!r) r = X_NameExpr(b, l + 1);
    if (!r) r = X_DollarExpr(b, l + 1);
    if (!r) r = X_AttrExpr(b, l + 1);
    if (!r) r = X_BigIntExpr(b, l + 1);
    if (!r) r = X_IntExpr(b, l + 1);
    if (!r) r = X_DecimalExpr(b, l + 1);
    if (!r) r = X_StringExpr(b, l + 1);
    if (!r) r = X_BytesExpr(b, l + 1);
    if (!r) r = consumeToken(b, "false");
    if (!r) r = consumeToken(b, "true");
    if (!r) r = X_NullLiteralExpr(b, l + 1);
    if (!r) r = X_ParenthesesExpr(b, l + 1);
    if (!r) r = X_CreateExpr(b, l + 1);
    if (!r) r = X_ListLiteralExpr(b, l + 1);
    if (!r) r = X_EmptyMapLiteralExpr(b, l + 1);
    if (!r) r = X_NonEmptyMapLiteralExpr(b, l + 1);
    if (!r) r = X_MirrorStructExpr(b, l + 1);
    if (!r) r = X_VirtualTypeExpr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_BaseExprHead (X_BaseExprTailNoCallNoAt)*
  public static boolean X_BaseExprNoCallNoAt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseExprNoCallNoAt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_BASE_EXPR_NO_CALL_NO_AT, "<x base expr no call no at>");
    r = X_BaseExprHead(b, l + 1);
    r = r && X_BaseExprNoCallNoAt_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_BaseExprTailNoCallNoAt)*
  private static boolean X_BaseExprNoCallNoAt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseExprNoCallNoAt_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_BaseExprNoCallNoAt_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_BaseExprNoCallNoAt_1", c)) break;
    }
    return true;
  }

  // (X_BaseExprTailNoCallNoAt)
  private static boolean X_BaseExprNoCallNoAt_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseExprNoCallNoAt_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_BaseExprTailNoCallNoAt(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_BaseExprTailMember
  //    | X_BaseExprTailSubscript
  //    | X_BaseExprTailNotNull
  //    | X_BaseExprTailSafeMember
  //    | X_BaseExprTailUnaryPostfixOp
  //    | X_BaseExprTailCall
  //    | X_BaseExprTailAt
  public static boolean X_BaseExprTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseExprTail")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_BASE_EXPR_TAIL, "<x base expr tail>");
    r = X_BaseExprTailMember(b, l + 1);
    if (!r) r = X_BaseExprTailSubscript(b, l + 1);
    if (!r) r = X_BaseExprTailNotNull(b, l + 1);
    if (!r) r = X_BaseExprTailSafeMember(b, l + 1);
    if (!r) r = X_BaseExprTailUnaryPostfixOp(b, l + 1);
    if (!r) r = X_BaseExprTailCall(b, l + 1);
    if (!r) r = X_BaseExprTailAt(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_AtExprAt X_AtExprWhere (X_AtExprWhat)? (X_AtExprModifiers)?
  public static boolean X_BaseExprTailAt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseExprTailAt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_BASE_EXPR_TAIL_AT, "<x base expr tail at>");
    r = X_AtExprAt(b, l + 1);
    r = r && X_AtExprWhere(b, l + 1);
    r = r && X_BaseExprTailAt_2(b, l + 1);
    r = r && X_BaseExprTailAt_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_AtExprWhat)?
  private static boolean X_BaseExprTailAt_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseExprTailAt_2")) return false;
    X_BaseExprTailAt_2_0(b, l + 1);
    return true;
  }

  // (X_AtExprWhat)
  private static boolean X_BaseExprTailAt_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseExprTailAt_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_AtExprWhat(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_AtExprModifiers)?
  private static boolean X_BaseExprTailAt_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseExprTailAt_3")) return false;
    X_BaseExprTailAt_3_0(b, l + 1);
    return true;
  }

  // (X_AtExprModifiers)
  private static boolean X_BaseExprTailAt_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseExprTailAt_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_AtExprModifiers(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_CallArgs
  public static boolean X_BaseExprTailCall(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseExprTailCall")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_BASE_EXPR_TAIL_CALL, "<x base expr tail call>");
    r = X_CallArgs(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '.' X_Name
  public static boolean X_BaseExprTailMember(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseExprTailMember")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_BASE_EXPR_TAIL_MEMBER, "<x base expr tail member>");
    r = consumeToken(b, ".");
    r = r && X_Name(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_BaseExprTailMember
  //    | X_BaseExprTailSubscript
  //    | X_BaseExprTailNotNull
  //    | X_BaseExprTailSafeMember
  //    | X_BaseExprTailUnaryPostfixOp
  public static boolean X_BaseExprTailNoCallNoAt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseExprTailNoCallNoAt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_BASE_EXPR_TAIL_NO_CALL_NO_AT, "<x base expr tail no call no at>");
    r = X_BaseExprTailMember(b, l + 1);
    if (!r) r = X_BaseExprTailSubscript(b, l + 1);
    if (!r) r = X_BaseExprTailNotNull(b, l + 1);
    if (!r) r = X_BaseExprTailSafeMember(b, l + 1);
    if (!r) r = X_BaseExprTailUnaryPostfixOp(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '!!'
  public static boolean X_BaseExprTailNotNull(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseExprTailNotNull")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_BASE_EXPR_TAIL_NOT_NULL, "<x base expr tail not null>");
    r = consumeToken(b, "!!");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '?.' X_Name
  public static boolean X_BaseExprTailSafeMember(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseExprTailSafeMember")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_BASE_EXPR_TAIL_SAFE_MEMBER, "<x base expr tail safe member>");
    r = consumeToken(b, "?.");
    r = r && X_Name(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_tkLBRACK X_ExpressionRef ']'
  public static boolean X_BaseExprTailSubscript(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseExprTailSubscript")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_BASE_EXPR_TAIL_SUBSCRIPT, "<x base expr tail subscript>");
    r = X_tkLBRACK(b, l + 1);
    r = r && X_ExpressionRef(b, l + 1);
    r = r && consumeToken(b, "]");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_UnaryPostfixOperator
  public static boolean X_BaseExprTailUnaryPostfixOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseExprTailUnaryPostfixOp")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_BASE_EXPR_TAIL_UNARY_POSTFIX_OP, "<x base expr tail unary postfix op>");
    r = X_UnaryPostfixOperator(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_PrimaryType (X_tkQUESTION)*
  public static boolean X_BasicType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BasicType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_BASIC_TYPE, "<x basic type>");
    r = X_PrimaryType(b, l + 1);
    r = r && X_BasicType_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkQUESTION)*
  private static boolean X_BasicType_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BasicType_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_BasicType_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_BasicType_1", c)) break;
    }
    return true;
  }

  // (X_tkQUESTION)
  private static boolean X_BasicType_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BasicType_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_tkQUESTION(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // BIG_INTEGER
  public static boolean X_BigIntExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BigIntExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_BIG_INT_EXPR, "<x big int expr>");
    r = BIG_INTEGER(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_BinaryOperator X_UnaryExpr
  public static boolean X_BinaryExprOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BinaryExprOperand")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_BINARY_EXPR_OPERAND, "<x binary expr operand>");
    r = X_BinaryOperator(b, l + 1);
    r = r && X_UnaryExpr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '=='
  //    | '!='
  //    | '<='
  //    | '>='
  //    | '<'
  //    | '>'
  //    | '==='
  //    | '!=='
  //    | '+'
  //    | '-'
  //    | '*'
  //    | '/'
  //    | '%'
  //    | 'and'
  //    | 'or'
  //    | 'in'
  //    | 'not' X_tkIN
  //    | '?:'
  public static boolean X_BinaryOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BinaryOperator")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_BINARY_OPERATOR, "<x binary operator>");
    r = consumeToken(b, "==");
    if (!r) r = consumeToken(b, "!=");
    if (!r) r = consumeToken(b, "<=");
    if (!r) r = consumeToken(b, ">=");
    if (!r) r = consumeToken(b, "<");
    if (!r) r = consumeToken(b, ">");
    if (!r) r = consumeToken(b, "===");
    if (!r) r = consumeToken(b, "!==");
    if (!r) r = consumeToken(b, "+");
    if (!r) r = consumeToken(b, "-");
    if (!r) r = consumeToken(b, "*");
    if (!r) r = consumeToken(b, "/");
    if (!r) r = consumeToken(b, "%");
    if (!r) r = consumeToken(b, "and");
    if (!r) r = consumeToken(b, "or");
    if (!r) r = consumeToken(b, "in");
    if (!r) r = X_BinaryOperator_16(b, l + 1);
    if (!r) r = consumeToken(b, "?:");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'not' X_tkIN
  private static boolean X_BinaryOperator_16(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BinaryOperator_16")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "not");
    r = r && X_tkIN(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkLCURL (X_StatementRef)* '}'
  public static boolean X_BlockStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BlockStmt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_BLOCK_STMT, "<x block stmt>");
    r = X_tkLCURL(b, l + 1);
    r = r && X_BlockStmt_1(b, l + 1);
    r = r && consumeToken(b, "}");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_StatementRef)*
  private static boolean X_BlockStmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BlockStmt_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_BlockStmt_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_BlockStmt_1", c)) break;
    }
    return true;
  }

  // (X_StatementRef)
  private static boolean X_BlockStmt_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BlockStmt_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_StatementRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkBREAK ';'
  public static boolean X_BreakStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BreakStmt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_BREAK_STMT, "<x break stmt>");
    r = X_tkBREAK(b, l + 1);
    r = r && consumeToken(b, ";");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // BYTES
  public static boolean X_BytesExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BytesExpr")) return false;
    if (!nextTokenIs(b, BYTES)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, BYTES);
    exit_section_(b, m, X_BYTES_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // (X_Name '=')? X_CallArgValue
  public static boolean X_CallArg(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CallArg")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_CALL_ARG, "<x call arg>");
    r = X_CallArg_0(b, l + 1);
    r = r && X_CallArgValue(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_Name '=')?
  private static boolean X_CallArg_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CallArg_0")) return false;
    X_CallArg_0_0(b, l + 1);
    return true;
  }

  // X_Name '='
  private static boolean X_CallArg_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CallArg_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_Name(b, l + 1);
    r = r && consumeToken(b, "=");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '*'
  //    | X_ExpressionRef
  public static boolean X_CallArgValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CallArgValue")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_CALL_ARG_VALUE, "<x call arg value>");
    r = consumeToken(b, "*");
    if (!r) r = X_ExpressionRef(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '(' (X_CallArg (',' X_CallArg)*)? ')'
  public static boolean X_CallArgs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CallArgs")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_CALL_ARGS, "<x call args>");
    r = consumeToken(b, "(");
    r = r && X_CallArgs_1(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_CallArg (',' X_CallArg)*)?
  private static boolean X_CallArgs_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CallArgs_1")) return false;
    X_CallArgs_1_0(b, l + 1);
    return true;
  }

  // X_CallArg (',' X_CallArg)*
  private static boolean X_CallArgs_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CallArgs_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CallArg(b, l + 1);
    r = r && X_CallArgs_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' X_CallArg)*
  private static boolean X_CallArgs_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CallArgs_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_CallArgs_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_CallArgs_1_0_1", c)) break;
    }
    return true;
  }

  // ',' X_CallArg
  private static boolean X_CallArgs_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CallArgs_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && X_CallArg(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_BaseExpr ';'
  public static boolean X_CallStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CallStmt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_CALL_STMT, "<x call stmt>");
    r = X_BaseExpr(b, l + 1);
    r = r && consumeToken(b, ";");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_tkLPAR X_TypeRef ')' '?'
  public static boolean X_ComplexNullableType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ComplexNullableType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_COMPLEX_NULLABLE_TYPE, "<x complex nullable type>");
    r = X_tkLPAR(b, l + 1);
    r = r && X_TypeRef(b, l + 1);
    r = r && consumeToken(b, ")");
    r = r && consumeToken(b, "?");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_tkVAL X_Name (':' X_TypeRef)? '=' X_ExpressionRef ';'
  public static boolean X_ConstantDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ConstantDef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_CONSTANT_DEF, "<x constant def>");
    r = X_tkVAL(b, l + 1);
    r = r && X_Name(b, l + 1);
    r = r && X_ConstantDef_2(b, l + 1);
    r = r && consumeToken(b, "=");
    r = r && X_ExpressionRef(b, l + 1);
    r = r && consumeToken(b, ";");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (':' X_TypeRef)?
  private static boolean X_ConstantDef_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ConstantDef_2")) return false;
    X_ConstantDef_2_0(b, l + 1);
    return true;
  }

  // ':' X_TypeRef
  private static boolean X_ConstantDef_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ConstantDef_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ":");
    r = r && X_TypeRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkCONTINUE ';'
  public static boolean X_ContinueStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ContinueStmt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_CONTINUE_STMT, "<x continue stmt>");
    r = X_tkCONTINUE(b, l + 1);
    r = r && consumeToken(b, ";");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_tkCREATE X_QualifiedName '(' (X_CreateExprArg (',' X_CreateExprArg)*)? ')'
  public static boolean X_CreateExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CreateExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_CREATE_EXPR, "<x create expr>");
    r = X_tkCREATE(b, l + 1);
    r = r && X_QualifiedName(b, l + 1);
    r = r && consumeToken(b, "(");
    r = r && X_CreateExpr_3(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_CreateExprArg (',' X_CreateExprArg)*)?
  private static boolean X_CreateExpr_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CreateExpr_3")) return false;
    X_CreateExpr_3_0(b, l + 1);
    return true;
  }

  // X_CreateExprArg (',' X_CreateExprArg)*
  private static boolean X_CreateExpr_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CreateExpr_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CreateExprArg(b, l + 1);
    r = r && X_CreateExpr_3_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' X_CreateExprArg)*
  private static boolean X_CreateExpr_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CreateExpr_3_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_CreateExpr_3_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_CreateExpr_3_0_1", c)) break;
    }
    return true;
  }

  // ',' X_CreateExprArg
  private static boolean X_CreateExpr_3_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CreateExpr_3_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && X_CreateExprArg(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (('.')? X_Name '=')? X_CallArgValue
  public static boolean X_CreateExprArg(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CreateExprArg")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_CREATE_EXPR_ARG, "<x create expr arg>");
    r = X_CreateExprArg_0(b, l + 1);
    r = r && X_CallArgValue(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (('.')? X_Name '=')?
  private static boolean X_CreateExprArg_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CreateExprArg_0")) return false;
    X_CreateExprArg_0_0(b, l + 1);
    return true;
  }

  // ('.')? X_Name '='
  private static boolean X_CreateExprArg_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CreateExprArg_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CreateExprArg_0_0_0(b, l + 1);
    r = r && X_Name(b, l + 1);
    r = r && consumeToken(b, "=");
    exit_section_(b, m, null, r);
    return r;
  }

  // ('.')?
  private static boolean X_CreateExprArg_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CreateExprArg_0_0_0")) return false;
    X_CreateExprArg_0_0_0_0(b, l + 1);
    return true;
  }

  // ('.')
  private static boolean X_CreateExprArg_0_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CreateExprArg_0_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ".");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_CreateExpr ';'
  public static boolean X_CreateStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CreateStmt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_CREATE_STMT, "<x create stmt>");
    r = X_CreateExpr(b, l + 1);
    r = r && consumeToken(b, ";");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // DECIMAL
  public static boolean X_DecimalExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_DecimalExpr")) return false;
    if (!nextTokenIs(b, DECIMAL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DECIMAL);
    exit_section_(b, m, X_DECIMAL_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkDELETE X_UpdateTarget ';'
  public static boolean X_DeleteStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_DeleteStmt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_DELETE_STMT, "<x delete stmt>");
    r = X_tkDELETE(b, l + 1);
    r = r && X_UpdateTarget(b, l + 1);
    r = r && consumeToken(b, ";");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '$'
  public static boolean X_DollarExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_DollarExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_DOLLAR_EXPR, "<x dollar expr>");
    r = consumeToken(b, "$");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_tkLBRACK ':' ']'
  public static boolean X_EmptyMapLiteralExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EmptyMapLiteralExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_EMPTY_MAP_LITERAL_EXPR, "<x empty map literal expr>");
    r = X_tkLBRACK(b, l + 1);
    r = r && consumeToken(b, ":");
    r = r && consumeToken(b, "]");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ';'
  public static boolean X_EmptyStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EmptyStmt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_EMPTY_STMT, "<x empty stmt>");
    r = consumeToken(b, ";");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '(' X_Name (',' X_Name)* ')'
  public static boolean X_EntityAnnotations(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EntityAnnotations")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_ENTITY_ANNOTATIONS, "<x entity annotations>");
    r = consumeToken(b, "(");
    r = r && X_Name(b, l + 1);
    r = r && X_EntityAnnotations_2(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (',' X_Name)*
  private static boolean X_EntityAnnotations_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EntityAnnotations_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_EntityAnnotations_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_EntityAnnotations_2", c)) break;
    }
    return true;
  }

  // ',' X_Name
  private static boolean X_EntityAnnotations_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EntityAnnotations_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && X_Name(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_EntityBodyFull
  //    | X_EntityBodyShort
  public static boolean X_EntityBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EntityBody")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_ENTITY_BODY, "<x entity body>");
    r = X_EntityBodyFull(b, l + 1);
    if (!r) r = X_EntityBodyShort(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '{' (X_RelAnyClause)* '}'
  public static boolean X_EntityBodyFull(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EntityBodyFull")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_ENTITY_BODY_FULL, "<x entity body full>");
    r = consumeToken(b, "{");
    r = r && X_EntityBodyFull_1(b, l + 1);
    r = r && consumeToken(b, "}");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_RelAnyClause)*
  private static boolean X_EntityBodyFull_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EntityBodyFull_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_EntityBodyFull_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_EntityBodyFull_1", c)) break;
    }
    return true;
  }

  // (X_RelAnyClause)
  private static boolean X_EntityBodyFull_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EntityBodyFull_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_RelAnyClause(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ';'
  public static boolean X_EntityBodyShort(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EntityBodyShort")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_ENTITY_BODY_SHORT, "<x entity body short>");
    r = consumeToken(b, ";");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_EntityKeyword X_Name (X_EntityAnnotations)? (X_EntityBody)?
  public static boolean X_EntityDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EntityDef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_ENTITY_DEF, "<x entity def>");
    r = X_EntityKeyword(b, l + 1);
    r = r && X_Name(b, l + 1);
    r = r && X_EntityDef_2(b, l + 1);
    r = r && X_EntityDef_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_EntityAnnotations)?
  private static boolean X_EntityDef_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EntityDef_2")) return false;
    X_EntityDef_2_0(b, l + 1);
    return true;
  }

  // (X_EntityAnnotations)
  private static boolean X_EntityDef_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EntityDef_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_EntityAnnotations(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_EntityBody)?
  private static boolean X_EntityDef_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EntityDef_3")) return false;
    X_EntityDef_3_0(b, l + 1);
    return true;
  }

  // (X_EntityBody)
  private static boolean X_EntityDef_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EntityDef_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_EntityBody(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'entity'
  //    | 'class'
  public static boolean X_EntityKeyword(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EntityKeyword")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_ENTITY_KEYWORD, "<x entity keyword>");
    r = consumeToken(b, "entity");
    if (!r) r = consumeToken(b, "class");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_tkENUM X_Name '{' (X_Name (',' X_Name)*)? (X_tkCOMMA)? '}'
  public static boolean X_EnumDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EnumDef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_ENUM_DEF, "<x enum def>");
    r = X_tkENUM(b, l + 1);
    r = r && X_Name(b, l + 1);
    r = r && consumeToken(b, "{");
    r = r && X_EnumDef_3(b, l + 1);
    r = r && X_EnumDef_4(b, l + 1);
    r = r && consumeToken(b, "}");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_Name (',' X_Name)*)?
  private static boolean X_EnumDef_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EnumDef_3")) return false;
    X_EnumDef_3_0(b, l + 1);
    return true;
  }

  // X_Name (',' X_Name)*
  private static boolean X_EnumDef_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EnumDef_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_Name(b, l + 1);
    r = r && X_EnumDef_3_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' X_Name)*
  private static boolean X_EnumDef_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EnumDef_3_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_EnumDef_3_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_EnumDef_3_0_1", c)) break;
    }
    return true;
  }

  // ',' X_Name
  private static boolean X_EnumDef_3_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EnumDef_3_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && X_Name(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkCOMMA)?
  private static boolean X_EnumDef_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EnumDef_4")) return false;
    X_EnumDef_4_0(b, l + 1);
    return true;
  }

  // (X_tkCOMMA)
  private static boolean X_EnumDef_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EnumDef_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_tkCOMMA(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_UnaryExpr (X_BinaryExprOperand)*
  public static boolean X_Expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_Expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_EXPRESSION, "<x expression>");
    r = X_UnaryExpr(b, l + 1);
    r = r && X_Expression_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_BinaryExprOperand)*
  private static boolean X_Expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_Expression_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_Expression_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_Expression_1", c)) break;
    }
    return true;
  }

  // (X_BinaryExprOperand)
  private static boolean X_Expression_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_Expression_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_BinaryExprOperand(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_Expression
  public static boolean X_ExpressionRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ExpressionRef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_EXPRESSION_REF, "<x expression ref>");
    r = X_Expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_tkFOR '(' X_VarDeclarator 'in' X_Expression ')' X_StatementRef
  public static boolean X_ForStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ForStmt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_FOR_STMT, "<x for stmt>");
    r = X_tkFOR(b, l + 1);
    r = r && consumeToken(b, "(");
    r = r && X_VarDeclarator(b, l + 1);
    r = r && consumeToken(b, "in");
    r = r && X_Expression(b, l + 1);
    r = r && consumeToken(b, ")");
    r = r && X_StatementRef(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_AttrHeader ('=' X_Expression)?
  public static boolean X_FormalParameter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FormalParameter")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_AttrHeader(b, l + 1);
    r = r && X_FormalParameter_1(b, l + 1);
    exit_section_(b, m, X_FORMAL_PARAMETER, r);
    return r;
  }

  // ('=' X_Expression)?
  private static boolean X_FormalParameter_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FormalParameter_1")) return false;
    X_FormalParameter_1_0(b, l + 1);
    return true;
  }

  // '=' X_Expression
  private static boolean X_FormalParameter_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FormalParameter_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "=");
    r = r && X_Expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_FunctionBodyShort
  //    | X_FunctionBodyFull
  //    | X_FunctionBodyNone
  public static boolean X_FunctionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FunctionBody")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_FUNCTION_BODY, "<x function body>");
    r = X_FunctionBodyShort(b, l + 1);
    if (!r) r = X_FunctionBodyFull(b, l + 1);
    if (!r) r = X_FunctionBodyNone(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_BlockStmt
  public static boolean X_FunctionBodyFull(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FunctionBodyFull")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_FUNCTION_BODY_FULL, "<x function body full>");
    r = X_BlockStmt(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ';'
  public static boolean X_FunctionBodyNone(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FunctionBodyNone")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_FUNCTION_BODY_NONE, "<x function body none>");
    r = consumeToken(b, ";");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '=' X_Expression ';'
  public static boolean X_FunctionBodyShort(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FunctionBodyShort")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_FUNCTION_BODY_SHORT, "<x function body short>");
    r = consumeToken(b, "=");
    r = r && X_Expression(b, l + 1);
    r = r && consumeToken(b, ";");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_tkFUNCTION (X_QualifiedName)? '(' (X_FormalParameter (',' X_FormalParameter)*)? ')' (':' X_Type)? X_FunctionBody
  public static boolean X_FunctionDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FunctionDef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_FUNCTION_DEF, "<x function def>");
    r = X_tkFUNCTION(b, l + 1);
    r = r && X_FunctionDef_1(b, l + 1);
    r = r && consumeToken(b, "(");
    r = r && X_FunctionDef_3(b, l + 1);
    r = r && consumeToken(b, ")");
    r = r && X_FunctionDef_5(b, l + 1);
    r = r && X_FunctionBody(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_QualifiedName)?
  private static boolean X_FunctionDef_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FunctionDef_1")) return false;
    X_FunctionDef_1_0(b, l + 1);
    return true;
  }

  // (X_QualifiedName)
  private static boolean X_FunctionDef_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FunctionDef_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_QualifiedName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_FormalParameter (',' X_FormalParameter)*)?
  private static boolean X_FunctionDef_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FunctionDef_3")) return false;
    X_FunctionDef_3_0(b, l + 1);
    return true;
  }

  // X_FormalParameter (',' X_FormalParameter)*
  private static boolean X_FunctionDef_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FunctionDef_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_FormalParameter(b, l + 1);
    r = r && X_FunctionDef_3_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' X_FormalParameter)*
  private static boolean X_FunctionDef_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FunctionDef_3_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_FunctionDef_3_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_FunctionDef_3_0_1", c)) break;
    }
    return true;
  }

  // ',' X_FormalParameter
  private static boolean X_FunctionDef_3_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FunctionDef_3_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && X_FormalParameter(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (':' X_Type)?
  private static boolean X_FunctionDef_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FunctionDef_5")) return false;
    X_FunctionDef_5_0(b, l + 1);
    return true;
  }

  // ':' X_Type
  private static boolean X_FunctionDef_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FunctionDef_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ":");
    r = r && X_Type(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkLPAR (X_TypeRef (',' X_TypeRef)*)? ')' '->' X_TypeRef
  public static boolean X_FunctionType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FunctionType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_FUNCTION_TYPE, "<x function type>");
    r = X_tkLPAR(b, l + 1);
    r = r && X_FunctionType_1(b, l + 1);
    r = r && consumeToken(b, ")");
    r = r && consumeToken(b, "->");
    r = r && X_TypeRef(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_TypeRef (',' X_TypeRef)*)?
  private static boolean X_FunctionType_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FunctionType_1")) return false;
    X_FunctionType_1_0(b, l + 1);
    return true;
  }

  // X_TypeRef (',' X_TypeRef)*
  private static boolean X_FunctionType_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FunctionType_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_TypeRef(b, l + 1);
    r = r && X_FunctionType_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' X_TypeRef)*
  private static boolean X_FunctionType_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FunctionType_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_FunctionType_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_FunctionType_1_0_1", c)) break;
    }
    return true;
  }

  // ',' X_TypeRef
  private static boolean X_FunctionType_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FunctionType_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && X_TypeRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_QualifiedName '<' X_TypeRef (',' X_TypeRef)* '>'
  public static boolean X_GenericType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_GenericType")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_QualifiedName(b, l + 1);
    r = r && consumeToken(b, "<");
    r = r && X_TypeRef(b, l + 1);
    r = r && X_GenericType_3(b, l + 1);
    r = r && consumeToken(b, ">");
    exit_section_(b, m, X_GENERIC_TYPE, r);
    return r;
  }

  // (',' X_TypeRef)*
  private static boolean X_GenericType_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_GenericType_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_GenericType_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_GenericType_3", c)) break;
    }
    return true;
  }

  // ',' X_TypeRef
  private static boolean X_GenericType_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_GenericType_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && X_TypeRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_GenericType (X_BaseExprTailMember | X_BaseExprTailCall)
  public static boolean X_GenericTypeExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_GenericTypeExpr")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_GenericType(b, l + 1);
    r = r && X_GenericTypeExpr_1(b, l + 1);
    exit_section_(b, m, X_GENERIC_TYPE_EXPR, r);
    return r;
  }

  // X_BaseExprTailMember | X_BaseExprTailCall
  private static boolean X_GenericTypeExpr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_GenericTypeExpr_1")) return false;
    boolean r;
    r = X_BaseExprTailMember(b, l + 1);
    if (!r) r = X_BaseExprTailCall(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // X_tkGUARD X_BlockStmt
  public static boolean X_GuardStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_GuardStmt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_GUARD_STMT, "<x guard stmt>");
    r = X_tkGUARD(b, l + 1);
    r = r && X_BlockStmt(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_tkIF '(' X_ExpressionRef ')' X_ExpressionRef 'else' X_ExpressionRef
  public static boolean X_IfExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_IfExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_IF_EXPR, "<x if expr>");
    r = X_tkIF(b, l + 1);
    r = r && consumeToken(b, "(");
    r = r && X_ExpressionRef(b, l + 1);
    r = r && consumeToken(b, ")");
    r = r && X_ExpressionRef(b, l + 1);
    r = r && consumeToken(b, "else");
    r = r && X_ExpressionRef(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_tkIF '(' X_Expression ')' X_StatementRef (X_tkElse X_StatementRef)?
  public static boolean X_IfStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_IfStmt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_IF_STMT, "<x if stmt>");
    r = X_tkIF(b, l + 1);
    r = r && consumeToken(b, "(");
    r = r && X_Expression(b, l + 1);
    r = r && consumeToken(b, ")");
    r = r && X_StatementRef(b, l + 1);
    r = r && X_IfStmt_5(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkElse X_StatementRef)?
  private static boolean X_IfStmt_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_IfStmt_5")) return false;
    X_IfStmt_5_0(b, l + 1);
    return true;
  }

  // X_tkElse X_StatementRef
  private static boolean X_IfStmt_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_IfStmt_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_tkElse(b, l + 1);
    r = r && X_StatementRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkIMPORT (X_Name ':')? X_ImportModule (X_ImportTarget)? ';'
  public static boolean X_ImportDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportDef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_IMPORT_DEF, "<x import def>");
    r = X_tkIMPORT(b, l + 1);
    r = r && X_ImportDef_1(b, l + 1);
    r = r && X_ImportModule(b, l + 1);
    r = r && X_ImportDef_3(b, l + 1);
    r = r && consumeToken(b, ";");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_Name ':')?
  private static boolean X_ImportDef_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportDef_1")) return false;
    X_ImportDef_1_0(b, l + 1);
    return true;
  }

  // X_Name ':'
  private static boolean X_ImportDef_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportDef_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_Name(b, l + 1);
    r = r && consumeToken(b, ":");
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_ImportTarget)?
  private static boolean X_ImportDef_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportDef_3")) return false;
    X_ImportDef_3_0(b, l + 1);
    return true;
  }

  // (X_ImportTarget)
  private static boolean X_ImportDef_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportDef_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_ImportTarget(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_AbsoluteImportModule
  //    | X_RelativeImportModule
  //    | X_UpImportModule
  public static boolean X_ImportModule(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportModule")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_IMPORT_MODULE, "<x import module>");
    r = X_AbsoluteImportModule(b, l + 1);
    if (!r) r = X_RelativeImportModule(b, l + 1);
    if (!r) r = X_UpImportModule(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '.' (X_ImportTargetExact | X_ImportTargetWildcard)
  public static boolean X_ImportTarget(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportTarget")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_IMPORT_TARGET, "<x import target>");
    r = consumeToken(b, ".");
    r = r && X_ImportTarget_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // X_ImportTargetExact | X_ImportTargetWildcard
  private static boolean X_ImportTarget_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportTarget_1")) return false;
    boolean r;
    r = X_ImportTargetExact(b, l + 1);
    if (!r) r = X_ImportTargetWildcard(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // '{' (X_ImportTargetExactItem (',' X_ImportTargetExactItem)*)? '}'
  public static boolean X_ImportTargetExact(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportTargetExact")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_IMPORT_TARGET_EXACT, "<x import target exact>");
    r = consumeToken(b, "{");
    r = r && X_ImportTargetExact_1(b, l + 1);
    r = r && consumeToken(b, "}");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_ImportTargetExactItem (',' X_ImportTargetExactItem)*)?
  private static boolean X_ImportTargetExact_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportTargetExact_1")) return false;
    X_ImportTargetExact_1_0(b, l + 1);
    return true;
  }

  // X_ImportTargetExactItem (',' X_ImportTargetExactItem)*
  private static boolean X_ImportTargetExact_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportTargetExact_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_ImportTargetExactItem(b, l + 1);
    r = r && X_ImportTargetExact_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' X_ImportTargetExactItem)*
  private static boolean X_ImportTargetExact_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportTargetExact_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_ImportTargetExact_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_ImportTargetExact_1_0_1", c)) break;
    }
    return true;
  }

  // ',' X_ImportTargetExactItem
  private static boolean X_ImportTargetExact_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportTargetExact_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && X_ImportTargetExactItem(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (X_Name ':')? X_QualifiedName ('.' X_tkMUL)?
  public static boolean X_ImportTargetExactItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportTargetExactItem")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_ImportTargetExactItem_0(b, l + 1);
    r = r && X_QualifiedName(b, l + 1);
    r = r && X_ImportTargetExactItem_2(b, l + 1);
    exit_section_(b, m, X_IMPORT_TARGET_EXACT_ITEM, r);
    return r;
  }

  // (X_Name ':')?
  private static boolean X_ImportTargetExactItem_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportTargetExactItem_0")) return false;
    X_ImportTargetExactItem_0_0(b, l + 1);
    return true;
  }

  // X_Name ':'
  private static boolean X_ImportTargetExactItem_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportTargetExactItem_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_Name(b, l + 1);
    r = r && consumeToken(b, ":");
    exit_section_(b, m, null, r);
    return r;
  }

  // ('.' X_tkMUL)?
  private static boolean X_ImportTargetExactItem_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportTargetExactItem_2")) return false;
    X_ImportTargetExactItem_2_0(b, l + 1);
    return true;
  }

  // '.' X_tkMUL
  private static boolean X_ImportTargetExactItem_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportTargetExactItem_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ".");
    r = r && X_tkMUL(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '*'
  public static boolean X_ImportTargetWildcard(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportTargetWildcard")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_IMPORT_TARGET_WILDCARD, "<x import target wildcard>");
    r = consumeToken(b, "*");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_tkINCLUDE X_tkSTRING ';'
  public static boolean X_IncludeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_IncludeDef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_INCLUDE_DEF, "<x include def>");
    r = X_tkINCLUDE(b, l + 1);
    r = r && X_tkSTRING(b, l + 1);
    r = r && consumeToken(b, ";");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '++'
  //    | '--'
  public static boolean X_IncrementOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_IncrementOperator")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_INCREMENT_OPERATOR, "<x increment operator>");
    r = consumeToken(b, "++");
    if (!r) r = consumeToken(b, "--");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_IncrementOperator X_BaseExpr ';'
  public static boolean X_IncrementStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_IncrementStmt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_INCREMENT_STMT, "<x increment stmt>");
    r = X_IncrementOperator(b, l + 1);
    r = r && X_BaseExpr(b, l + 1);
    r = r && consumeToken(b, ";");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // NUMBER
  public static boolean X_IntExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_IntExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_INT_EXPR, "<x int expr>");
    r = NUMBER(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'key'
  //    | 'index'
  public static boolean X_KeyIndexKind(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_KeyIndexKind")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_KEY_INDEX_KIND, "<x key index kind>");
    r = consumeToken(b, "key");
    if (!r) r = consumeToken(b, "index");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_tkLBRACK (X_ExpressionRef (',' X_ExpressionRef)*)? ']'
  public static boolean X_ListLiteralExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ListLiteralExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_LIST_LITERAL_EXPR, "<x list literal expr>");
    r = X_tkLBRACK(b, l + 1);
    r = r && X_ListLiteralExpr_1(b, l + 1);
    r = r && consumeToken(b, "]");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_ExpressionRef (',' X_ExpressionRef)*)?
  private static boolean X_ListLiteralExpr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ListLiteralExpr_1")) return false;
    X_ListLiteralExpr_1_0(b, l + 1);
    return true;
  }

  // X_ExpressionRef (',' X_ExpressionRef)*
  private static boolean X_ListLiteralExpr_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ListLiteralExpr_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_ExpressionRef(b, l + 1);
    r = r && X_ListLiteralExpr_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' X_ExpressionRef)*
  private static boolean X_ListLiteralExpr_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ListLiteralExpr_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_ListLiteralExpr_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_ListLiteralExpr_1_0_1", c)) break;
    }
    return true;
  }

  // ',' X_ExpressionRef
  private static boolean X_ListLiteralExpr_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ListLiteralExpr_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && X_ExpressionRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_BigIntExpr
  //    | X_IntExpr
  //    | X_DecimalExpr
  //    | X_StringExpr
  //    | X_BytesExpr
  //    | 'false'
  //    | 'true'
  //    | X_NullLiteralExpr
  public static boolean X_LiteralExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_LiteralExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_LITERAL_EXPR, "<x literal expr>");
    r = X_BigIntExpr(b, l + 1);
    if (!r) r = X_IntExpr(b, l + 1);
    if (!r) r = X_DecimalExpr(b, l + 1);
    if (!r) r = X_StringExpr(b, l + 1);
    if (!r) r = X_BytesExpr(b, l + 1);
    if (!r) r = consumeToken(b, "false");
    if (!r) r = consumeToken(b, "true");
    if (!r) r = X_NullLiteralExpr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_ExpressionRef ':' X_ExpressionRef
  public static boolean X_MapLiteralExprEntry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_MapLiteralExprEntry")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_MAP_LITERAL_EXPR_ENTRY, "<x map literal expr entry>");
    r = X_ExpressionRef(b, l + 1);
    r = r && consumeToken(b, ":");
    r = r && X_ExpressionRef(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_MirrorStructType0
  public static boolean X_MirrorStructExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_MirrorStructExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_MIRROR_STRUCT_EXPR, "<x mirror struct expr>");
    r = X_MirrorStructType0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_MirrorStructType0
  public static boolean X_MirrorStructType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_MirrorStructType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_MIRROR_STRUCT_TYPE, "<x mirror struct type>");
    r = X_MirrorStructType0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_tkSTRUCT '<' (X_tkMUTABLE)? X_TypeRef '>'
  public static boolean X_MirrorStructType0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_MirrorStructType0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_MIRROR_STRUCT_TYPE_0, "<x mirror struct type 0>");
    r = X_tkSTRUCT(b, l + 1);
    r = r && consumeToken(b, "<");
    r = r && X_MirrorStructType0_2(b, l + 1);
    r = r && X_TypeRef(b, l + 1);
    r = r && consumeToken(b, ">");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkMUTABLE)?
  private static boolean X_MirrorStructType0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_MirrorStructType0_2")) return false;
    X_MirrorStructType0_2_0(b, l + 1);
    return true;
  }

  // (X_tkMUTABLE)
  private static boolean X_MirrorStructType0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_MirrorStructType0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_tkMUTABLE(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'abstract'
  //    | 'override'
  //    | X_Annotation
  public static boolean X_Modifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_Modifier")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_MODIFIER, "<x modifier>");
    r = consumeToken(b, "abstract");
    if (!r) r = consumeToken(b, "override");
    if (!r) r = X_Annotation(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (X_Modifier)* X_tkMODULE ';'
  public static boolean X_ModuleHeader(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ModuleHeader")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_MODULE_HEADER, "<x module header>");
    r = X_ModuleHeader_0(b, l + 1);
    r = r && X_tkMODULE(b, l + 1);
    r = r && consumeToken(b, ";");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_Modifier)*
  private static boolean X_ModuleHeader_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ModuleHeader_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_ModuleHeader_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_ModuleHeader_0", c)) break;
    }
    return true;
  }

  // (X_Modifier)
  private static boolean X_ModuleHeader_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ModuleHeader_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_Modifier(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ID
  public static boolean X_Name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_Name")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID);
    exit_section_(b, m, X_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // X_Name
  public static boolean X_NameExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_NameExpr")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_Name(b, l + 1);
    exit_section_(b, m, X_NAME_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // X_QualifiedName
  public static boolean X_NameType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_NameType")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_QualifiedName(b, l + 1);
    exit_section_(b, m, X_NAME_TYPE, r);
    return r;
  }

  /* ********************************************************** */
  // X_Name ':' X_Type
  public static boolean X_NameTypeAttrHeader(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_NameTypeAttrHeader")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_Name(b, l + 1);
    r = r && consumeToken(b, ":");
    r = r && X_Type(b, l + 1);
    exit_section_(b, m, X_NAME_TYPE_ATTR_HEADER, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkNAMESPACE (X_QualifiedName)? '{' (X_AnnotatedDef)* '}'
  public static boolean X_NamespaceDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_NamespaceDef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_NAMESPACE_DEF, "<x namespace def>");
    r = X_tkNAMESPACE(b, l + 1);
    r = r && X_NamespaceDef_1(b, l + 1);
    r = r && consumeToken(b, "{");
    r = r && X_NamespaceDef_3(b, l + 1);
    r = r && consumeToken(b, "}");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_QualifiedName)?
  private static boolean X_NamespaceDef_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_NamespaceDef_1")) return false;
    X_NamespaceDef_1_0(b, l + 1);
    return true;
  }

  // (X_QualifiedName)
  private static boolean X_NamespaceDef_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_NamespaceDef_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_QualifiedName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_AnnotatedDef)*
  private static boolean X_NamespaceDef_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_NamespaceDef_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_NamespaceDef_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_NamespaceDef_3", c)) break;
    }
    return true;
  }

  // (X_AnnotatedDef)
  private static boolean X_NamespaceDef_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_NamespaceDef_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_AnnotatedDef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkLBRACK X_MapLiteralExprEntry (',' X_MapLiteralExprEntry)* ']'
  public static boolean X_NonEmptyMapLiteralExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_NonEmptyMapLiteralExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_NON_EMPTY_MAP_LITERAL_EXPR, "<x non empty map literal expr>");
    r = X_tkLBRACK(b, l + 1);
    r = r && X_MapLiteralExprEntry(b, l + 1);
    r = r && X_NonEmptyMapLiteralExpr_2(b, l + 1);
    r = r && consumeToken(b, "]");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (',' X_MapLiteralExprEntry)*
  private static boolean X_NonEmptyMapLiteralExpr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_NonEmptyMapLiteralExpr_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_NonEmptyMapLiteralExpr_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_NonEmptyMapLiteralExpr_2", c)) break;
    }
    return true;
  }

  // ',' X_MapLiteralExprEntry
  private static boolean X_NonEmptyMapLiteralExpr_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_NonEmptyMapLiteralExpr_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && X_MapLiteralExprEntry(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'null'
  public static boolean X_NullLiteralExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_NullLiteralExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_NULL_LITERAL_EXPR, "<x null literal expr>");
    r = consumeToken(b, "null");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_tkOBJECT X_Name '{' (X_AttributeDefinition)* '}'
  public static boolean X_ObjectDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ObjectDef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_OBJECT_DEF, "<x object def>");
    r = X_tkOBJECT(b, l + 1);
    r = r && X_Name(b, l + 1);
    r = r && consumeToken(b, "{");
    r = r && X_ObjectDef_3(b, l + 1);
    r = r && consumeToken(b, "}");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_AttributeDefinition)*
  private static boolean X_ObjectDef_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ObjectDef_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_ObjectDef_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_ObjectDef_3", c)) break;
    }
    return true;
  }

  // (X_AttributeDefinition)
  private static boolean X_ObjectDef_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ObjectDef_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_AttributeDefinition(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkOPERATION X_Name '(' (X_FormalParameter (',' X_FormalParameter)*)? ')' X_BlockStmt
  public static boolean X_OpDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_OpDef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_OP_DEF, "<x op def>");
    r = X_tkOPERATION(b, l + 1);
    r = r && X_Name(b, l + 1);
    r = r && consumeToken(b, "(");
    r = r && X_OpDef_3(b, l + 1);
    r = r && consumeToken(b, ")");
    r = r && X_BlockStmt(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_FormalParameter (',' X_FormalParameter)*)?
  private static boolean X_OpDef_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_OpDef_3")) return false;
    X_OpDef_3_0(b, l + 1);
    return true;
  }

  // X_FormalParameter (',' X_FormalParameter)*
  private static boolean X_OpDef_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_OpDef_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_FormalParameter(b, l + 1);
    r = r && X_OpDef_3_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' X_FormalParameter)*
  private static boolean X_OpDef_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_OpDef_3_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_OpDef_3_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_OpDef_3_0_1", c)) break;
    }
    return true;
  }

  // ',' X_FormalParameter
  private static boolean X_OpDef_3_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_OpDef_3_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && X_FormalParameter(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_BaseExpr
  //    | X_IfExpr
  //    | X_WhenExpr
  public static boolean X_OperandExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_OperandExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_OPERAND_EXPR, "<x operand expr>");
    r = X_BaseExpr(b, l + 1);
    if (!r) r = X_IfExpr(b, l + 1);
    if (!r) r = X_WhenExpr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_tkLPAR X_TupleExprField (X_TupleExprTail)? ')'
  public static boolean X_ParenthesesExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ParenthesesExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_PARENTHESES_EXPR, "<x parentheses expr>");
    r = X_tkLPAR(b, l + 1);
    r = r && X_TupleExprField(b, l + 1);
    r = r && X_ParenthesesExpr_2(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_TupleExprTail)?
  private static boolean X_ParenthesesExpr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ParenthesesExpr_2")) return false;
    X_ParenthesesExpr_2_0(b, l + 1);
    return true;
  }

  // (X_TupleExprTail)
  private static boolean X_ParenthesesExpr_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ParenthesesExpr_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_TupleExprTail(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_GenericType
  //    | X_NameType
  //    | X_TupleType
  //    | X_VirtualType
  //    | X_MirrorStructType
  public static boolean X_PrimaryType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_PrimaryType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_PRIMARY_TYPE, "<x primary type>");
    r = X_GenericType(b, l + 1);
    if (!r) r = X_NameType(b, l + 1);
    if (!r) r = X_TupleType(b, l + 1);
    if (!r) r = X_VirtualType(b, l + 1);
    if (!r) r = X_MirrorStructType(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_Name ('.' X_Name)*
  public static boolean X_QualifiedName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_QualifiedName")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_Name(b, l + 1);
    r = r && X_QualifiedName_1(b, l + 1);
    exit_section_(b, m, X_QUALIFIED_NAME, r);
    return r;
  }

  // ('.' X_Name)*
  private static boolean X_QualifiedName_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_QualifiedName_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_QualifiedName_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_QualifiedName_1", c)) break;
    }
    return true;
  }

  // '.' X_Name
  private static boolean X_QualifiedName_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_QualifiedName_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ".");
    r = r && X_Name(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_FunctionBodyShort
  //    | X_FunctionBodyFull
  public static boolean X_QueryBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_QueryBody")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_QUERY_BODY, "<x query body>");
    r = X_FunctionBodyShort(b, l + 1);
    if (!r) r = X_FunctionBodyFull(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_tkQUERY X_Name '(' (X_FormalParameter (',' X_FormalParameter)*)? ')' (':' X_Type)? X_QueryBody
  public static boolean X_QueryDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_QueryDef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_QUERY_DEF, "<x query def>");
    r = X_tkQUERY(b, l + 1);
    r = r && X_Name(b, l + 1);
    r = r && consumeToken(b, "(");
    r = r && X_QueryDef_3(b, l + 1);
    r = r && consumeToken(b, ")");
    r = r && X_QueryDef_5(b, l + 1);
    r = r && X_QueryBody(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_FormalParameter (',' X_FormalParameter)*)?
  private static boolean X_QueryDef_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_QueryDef_3")) return false;
    X_QueryDef_3_0(b, l + 1);
    return true;
  }

  // X_FormalParameter (',' X_FormalParameter)*
  private static boolean X_QueryDef_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_QueryDef_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_FormalParameter(b, l + 1);
    r = r && X_QueryDef_3_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' X_FormalParameter)*
  private static boolean X_QueryDef_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_QueryDef_3_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_QueryDef_3_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_QueryDef_3_0_1", c)) break;
    }
    return true;
  }

  // ',' X_FormalParameter
  private static boolean X_QueryDef_3_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_QueryDef_3_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && X_FormalParameter(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (':' X_Type)?
  private static boolean X_QueryDef_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_QueryDef_5")) return false;
    X_QueryDef_5_0(b, l + 1);
    return true;
  }

  // ':' X_Type
  private static boolean X_QueryDef_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_QueryDef_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ":");
    r = r && X_Type(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_RelAttributeClause
  //    | X_RelKeyIndexClause
  public static boolean X_RelAnyClause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_RelAnyClause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_REL_ANY_CLAUSE, "<x rel any clause>");
    r = X_RelAttributeClause(b, l + 1);
    if (!r) r = X_RelKeyIndexClause(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_AttributeDefinition
  public static boolean X_RelAttributeClause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_RelAttributeClause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_REL_ATTRIBUTE_CLAUSE, "<x rel attribute clause>");
    r = X_AttributeDefinition(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_KeyIndexKind X_BaseAttributeDefinition (',' X_BaseAttributeDefinition)* ';'
  public static boolean X_RelKeyIndexClause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_RelKeyIndexClause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_REL_KEY_INDEX_CLAUSE, "<x rel key index clause>");
    r = X_KeyIndexKind(b, l + 1);
    r = r && X_BaseAttributeDefinition(b, l + 1);
    r = r && X_RelKeyIndexClause_2(b, l + 1);
    r = r && consumeToken(b, ";");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (',' X_BaseAttributeDefinition)*
  private static boolean X_RelKeyIndexClause_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_RelKeyIndexClause_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_RelKeyIndexClause_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_RelKeyIndexClause_2", c)) break;
    }
    return true;
  }

  // ',' X_BaseAttributeDefinition
  private static boolean X_RelKeyIndexClause_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_RelKeyIndexClause_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && X_BaseAttributeDefinition(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkDOT (X_QualifiedName)?
  public static boolean X_RelativeImportModule(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_RelativeImportModule")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_RELATIVE_IMPORT_MODULE, "<x relative import module>");
    r = X_tkDOT(b, l + 1);
    r = r && X_RelativeImportModule_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_QualifiedName)?
  private static boolean X_RelativeImportModule_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_RelativeImportModule_1")) return false;
    X_RelativeImportModule_1_0(b, l + 1);
    return true;
  }

  // (X_QualifiedName)
  private static boolean X_RelativeImportModule_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_RelativeImportModule_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_QualifiedName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkRETURN (X_Expression)? ';'
  public static boolean X_ReturnStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ReturnStmt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_RETURN_STMT, "<x return stmt>");
    r = X_tkRETURN(b, l + 1);
    r = r && X_ReturnStmt_1(b, l + 1);
    r = r && consumeToken(b, ";");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_Expression)?
  private static boolean X_ReturnStmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ReturnStmt_1")) return false;
    X_ReturnStmt_1_0(b, l + 1);
    return true;
  }

  // (X_Expression)
  private static boolean X_ReturnStmt_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ReturnStmt_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_Expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (X_ModuleHeader)? (X_AnnotatedDef)*
  static boolean X_RootParser(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_RootParser")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_RootParser_0(b, l + 1);
    r = r && X_RootParser_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_ModuleHeader)?
  private static boolean X_RootParser_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_RootParser_0")) return false;
    X_RootParser_0_0(b, l + 1);
    return true;
  }

  // (X_ModuleHeader)
  private static boolean X_RootParser_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_RootParser_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_ModuleHeader(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_AnnotatedDef)*
  private static boolean X_RootParser_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_RootParser_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_RootParser_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_RootParser_1", c)) break;
    }
    return true;
  }

  // (X_AnnotatedDef)
  private static boolean X_RootParser_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_RootParser_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_AnnotatedDef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_AttrHeader
  public static boolean X_SimpleVarDeclarator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_SimpleVarDeclarator")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_AttrHeader(b, l + 1);
    exit_section_(b, m, X_SIMPLE_VAR_DECLARATOR, r);
    return r;
  }

  /* ********************************************************** */
  // X_EmptyStmt
  //    | X_VarStmt
  //    | X_AssignStmt
  //    | X_ReturnStmt
  //    | X_BlockStmt
  //    | X_IfStmt
  //    | X_WhenStmt
  //    | X_WhileStmt
  //    | X_ForStmt
  //    | X_BreakStmt
  //    | X_ContinueStmt
  //    | X_UpdateStmt
  //    | X_DeleteStmt
  //    | X_IncrementStmt
  //    | X_CallStmt
  //    | X_CreateStmt
  //    | X_GuardStmt
  public static boolean X_Statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_Statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_STATEMENT, "<x statement>");
    r = X_EmptyStmt(b, l + 1);
    if (!r) r = X_VarStmt(b, l + 1);
    if (!r) r = X_AssignStmt(b, l + 1);
    if (!r) r = X_ReturnStmt(b, l + 1);
    if (!r) r = X_BlockStmt(b, l + 1);
    if (!r) r = X_IfStmt(b, l + 1);
    if (!r) r = X_WhenStmt(b, l + 1);
    if (!r) r = X_WhileStmt(b, l + 1);
    if (!r) r = X_ForStmt(b, l + 1);
    if (!r) r = X_BreakStmt(b, l + 1);
    if (!r) r = X_ContinueStmt(b, l + 1);
    if (!r) r = X_UpdateStmt(b, l + 1);
    if (!r) r = X_DeleteStmt(b, l + 1);
    if (!r) r = X_IncrementStmt(b, l + 1);
    if (!r) r = X_CallStmt(b, l + 1);
    if (!r) r = X_CreateStmt(b, l + 1);
    if (!r) r = X_GuardStmt(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_Statement
  public static boolean X_StatementRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_StatementRef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_STATEMENT_REF, "<x statement ref>");
    r = X_Statement(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // STRING
  public static boolean X_StringExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_StringExpr")) return false;
    if (!nextTokenIs(b, STRING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STRING);
    exit_section_(b, m, X_STRING_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // X_StructKeyword X_Name '{' (X_AttributeDefinition)* '}'
  public static boolean X_StructDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_StructDef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_STRUCT_DEF, "<x struct def>");
    r = X_StructKeyword(b, l + 1);
    r = r && X_Name(b, l + 1);
    r = r && consumeToken(b, "{");
    r = r && X_StructDef_3(b, l + 1);
    r = r && consumeToken(b, "}");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_AttributeDefinition)*
  private static boolean X_StructDef_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_StructDef_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_StructDef_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_StructDef_3", c)) break;
    }
    return true;
  }

  // (X_AttributeDefinition)
  private static boolean X_StructDef_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_StructDef_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_AttributeDefinition(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'struct'
  //    | 'record'
  public static boolean X_StructKeyword(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_StructKeyword")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_STRUCT_KEYWORD, "<x struct keyword>");
    r = consumeToken(b, "struct");
    if (!r) r = consumeToken(b, "record");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_TupleExprFieldNameEqExpr
  //    | X_TupleExprFieldNameColonExpr
  //    | X_TupleExprFieldExpr
  public static boolean X_TupleExprField(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleExprField")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TUPLE_EXPR_FIELD, "<x tuple expr field>");
    r = X_TupleExprFieldNameEqExpr(b, l + 1);
    if (!r) r = X_TupleExprFieldNameColonExpr(b, l + 1);
    if (!r) r = X_TupleExprFieldExpr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_ExpressionRef
  public static boolean X_TupleExprFieldExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleExprFieldExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TUPLE_EXPR_FIELD_EXPR, "<x tuple expr field expr>");
    r = X_ExpressionRef(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_Name X_tkCOLON X_ExpressionRef
  public static boolean X_TupleExprFieldNameColonExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleExprFieldNameColonExpr")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_Name(b, l + 1);
    r = r && X_tkCOLON(b, l + 1);
    r = r && X_ExpressionRef(b, l + 1);
    exit_section_(b, m, X_TUPLE_EXPR_FIELD_NAME_COLON_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // X_Name X_tkASSIGN X_ExpressionRef
  public static boolean X_TupleExprFieldNameEqExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleExprFieldNameEqExpr")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_Name(b, l + 1);
    r = r && X_tkASSIGN(b, l + 1);
    r = r && X_ExpressionRef(b, l + 1);
    exit_section_(b, m, X_TUPLE_EXPR_FIELD_NAME_EQ_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // ',' (X_TupleExprField (',' X_TupleExprField)*)?
  public static boolean X_TupleExprTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleExprTail")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TUPLE_EXPR_TAIL, "<x tuple expr tail>");
    r = consumeToken(b, ",");
    r = r && X_TupleExprTail_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_TupleExprField (',' X_TupleExprField)*)?
  private static boolean X_TupleExprTail_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleExprTail_1")) return false;
    X_TupleExprTail_1_0(b, l + 1);
    return true;
  }

  // X_TupleExprField (',' X_TupleExprField)*
  private static boolean X_TupleExprTail_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleExprTail_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_TupleExprField(b, l + 1);
    r = r && X_TupleExprTail_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' X_TupleExprField)*
  private static boolean X_TupleExprTail_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleExprTail_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_TupleExprTail_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_TupleExprTail_1_0_1", c)) break;
    }
    return true;
  }

  // ',' X_TupleExprField
  private static boolean X_TupleExprTail_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleExprTail_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && X_TupleExprField(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkLPAR X_TupleTypeField (X_TupleTypeTail)? ')'
  public static boolean X_TupleType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TUPLE_TYPE, "<x tuple type>");
    r = X_tkLPAR(b, l + 1);
    r = r && X_TupleTypeField(b, l + 1);
    r = r && X_TupleType_2(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_TupleTypeTail)?
  private static boolean X_TupleType_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleType_2")) return false;
    X_TupleType_2_0(b, l + 1);
    return true;
  }

  // (X_TupleTypeTail)
  private static boolean X_TupleType_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleType_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_TupleTypeTail(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (X_Name ':')? X_TypeRef
  public static boolean X_TupleTypeField(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleTypeField")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TUPLE_TYPE_FIELD, "<x tuple type field>");
    r = X_TupleTypeField_0(b, l + 1);
    r = r && X_TypeRef(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_Name ':')?
  private static boolean X_TupleTypeField_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleTypeField_0")) return false;
    X_TupleTypeField_0_0(b, l + 1);
    return true;
  }

  // X_Name ':'
  private static boolean X_TupleTypeField_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleTypeField_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_Name(b, l + 1);
    r = r && consumeToken(b, ":");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ',' (X_TupleTypeField (',' X_TupleTypeField)*)?
  public static boolean X_TupleTypeTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleTypeTail")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TUPLE_TYPE_TAIL, "<x tuple type tail>");
    r = consumeToken(b, ",");
    r = r && X_TupleTypeTail_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_TupleTypeField (',' X_TupleTypeField)*)?
  private static boolean X_TupleTypeTail_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleTypeTail_1")) return false;
    X_TupleTypeTail_1_0(b, l + 1);
    return true;
  }

  // X_TupleTypeField (',' X_TupleTypeField)*
  private static boolean X_TupleTypeTail_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleTypeTail_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_TupleTypeField(b, l + 1);
    r = r && X_TupleTypeTail_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' X_TupleTypeField)*
  private static boolean X_TupleTypeTail_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleTypeTail_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_TupleTypeTail_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_TupleTypeTail_1_0_1", c)) break;
    }
    return true;
  }

  // ',' X_TupleTypeField
  private static boolean X_TupleTypeTail_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleTypeTail_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && X_TupleTypeField(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkLPAR X_VarDeclarator (',' X_VarDeclarator)* ')'
  public static boolean X_TupleVarDeclarator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleVarDeclarator")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TUPLE_VAR_DECLARATOR, "<x tuple var declarator>");
    r = X_tkLPAR(b, l + 1);
    r = r && X_VarDeclarator(b, l + 1);
    r = r && X_TupleVarDeclarator_2(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (',' X_VarDeclarator)*
  private static boolean X_TupleVarDeclarator_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleVarDeclarator_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_TupleVarDeclarator_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_TupleVarDeclarator_2", c)) break;
    }
    return true;
  }

  // ',' X_VarDeclarator
  private static boolean X_TupleVarDeclarator_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleVarDeclarator_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && X_VarDeclarator(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_ComplexNullableType
  //    | X_FunctionType
  //    | X_BasicType
  public static boolean X_Type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_Type")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TYPE, "<x type>");
    r = X_ComplexNullableType(b, l + 1);
    if (!r) r = X_FunctionType(b, l + 1);
    if (!r) r = X_BasicType(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_Type
  public static boolean X_TypeRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TypeRef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TYPE_REF, "<x type ref>");
    r = X_Type(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (X_UnaryPrefixOperator)* X_OperandExpr
  public static boolean X_UnaryExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UnaryExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_UNARY_EXPR, "<x unary expr>");
    r = X_UnaryExpr_0(b, l + 1);
    r = r && X_OperandExpr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_UnaryPrefixOperator)*
  private static boolean X_UnaryExpr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UnaryExpr_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_UnaryExpr_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_UnaryExpr_0", c)) break;
    }
    return true;
  }

  // (X_UnaryPrefixOperator)
  private static boolean X_UnaryExpr_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UnaryExpr_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_UnaryPrefixOperator(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_IncrementOperator
  //    | '??'
  public static boolean X_UnaryPostfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UnaryPostfixOperator")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_UNARY_POSTFIX_OPERATOR, "<x unary postfix operator>");
    r = X_IncrementOperator(b, l + 1);
    if (!r) r = consumeToken(b, "??");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '+'
  //    | '-'
  //    | 'not'
  //    | X_IncrementOperator
  public static boolean X_UnaryPrefixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UnaryPrefixOperator")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_UNARY_PREFIX_OPERATOR, "<x unary prefix operator>");
    r = consumeToken(b, "+");
    if (!r) r = consumeToken(b, "-");
    if (!r) r = consumeToken(b, "not");
    if (!r) r = X_IncrementOperator(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (X_tkCARET)+ ('.' X_QualifiedName)?
  public static boolean X_UpImportModule(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpImportModule")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_UP_IMPORT_MODULE, "<x up import module>");
    r = X_UpImportModule_0(b, l + 1);
    r = r && X_UpImportModule_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkCARET)+
  private static boolean X_UpImportModule_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpImportModule_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_UpImportModule_0_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!X_UpImportModule_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_UpImportModule_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkCARET)
  private static boolean X_UpImportModule_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpImportModule_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_tkCARET(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('.' X_QualifiedName)?
  private static boolean X_UpImportModule_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpImportModule_1")) return false;
    X_UpImportModule_1_0(b, l + 1);
    return true;
  }

  // '.' X_QualifiedName
  private static boolean X_UpImportModule_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpImportModule_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ".");
    r = r && X_QualifiedName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkUPDATE X_UpdateTarget '(' (X_UpdateWhatExpr (',' X_UpdateWhatExpr)*)? ')' ';'
  public static boolean X_UpdateStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpdateStmt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_UPDATE_STMT, "<x update stmt>");
    r = X_tkUPDATE(b, l + 1);
    r = r && X_UpdateTarget(b, l + 1);
    r = r && consumeToken(b, "(");
    r = r && X_UpdateStmt_3(b, l + 1);
    r = r && consumeToken(b, ")");
    r = r && consumeToken(b, ";");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_UpdateWhatExpr (',' X_UpdateWhatExpr)*)?
  private static boolean X_UpdateStmt_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpdateStmt_3")) return false;
    X_UpdateStmt_3_0(b, l + 1);
    return true;
  }

  // X_UpdateWhatExpr (',' X_UpdateWhatExpr)*
  private static boolean X_UpdateStmt_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpdateStmt_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_UpdateWhatExpr(b, l + 1);
    r = r && X_UpdateStmt_3_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' X_UpdateWhatExpr)*
  private static boolean X_UpdateStmt_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpdateStmt_3_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_UpdateStmt_3_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_UpdateStmt_3_0_1", c)) break;
    }
    return true;
  }

  // ',' X_UpdateWhatExpr
  private static boolean X_UpdateStmt_3_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpdateStmt_3_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && X_UpdateWhatExpr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_UpdateTargetAt
  //    | X_UpdateTargetExpr
  public static boolean X_UpdateTarget(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpdateTarget")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_UPDATE_TARGET, "<x update target>");
    r = X_UpdateTargetAt(b, l + 1);
    if (!r) r = X_UpdateTargetExpr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_AtExprFrom X_AtExprAt X_AtExprWhere
  public static boolean X_UpdateTargetAt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpdateTargetAt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_UPDATE_TARGET_AT, "<x update target at>");
    r = X_AtExprFrom(b, l + 1);
    r = r && X_AtExprAt(b, l + 1);
    r = r && X_AtExprWhere(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_BaseExprNoCallNoAt
  public static boolean X_UpdateTargetExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpdateTargetExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_UPDATE_TARGET_EXPR, "<x update target expr>");
    r = X_BaseExprNoCallNoAt(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (X_UpdateWhatNameOp)? X_Expression
  public static boolean X_UpdateWhatExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpdateWhatExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_UPDATE_WHAT_EXPR, "<x update what expr>");
    r = X_UpdateWhatExpr_0(b, l + 1);
    r = r && X_Expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_UpdateWhatNameOp)?
  private static boolean X_UpdateWhatExpr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpdateWhatExpr_0")) return false;
    X_UpdateWhatExpr_0_0(b, l + 1);
    return true;
  }

  // (X_UpdateWhatNameOp)
  private static boolean X_UpdateWhatExpr_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpdateWhatExpr_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_UpdateWhatNameOp(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ('.')? X_Name X_AssignOp
  public static boolean X_UpdateWhatNameOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpdateWhatNameOp")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_UPDATE_WHAT_NAME_OP, "<x update what name op>");
    r = X_UpdateWhatNameOp_0(b, l + 1);
    r = r && X_Name(b, l + 1);
    r = r && X_AssignOp(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ('.')?
  private static boolean X_UpdateWhatNameOp_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpdateWhatNameOp_0")) return false;
    X_UpdateWhatNameOp_0_0(b, l + 1);
    return true;
  }

  // ('.')
  private static boolean X_UpdateWhatNameOp_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpdateWhatNameOp_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ".");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_SimpleVarDeclarator
  //    | X_TupleVarDeclarator
  public static boolean X_VarDeclarator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_VarDeclarator")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_VAR_DECLARATOR, "<x var declarator>");
    r = X_SimpleVarDeclarator(b, l + 1);
    if (!r) r = X_TupleVarDeclarator(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_VarVal X_VarDeclarator ('=' X_Expression)? ';'
  public static boolean X_VarStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_VarStmt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_VAR_STMT, "<x var stmt>");
    r = X_VarVal(b, l + 1);
    r = r && X_VarDeclarator(b, l + 1);
    r = r && X_VarStmt_2(b, l + 1);
    r = r && consumeToken(b, ";");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ('=' X_Expression)?
  private static boolean X_VarStmt_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_VarStmt_2")) return false;
    X_VarStmt_2_0(b, l + 1);
    return true;
  }

  // '=' X_Expression
  private static boolean X_VarStmt_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_VarStmt_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "=");
    r = r && X_Expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'val'
  //    | 'var'
  public static boolean X_VarVal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_VarVal")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_VAR_VAL, "<x var val>");
    r = consumeToken(b, "val");
    if (!r) r = consumeToken(b, "var");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_tkVIRTUAL '<' X_TypeRef '>'
  public static boolean X_VirtualType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_VirtualType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_VIRTUAL_TYPE, "<x virtual type>");
    r = X_tkVIRTUAL(b, l + 1);
    r = r && consumeToken(b, "<");
    r = r && X_TypeRef(b, l + 1);
    r = r && consumeToken(b, ">");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_VirtualType
  public static boolean X_VirtualTypeExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_VirtualTypeExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_VIRTUAL_TYPE_EXPR, "<x virtual type expr>");
    r = X_VirtualType(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_WhenConditionExpr
  //    | X_WhenConditionElse
  public static boolean X_WhenCondition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenCondition")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_WHEN_CONDITION, "<x when condition>");
    r = X_WhenConditionExpr(b, l + 1);
    if (!r) r = X_WhenConditionElse(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'else'
  public static boolean X_WhenConditionElse(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenConditionElse")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_WHEN_CONDITION_ELSE, "<x when condition else>");
    r = consumeToken(b, "else");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_ExpressionRef (',' X_ExpressionRef)*
  public static boolean X_WhenConditionExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenConditionExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_WHEN_CONDITION_EXPR, "<x when condition expr>");
    r = X_ExpressionRef(b, l + 1);
    r = r && X_WhenConditionExpr_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (',' X_ExpressionRef)*
  private static boolean X_WhenConditionExpr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenConditionExpr_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_WhenConditionExpr_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_WhenConditionExpr_1", c)) break;
    }
    return true;
  }

  // ',' X_ExpressionRef
  private static boolean X_WhenConditionExpr_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenConditionExpr_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && X_ExpressionRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkWHEN ('(' X_ExpressionRef ')')? '{' X_WhenExprCases '}'
  public static boolean X_WhenExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_WHEN_EXPR, "<x when expr>");
    r = X_tkWHEN(b, l + 1);
    r = r && X_WhenExpr_1(b, l + 1);
    r = r && consumeToken(b, "{");
    r = r && X_WhenExprCases(b, l + 1);
    r = r && consumeToken(b, "}");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ('(' X_ExpressionRef ')')?
  private static boolean X_WhenExpr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenExpr_1")) return false;
    X_WhenExpr_1_0(b, l + 1);
    return true;
  }

  // '(' X_ExpressionRef ')'
  private static boolean X_WhenExpr_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenExpr_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "(");
    r = r && X_ExpressionRef(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_WhenCondition '->' X_ExpressionRef
  public static boolean X_WhenExprCase(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenExprCase")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_WHEN_EXPR_CASE, "<x when expr case>");
    r = X_WhenCondition(b, l + 1);
    r = r && consumeToken(b, "->");
    r = r && X_ExpressionRef(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_WhenExprCase ((';')+ X_WhenExprCase)* (X_tkSEMI)*
  public static boolean X_WhenExprCases(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenExprCases")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_WHEN_EXPR_CASES, "<x when expr cases>");
    r = X_WhenExprCase(b, l + 1);
    r = r && X_WhenExprCases_1(b, l + 1);
    r = r && X_WhenExprCases_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ((';')+ X_WhenExprCase)*
  private static boolean X_WhenExprCases_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenExprCases_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_WhenExprCases_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_WhenExprCases_1", c)) break;
    }
    return true;
  }

  // (';')+ X_WhenExprCase
  private static boolean X_WhenExprCases_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenExprCases_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_WhenExprCases_1_0_0(b, l + 1);
    r = r && X_WhenExprCase(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (';')+
  private static boolean X_WhenExprCases_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenExprCases_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_WhenExprCases_1_0_0_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!X_WhenExprCases_1_0_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_WhenExprCases_1_0_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // (';')
  private static boolean X_WhenExprCases_1_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenExprCases_1_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ";");
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkSEMI)*
  private static boolean X_WhenExprCases_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenExprCases_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_WhenExprCases_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_WhenExprCases_2", c)) break;
    }
    return true;
  }

  // (X_tkSEMI)
  private static boolean X_WhenExprCases_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenExprCases_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_tkSEMI(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkWHEN ('(' X_ExpressionRef ')')? '{' (X_WhenStmtCase)* '}'
  public static boolean X_WhenStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenStmt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_WHEN_STMT, "<x when stmt>");
    r = X_tkWHEN(b, l + 1);
    r = r && X_WhenStmt_1(b, l + 1);
    r = r && consumeToken(b, "{");
    r = r && X_WhenStmt_3(b, l + 1);
    r = r && consumeToken(b, "}");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ('(' X_ExpressionRef ')')?
  private static boolean X_WhenStmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenStmt_1")) return false;
    X_WhenStmt_1_0(b, l + 1);
    return true;
  }

  // '(' X_ExpressionRef ')'
  private static boolean X_WhenStmt_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenStmt_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "(");
    r = r && X_ExpressionRef(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_WhenStmtCase)*
  private static boolean X_WhenStmt_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenStmt_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_WhenStmt_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_WhenStmt_3", c)) break;
    }
    return true;
  }

  // (X_WhenStmtCase)
  private static boolean X_WhenStmt_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenStmt_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_WhenStmtCase(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_WhenCondition '->' X_StatementRef (X_tkSEMI)*
  public static boolean X_WhenStmtCase(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenStmtCase")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_WHEN_STMT_CASE, "<x when stmt case>");
    r = X_WhenCondition(b, l + 1);
    r = r && consumeToken(b, "->");
    r = r && X_StatementRef(b, l + 1);
    r = r && X_WhenStmtCase_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkSEMI)*
  private static boolean X_WhenStmtCase_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenStmtCase_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_WhenStmtCase_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_WhenStmtCase_3", c)) break;
    }
    return true;
  }

  // (X_tkSEMI)
  private static boolean X_WhenStmtCase_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenStmtCase_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_tkSEMI(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkWHILE '(' X_Expression ')' X_StatementRef
  public static boolean X_WhileStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhileStmt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_WHILE_STMT, "<x while stmt>");
    r = X_tkWHILE(b, l + 1);
    r = r && consumeToken(b, "(");
    r = r && X_Expression(b, l + 1);
    r = r && consumeToken(b, ")");
    r = r && X_StatementRef(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '='
  public static boolean X_tkASSIGN(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkASSIGN")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_ASSIGN, "<x tk assign>");
    r = consumeToken(b, "=");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '@'
  public static boolean X_tkAT(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkAT")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_AT, "<x tk at>");
    r = consumeToken(b, "@");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '->'
  public static boolean X_tkArrow(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkArrow")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_ARROW, "<x tk arrow>");
    r = consumeToken(b, "->");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'break'
  public static boolean X_tkBREAK(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkBREAK")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_BREAK, "<x tk break>");
    r = consumeToken(b, "break");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '^'
  public static boolean X_tkCARET(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkCARET")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_CARET, "<x tk caret>");
    r = consumeToken(b, "^");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ':'
  public static boolean X_tkCOLON(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkCOLON")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_COLON, "<x tk colon>");
    r = consumeToken(b, ":");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ','
  public static boolean X_tkCOMMA(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkCOMMA")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_COMMA, "<x tk comma>");
    r = consumeToken(b, ",");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'continue'
  public static boolean X_tkCONTINUE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkCONTINUE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_CONTINUE, "<x tk continue>");
    r = consumeToken(b, "continue");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'create'
  public static boolean X_tkCREATE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkCREATE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_CREATE, "<x tk create>");
    r = consumeToken(b, "create");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'delete'
  public static boolean X_tkDELETE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkDELETE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_DELETE, "<x tk delete>");
    r = consumeToken(b, "delete");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '.'
  public static boolean X_tkDOT(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkDOT")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_DOT, "<x tk dot>");
    r = consumeToken(b, ".");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'enum'
  public static boolean X_tkENUM(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkENUM")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_ENUM, "<x tk enum>");
    r = consumeToken(b, "enum");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'else'
  public static boolean X_tkElse(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkElse")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_ELSE, "<x tk else>");
    r = consumeToken(b, "else");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'for'
  public static boolean X_tkFOR(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkFOR")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_FOR, "<x tk for>");
    r = consumeToken(b, "for");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'function'
  public static boolean X_tkFUNCTION(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkFUNCTION")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_FUNCTION, "<x tk function>");
    r = consumeToken(b, "function");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'guard'
  public static boolean X_tkGUARD(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkGUARD")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_GUARD, "<x tk guard>");
    r = consumeToken(b, "guard");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'if'
  public static boolean X_tkIF(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkIF")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_IF, "<x tk if>");
    r = consumeToken(b, "if");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'import'
  public static boolean X_tkIMPORT(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkIMPORT")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_IMPORT, "<x tk import>");
    r = consumeToken(b, "import");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'in'
  public static boolean X_tkIN(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkIN")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_IN, "<x tk in>");
    r = consumeToken(b, "in");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'include'
  public static boolean X_tkINCLUDE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkINCLUDE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_INCLUDE, "<x tk include>");
    r = consumeToken(b, "include");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '['
  public static boolean X_tkLBRACK(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkLBRACK")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_LBRACK, "<x tk lbrack>");
    r = consumeToken(b, "[");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '{'
  public static boolean X_tkLCURL(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkLCURL")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_LCURL, "<x tk lcurl>");
    r = consumeToken(b, "{");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '('
  public static boolean X_tkLPAR(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkLPAR")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_LPAR, "<x tk lpar>");
    r = consumeToken(b, "(");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'limit'
  public static boolean X_tkLimit(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkLimit")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_LIMIT, "<x tk limit>");
    r = consumeToken(b, "limit");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'module'
  public static boolean X_tkMODULE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkMODULE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_MODULE, "<x tk module>");
    r = consumeToken(b, "module");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '*'
  public static boolean X_tkMUL(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkMUL")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_MUL, "<x tk mul>");
    r = consumeToken(b, "*");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'mutable'
  public static boolean X_tkMUTABLE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkMUTABLE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_MUTABLE, "<x tk mutable>");
    r = consumeToken(b, "mutable");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'namespace'
  public static boolean X_tkNAMESPACE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkNAMESPACE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_NAMESPACE, "<x tk namespace>");
    r = consumeToken(b, "namespace");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'object'
  public static boolean X_tkOBJECT(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkOBJECT")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_OBJECT, "<x tk object>");
    r = consumeToken(b, "object");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'operation'
  public static boolean X_tkOPERATION(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkOPERATION")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_OPERATION, "<x tk operation>");
    r = consumeToken(b, "operation");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'offset'
  public static boolean X_tkOffset(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkOffset")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_OFFSET, "<x tk offset>");
    r = consumeToken(b, "offset");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '+'
  public static boolean X_tkPLUS(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkPLUS")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_PLUS, "<x tk plus>");
    r = consumeToken(b, "+");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'query'
  public static boolean X_tkQUERY(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkQUERY")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_QUERY, "<x tk query>");
    r = consumeToken(b, "query");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '?'
  public static boolean X_tkQUESTION(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkQUESTION")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_QUESTION, "<x tk question>");
    r = consumeToken(b, "?");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ']'
  public static boolean X_tkRBRACK(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkRBRACK")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_RBRACK, "<x tk rbrack>");
    r = consumeToken(b, "]");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '}'
  public static boolean X_tkRCURL(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkRCURL")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_RCURL, "<x tk rcurl>");
    r = consumeToken(b, "}");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'return'
  public static boolean X_tkRETURN(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkRETURN")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_RETURN, "<x tk return>");
    r = consumeToken(b, "return");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ')'
  public static boolean X_tkRPAR(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkRPAR")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_RPAR, "<x tk rpar>");
    r = consumeToken(b, ")");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ';'
  public static boolean X_tkSEMI(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkSEMI")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_SEMI, "<x tk semi>");
    r = consumeToken(b, ";");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // STRING
  public static boolean X_tkSTRING(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkSTRING")) return false;
    if (!nextTokenIs(b, STRING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STRING);
    exit_section_(b, m, X_TK_STRING, r);
    return r;
  }

  /* ********************************************************** */
  // 'struct'
  public static boolean X_tkSTRUCT(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkSTRUCT")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_STRUCT, "<x tk struct>");
    r = consumeToken(b, "struct");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'update'
  public static boolean X_tkUPDATE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkUPDATE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_UPDATE, "<x tk update>");
    r = consumeToken(b, "update");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'val'
  public static boolean X_tkVAL(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkVAL")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_VAL, "<x tk val>");
    r = consumeToken(b, "val");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'virtual'
  public static boolean X_tkVIRTUAL(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkVIRTUAL")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_VIRTUAL, "<x tk virtual>");
    r = consumeToken(b, "virtual");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'when'
  public static boolean X_tkWHEN(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkWHEN")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_WHEN, "<x tk when>");
    r = consumeToken(b, "when");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'while'
  public static boolean X_tkWHILE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_tkWHILE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TK_WHILE, "<x tk while>");
    r = consumeToken(b, "while");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

}
