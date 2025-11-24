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
    if (!nextTokenIs(b, "<big integer>", DECNUM, HEXDIGNUM)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BIG_INTEGER, "<big integer>");
    r = COMMON_INT(b, l + 1);
    r = r && consumeToken(b, "L");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // HEXDIGNUM | DECNUM
  public static boolean COMMON_INT(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "COMMON_INT")) return false;
    if (!nextTokenIs(b, "<common int>", DECNUM, HEXDIGNUM)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COMMON_INT, "<common int>");
    r = consumeToken(b, HEXDIGNUM);
    if (!r) r = consumeToken(b, DECNUM);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // COMMON_INT
  public static boolean NUMBER(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NUMBER")) return false;
    if (!nextTokenIs(b, "<number>", DECNUM, HEXDIGNUM)) return false;
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
  // X_Modifiers X_AnyDef
  public static boolean X_AnnotatedDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AnnotatedDef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_ANNOTATED_DEF, "<x annotated def>");
    r = X_Modifiers(b, l + 1);
    r = r && X_AnyDef(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_tkAT X_Name (X_AnnotationArgs)?
  public static boolean X_Annotation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_Annotation")) return false;
    if (!nextTokenIs(b, X_TKAT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKAT);
    r = r && X_Name(b, l + 1);
    r = r && X_Annotation_2(b, l + 1);
    exit_section_(b, m, X_ANNOTATION, r);
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
  // X_CommaSeparated_7
  public static boolean X_AnnotationArgs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AnnotationArgs")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CommaSeparated_7(b, l + 1);
    exit_section_(b, m, X_ANNOTATION_ARGS, r);
    return r;
  }

  /* ********************************************************** */
  // X_QualifiedNameNode (X_tkQUESTION)?
  public static boolean X_AnonAttrHeader(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AnonAttrHeader")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_QualifiedNameNode(b, l + 1);
    r = r && X_AnonAttrHeader_1(b, l + 1);
    exit_section_(b, m, X_ANON_ATTR_HEADER, r);
    return r;
  }

  // (X_tkQUESTION)?
  private static boolean X_AnonAttrHeader_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AnonAttrHeader_1")) return false;
    consumeToken(b, X_TKQUESTION);
    return true;
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
  // X_tkASSIGN
  //    | '+='
  //    | '-='
  //    | '*='
  //    | '/='
  //    | '%='
  public static boolean X_AssignOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AssignOp")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_ASSIGN_OP, "<x assign op>");
    r = consumeToken(b, X_TKASSIGN);
    if (!r) r = consumeToken(b, "+=");
    if (!r) r = consumeToken(b, "-=");
    if (!r) r = consumeToken(b, "*=");
    if (!r) r = consumeToken(b, "/=");
    if (!r) r = consumeToken(b, "%=");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_BaseExpr X_AssignOp X_Expression X_tkSEMI
  public static boolean X_AssignStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AssignStmt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_ASSIGN_STMT, "<x assign stmt>");
    r = X_BaseExpr(b, l + 1);
    r = r && X_AssignOp(b, l + 1);
    r = r && X_Expression(b, l + 1);
    r = r && consumeToken(b, X_TKSEMI);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_AtExprFrom X_BaseExprTailAt
  public static boolean X_AtExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExpr")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_AtExprFrom(b, l + 1);
    r = r && X_BaseExprTailAt(b, l + 1);
    exit_section_(b, m, X_AT_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkAT X_tkQUESTION
  //    | X_tkAT X_tkMUL
  //    | X_tkAT X_tkPLUS
  //    | X_tkAT
  public static boolean X_AtExprAt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprAt")) return false;
    if (!nextTokenIs(b, X_TKAT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parseTokens(b, 0, X_TKAT, X_TKQUESTION);
    if (!r) r = parseTokens(b, 0, X_TKAT, X_TKMUL);
    if (!r) r = parseTokens(b, 0, X_TKAT, X_TKPLUS);
    if (!r) r = consumeToken(b, X_TKAT);
    exit_section_(b, m, X_AT_EXPR_AT, r);
    return r;
  }

  /* ********************************************************** */
  // X_CommaSeparated_16
  public static boolean X_AtExprFrom(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprFrom")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CommaSeparated_16(b, l + 1);
    exit_section_(b, m, X_AT_EXPR_FROM, r);
    return r;
  }

  /* ********************************************************** */
  // (X_Annotation)* (X_NameNode X_tkCOLON)? X_ExpressionRef
  public static boolean X_AtExprFromItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprFromItem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_AT_EXPR_FROM_ITEM, "<x at expr from item>");
    r = X_AtExprFromItem_0(b, l + 1);
    r = r && X_AtExprFromItem_1(b, l + 1);
    r = r && X_ExpressionRef(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_Annotation)*
  private static boolean X_AtExprFromItem_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprFromItem_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_AtExprFromItem_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_AtExprFromItem_0", c)) break;
    }
    return true;
  }

  // (X_Annotation)
  private static boolean X_AtExprFromItem_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprFromItem_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_Annotation(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_NameNode X_tkCOLON)?
  private static boolean X_AtExprFromItem_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprFromItem_1")) return false;
    X_AtExprFromItem_1_0(b, l + 1);
    return true;
  }

  // X_NameNode X_tkCOLON
  private static boolean X_AtExprFromItem_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprFromItem_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_NameNode(b, l + 1);
    r = r && consumeToken(b, X_TKCOLON);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkLimit X_ExpressionRef
  public static boolean X_AtExprLimit(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprLimit")) return false;
    if (!nextTokenIs(b, X_TKLIMIT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLIMIT);
    r = r && X_ExpressionRef(b, l + 1);
    exit_section_(b, m, X_AT_EXPR_LIMIT, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkLimit X_ExpressionRef (X_AtExprOffset)?
  //    | X_tkOffset X_ExpressionRef (X_AtExprLimit)?
  public static boolean X_AtExprModifiers(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprModifiers")) return false;
    if (!nextTokenIs(b, "<x at expr modifiers>", X_TKLIMIT, X_TKOFFSET)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_AT_EXPR_MODIFIERS, "<x at expr modifiers>");
    r = X_AtExprModifiers_0(b, l + 1);
    if (!r) r = X_AtExprModifiers_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // X_tkLimit X_ExpressionRef (X_AtExprOffset)?
  private static boolean X_AtExprModifiers_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprModifiers_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLIMIT);
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

  // X_tkOffset X_ExpressionRef (X_AtExprLimit)?
  private static boolean X_AtExprModifiers_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprModifiers_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKOFFSET);
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
  // X_tkOffset X_ExpressionRef
  public static boolean X_AtExprOffset(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprOffset")) return false;
    if (!nextTokenIs(b, X_TKOFFSET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKOFFSET);
    r = r && X_ExpressionRef(b, l + 1);
    exit_section_(b, m, X_AT_EXPR_OFFSET, r);
    return r;
  }

  /* ********************************************************** */
  // X_AtExprWhatSimple
  //    | X_AtExprWhatComplex
  public static boolean X_AtExprWhat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprWhat")) return false;
    if (!nextTokenIs(b, "<x at expr what>", X_TKDOT, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_AT_EXPR_WHAT, "<x at expr what>");
    r = X_AtExprWhatSimple(b, l + 1);
    if (!r) r = X_AtExprWhatComplex(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_CommaSeparated_18
  public static boolean X_AtExprWhatComplex(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprWhatComplex")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CommaSeparated_18(b, l + 1);
    exit_section_(b, m, X_AT_EXPR_WHAT_COMPLEX, r);
    return r;
  }

  /* ********************************************************** */
  // (X_Annotation)* (X_NameNode X_tkASSIGN)? X_ExpressionRef
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

  // (X_NameNode X_tkASSIGN)?
  private static boolean X_AtExprWhatComplexItem_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprWhatComplexItem_1")) return false;
    X_AtExprWhatComplexItem_1_0(b, l + 1);
    return true;
  }

  // X_NameNode X_tkASSIGN
  private static boolean X_AtExprWhatComplexItem_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprWhatComplexItem_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_NameNode(b, l + 1);
    r = r && consumeToken(b, X_TKASSIGN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_Name X_tkASSIGN
  public static boolean X_AtExprWhatName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprWhatName")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_Name(b, l + 1);
    r = r && consumeToken(b, X_TKASSIGN);
    exit_section_(b, m, X_AT_EXPR_WHAT_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkDOT X_Name (X_tkDOT X_Name)*
  public static boolean X_AtExprWhatSimple(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprWhatSimple")) return false;
    if (!nextTokenIs(b, X_TKDOT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKDOT);
    r = r && X_Name(b, l + 1);
    r = r && X_AtExprWhatSimple_2(b, l + 1);
    exit_section_(b, m, X_AT_EXPR_WHAT_SIMPLE, r);
    return r;
  }

  // (X_tkDOT X_Name)*
  private static boolean X_AtExprWhatSimple_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprWhatSimple_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_AtExprWhatSimple_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_AtExprWhatSimple_2", c)) break;
    }
    return true;
  }

  // X_tkDOT X_Name
  private static boolean X_AtExprWhatSimple_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprWhatSimple_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKDOT);
    r = r && X_Name(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_CommaSeparated_20
  public static boolean X_AtExprWhere(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AtExprWhere")) return false;
    if (!nextTokenIs(b, X_TKLCURL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CommaSeparated_20(b, l + 1);
    exit_section_(b, m, X_AT_EXPR_WHERE, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkDOT X_Name
  public static boolean X_AttrExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AttrExpr")) return false;
    if (!nextTokenIs(b, X_TKDOT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKDOT);
    r = r && X_Name(b, l + 1);
    exit_section_(b, m, X_ATTR_EXPR, r);
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
  // X_AttributeDefinition
  public static boolean X_AttributeClause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AttributeClause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_ATTRIBUTE_CLAUSE, "<x attribute clause>");
    r = X_AttributeDefinition(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_BaseAttributeDefinition X_tkSEMI
  public static boolean X_AttributeDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_AttributeDefinition")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_ATTRIBUTE_DEFINITION, "<x attribute definition>");
    r = X_BaseAttributeDefinition(b, l + 1);
    r = r && consumeToken(b, X_TKSEMI);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_Modifiers X_AttrHeader (X_tkASSIGN X_ExpressionRef)?
  public static boolean X_BaseAttributeDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseAttributeDefinition")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_BASE_ATTRIBUTE_DEFINITION, "<x base attribute definition>");
    r = X_Modifiers(b, l + 1);
    r = r && X_AttrHeader(b, l + 1);
    r = r && X_BaseAttributeDefinition_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkASSIGN X_ExpressionRef)?
  private static boolean X_BaseAttributeDefinition_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseAttributeDefinition_2")) return false;
    X_BaseAttributeDefinition_2_0(b, l + 1);
    return true;
  }

  // X_tkASSIGN X_ExpressionRef
  private static boolean X_BaseAttributeDefinition_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseAttributeDefinition_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKASSIGN);
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
  //    | X_AtExpr
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
  //    | X_TupleExpr
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
    if (!r) r = X_AtExpr(b, l + 1);
    if (!r) r = X_NameExpr(b, l + 1);
    if (!r) r = consumeToken(b, X_DOLLAREXPR);
    if (!r) r = X_AttrExpr(b, l + 1);
    if (!r) r = X_BigIntExpr(b, l + 1);
    if (!r) r = X_IntExpr(b, l + 1);
    if (!r) r = X_DecimalExpr(b, l + 1);
    if (!r) r = X_StringExpr(b, l + 1);
    if (!r) r = X_BytesExpr(b, l + 1);
    if (!r) r = consumeToken(b, "false");
    if (!r) r = consumeToken(b, "true");
    if (!r) r = consumeToken(b, X_NULLLITERALEXPR);
    if (!r) r = X_TupleExpr(b, l + 1);
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
    if (!r) r = consumeToken(b, X_BASEEXPRTAILNOTNULL);
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
    if (!nextTokenIs(b, X_TKAT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_AtExprAt(b, l + 1);
    r = r && X_AtExprWhere(b, l + 1);
    r = r && X_BaseExprTailAt_2(b, l + 1);
    r = r && X_BaseExprTailAt_3(b, l + 1);
    exit_section_(b, m, X_BASE_EXPR_TAIL_AT, r);
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
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CallArgs(b, l + 1);
    exit_section_(b, m, X_BASE_EXPR_TAIL_CALL, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkDOT X_Name
  public static boolean X_BaseExprTailMember(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseExprTailMember")) return false;
    if (!nextTokenIs(b, X_TKDOT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKDOT);
    r = r && X_Name(b, l + 1);
    exit_section_(b, m, X_BASE_EXPR_TAIL_MEMBER, r);
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
    if (!r) r = consumeToken(b, X_BASEEXPRTAILNOTNULL);
    if (!r) r = X_BaseExprTailSafeMember(b, l + 1);
    if (!r) r = X_BaseExprTailUnaryPostfixOp(b, l + 1);
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
  // X_tkLBRACK X_ExpressionRef X_tkRBRACK
  public static boolean X_BaseExprTailSubscript(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BaseExprTailSubscript")) return false;
    if (!nextTokenIs(b, X_TKLBRACK)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLBRACK);
    r = r && X_ExpressionRef(b, l + 1);
    r = r && consumeToken(b, X_TKRBRACK);
    exit_section_(b, m, X_BASE_EXPR_TAIL_SUBSCRIPT, r);
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
      if (!consumeToken(b, X_TKQUESTION)) break;
      if (!empty_element_parsed_guard_(b, "X_BasicType_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // BIG_INTEGER
  public static boolean X_BigIntExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BigIntExpr")) return false;
    if (!nextTokenIs(b, "<x big int expr>", DECNUM, HEXDIGNUM)) return false;
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
  //    | X_tkLT
  //    | X_tkGT
  //    | '==='
  //    | '!=='
  //    | X_tkPLUS
  //    | '-'
  //    | X_tkMUL
  //    | '/'
  //    | '%'
  //    | 'and'
  //    | 'or'
  //    | '&'
  //    | X_tkIN
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
    if (!r) r = consumeToken(b, X_TKLT);
    if (!r) r = consumeToken(b, X_TKGT);
    if (!r) r = consumeToken(b, "===");
    if (!r) r = consumeToken(b, "!==");
    if (!r) r = consumeToken(b, X_TKPLUS);
    if (!r) r = consumeToken(b, "-");
    if (!r) r = consumeToken(b, X_TKMUL);
    if (!r) r = consumeToken(b, "/");
    if (!r) r = consumeToken(b, "%");
    if (!r) r = consumeToken(b, "and");
    if (!r) r = consumeToken(b, "or");
    if (!r) r = consumeToken(b, "&");
    if (!r) r = consumeToken(b, X_TKIN);
    if (!r) r = X_BinaryOperator_17(b, l + 1);
    if (!r) r = consumeToken(b, "?:");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'not' X_tkIN
  private static boolean X_BinaryOperator_17(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BinaryOperator_17")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "not");
    r = r && consumeToken(b, X_TKIN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkLCURL (X_StatementRef)* X_tkRCURL
  public static boolean X_BlockStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BlockStmt")) return false;
    if (!nextTokenIs(b, X_TKLCURL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLCURL);
    r = r && X_BlockStmt_1(b, l + 1);
    r = r && consumeToken(b, X_TKRCURL);
    exit_section_(b, m, X_BLOCK_STMT, r);
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
  // X_tkBREAK X_tkSEMI
  public static boolean X_BreakStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_BreakStmt")) return false;
    if (!nextTokenIs(b, X_TKBREAK)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, X_TKBREAK, X_TKSEMI);
    exit_section_(b, m, X_BREAK_STMT, r);
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
  // (X_Name X_tkASSIGN)? X_CallArgValue
  public static boolean X_CallArg(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CallArg")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_CALL_ARG, "<x call arg>");
    r = X_CallArg_0(b, l + 1);
    r = r && X_CallArgValue(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_Name X_tkASSIGN)?
  private static boolean X_CallArg_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CallArg_0")) return false;
    X_CallArg_0_0(b, l + 1);
    return true;
  }

  // X_Name X_tkASSIGN
  private static boolean X_CallArg_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CallArg_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_Name(b, l + 1);
    r = r && consumeToken(b, X_TKASSIGN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkMUL
  //    | X_ExpressionRef
  public static boolean X_CallArgValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CallArgValue")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_CALL_ARG_VALUE, "<x call arg value>");
    r = consumeToken(b, X_TKMUL);
    if (!r) r = X_ExpressionRef(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_CommaSeparated_28
  public static boolean X_CallArgs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CallArgs")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CommaSeparated_28(b, l + 1);
    exit_section_(b, m, X_CALL_ARGS, r);
    return r;
  }

  /* ********************************************************** */
  // X_BaseExpr X_tkSEMI
  public static boolean X_CallStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CallStmt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_CALL_STMT, "<x call stmt>");
    r = X_BaseExpr(b, l + 1);
    r = r && consumeToken(b, X_TKSEMI);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_TupleTypeField (X_tkCOMMA X_TupleTypeField)* (X_tkCOMMA)?
  public static boolean X_CommaSeparated_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_COMMA_SEPARATED_0, "<x comma separated 0>");
    r = X_TupleTypeField(b, l + 1);
    r = r && X_CommaSeparated_0_1(b, l + 1);
    r = r && X_CommaSeparated_0_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkCOMMA X_TupleTypeField)*
  private static boolean X_CommaSeparated_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_CommaSeparated_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_CommaSeparated_0_1", c)) break;
    }
    return true;
  }

  // X_tkCOMMA X_TupleTypeField
  private static boolean X_CommaSeparated_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKCOMMA);
    r = r && X_TupleTypeField(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkCOMMA)?
  private static boolean X_CommaSeparated_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_0_2")) return false;
    consumeToken(b, X_TKCOMMA);
    return true;
  }

  /* ********************************************************** */
  // X_tkLPAR X_CommaSeparated_0 X_tkRPAR
  public static boolean X_CommaSeparated_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_1")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLPAR);
    r = r && X_CommaSeparated_0(b, l + 1);
    r = r && consumeToken(b, X_TKRPAR);
    exit_section_(b, m, X_COMMA_SEPARATED_1, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkLPAR X_CommaSeparated_9 X_tkRPAR
  public static boolean X_CommaSeparated_10(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_10")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLPAR);
    r = r && X_CommaSeparated_9(b, l + 1);
    r = r && consumeToken(b, X_TKRPAR);
    exit_section_(b, m, X_COMMA_SEPARATED_10, r);
    return r;
  }

  /* ********************************************************** */
  // X_EnumValue (X_tkCOMMA X_EnumValue)* (X_tkCOMMA)?
  public static boolean X_CommaSeparated_11(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_11")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_EnumValue(b, l + 1);
    r = r && X_CommaSeparated_11_1(b, l + 1);
    r = r && X_CommaSeparated_11_2(b, l + 1);
    exit_section_(b, m, X_COMMA_SEPARATED_11, r);
    return r;
  }

  // (X_tkCOMMA X_EnumValue)*
  private static boolean X_CommaSeparated_11_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_11_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_CommaSeparated_11_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_CommaSeparated_11_1", c)) break;
    }
    return true;
  }

  // X_tkCOMMA X_EnumValue
  private static boolean X_CommaSeparated_11_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_11_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKCOMMA);
    r = r && X_EnumValue(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkCOMMA)?
  private static boolean X_CommaSeparated_11_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_11_2")) return false;
    consumeToken(b, X_TKCOMMA);
    return true;
  }

  /* ********************************************************** */
  // X_tkLCURL (X_CommaSeparated_11)? X_tkRCURL
  public static boolean X_CommaSeparated_12(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_12")) return false;
    if (!nextTokenIs(b, X_TKLCURL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLCURL);
    r = r && X_CommaSeparated_12_1(b, l + 1);
    r = r && consumeToken(b, X_TKRCURL);
    exit_section_(b, m, X_COMMA_SEPARATED_12, r);
    return r;
  }

  // (X_CommaSeparated_11)?
  private static boolean X_CommaSeparated_12_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_12_1")) return false;
    X_CommaSeparated_12_1_0(b, l + 1);
    return true;
  }

  // (X_CommaSeparated_11)
  private static boolean X_CommaSeparated_12_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_12_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CommaSeparated_11(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_TupleExprField (X_tkCOMMA X_TupleExprField)* (X_tkCOMMA)?
  public static boolean X_CommaSeparated_13(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_13")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_COMMA_SEPARATED_13, "<x comma separated 13>");
    r = X_TupleExprField(b, l + 1);
    r = r && X_CommaSeparated_13_1(b, l + 1);
    r = r && X_CommaSeparated_13_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkCOMMA X_TupleExprField)*
  private static boolean X_CommaSeparated_13_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_13_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_CommaSeparated_13_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_CommaSeparated_13_1", c)) break;
    }
    return true;
  }

  // X_tkCOMMA X_TupleExprField
  private static boolean X_CommaSeparated_13_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_13_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKCOMMA);
    r = r && X_TupleExprField(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkCOMMA)?
  private static boolean X_CommaSeparated_13_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_13_2")) return false;
    consumeToken(b, X_TKCOMMA);
    return true;
  }

  /* ********************************************************** */
  // X_tkLPAR X_CommaSeparated_13 X_tkRPAR
  public static boolean X_CommaSeparated_14(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_14")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLPAR);
    r = r && X_CommaSeparated_13(b, l + 1);
    r = r && consumeToken(b, X_TKRPAR);
    exit_section_(b, m, X_COMMA_SEPARATED_14, r);
    return r;
  }

  /* ********************************************************** */
  // X_AtExprFromItem (X_tkCOMMA X_AtExprFromItem)* (X_tkCOMMA)?
  public static boolean X_CommaSeparated_15(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_15")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_COMMA_SEPARATED_15, "<x comma separated 15>");
    r = X_AtExprFromItem(b, l + 1);
    r = r && X_CommaSeparated_15_1(b, l + 1);
    r = r && X_CommaSeparated_15_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkCOMMA X_AtExprFromItem)*
  private static boolean X_CommaSeparated_15_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_15_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_CommaSeparated_15_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_CommaSeparated_15_1", c)) break;
    }
    return true;
  }

  // X_tkCOMMA X_AtExprFromItem
  private static boolean X_CommaSeparated_15_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_15_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKCOMMA);
    r = r && X_AtExprFromItem(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkCOMMA)?
  private static boolean X_CommaSeparated_15_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_15_2")) return false;
    consumeToken(b, X_TKCOMMA);
    return true;
  }

  /* ********************************************************** */
  // X_tkLPAR X_CommaSeparated_15 X_tkRPAR
  public static boolean X_CommaSeparated_16(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_16")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLPAR);
    r = r && X_CommaSeparated_15(b, l + 1);
    r = r && consumeToken(b, X_TKRPAR);
    exit_section_(b, m, X_COMMA_SEPARATED_16, r);
    return r;
  }

  /* ********************************************************** */
  // X_AtExprWhatComplexItem (X_tkCOMMA X_AtExprWhatComplexItem)* (X_tkCOMMA)?
  public static boolean X_CommaSeparated_17(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_17")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_COMMA_SEPARATED_17, "<x comma separated 17>");
    r = X_AtExprWhatComplexItem(b, l + 1);
    r = r && X_CommaSeparated_17_1(b, l + 1);
    r = r && X_CommaSeparated_17_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkCOMMA X_AtExprWhatComplexItem)*
  private static boolean X_CommaSeparated_17_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_17_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_CommaSeparated_17_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_CommaSeparated_17_1", c)) break;
    }
    return true;
  }

  // X_tkCOMMA X_AtExprWhatComplexItem
  private static boolean X_CommaSeparated_17_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_17_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKCOMMA);
    r = r && X_AtExprWhatComplexItem(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkCOMMA)?
  private static boolean X_CommaSeparated_17_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_17_2")) return false;
    consumeToken(b, X_TKCOMMA);
    return true;
  }

  /* ********************************************************** */
  // X_tkLPAR X_CommaSeparated_17 X_tkRPAR
  public static boolean X_CommaSeparated_18(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_18")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLPAR);
    r = r && X_CommaSeparated_17(b, l + 1);
    r = r && consumeToken(b, X_TKRPAR);
    exit_section_(b, m, X_COMMA_SEPARATED_18, r);
    return r;
  }

  /* ********************************************************** */
  // X_ExpressionRef (X_tkCOMMA X_ExpressionRef)* (X_tkCOMMA)?
  public static boolean X_CommaSeparated_19(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_19")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_COMMA_SEPARATED_19, "<x comma separated 19>");
    r = X_ExpressionRef(b, l + 1);
    r = r && X_CommaSeparated_19_1(b, l + 1);
    r = r && X_CommaSeparated_19_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkCOMMA X_ExpressionRef)*
  private static boolean X_CommaSeparated_19_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_19_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_CommaSeparated_19_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_CommaSeparated_19_1", c)) break;
    }
    return true;
  }

  // X_tkCOMMA X_ExpressionRef
  private static boolean X_CommaSeparated_19_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_19_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKCOMMA);
    r = r && X_ExpressionRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkCOMMA)?
  private static boolean X_CommaSeparated_19_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_19_2")) return false;
    consumeToken(b, X_TKCOMMA);
    return true;
  }

  /* ********************************************************** */
  // X_TypeRef (X_tkCOMMA X_TypeRef)* (X_tkCOMMA)?
  public static boolean X_CommaSeparated_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_COMMA_SEPARATED_2, "<x comma separated 2>");
    r = X_TypeRef(b, l + 1);
    r = r && X_CommaSeparated_2_1(b, l + 1);
    r = r && X_CommaSeparated_2_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkCOMMA X_TypeRef)*
  private static boolean X_CommaSeparated_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_2_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_CommaSeparated_2_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_CommaSeparated_2_1", c)) break;
    }
    return true;
  }

  // X_tkCOMMA X_TypeRef
  private static boolean X_CommaSeparated_2_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_2_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKCOMMA);
    r = r && X_TypeRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkCOMMA)?
  private static boolean X_CommaSeparated_2_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_2_2")) return false;
    consumeToken(b, X_TKCOMMA);
    return true;
  }

  /* ********************************************************** */
  // X_tkLCURL (X_CommaSeparated_19)? X_tkRCURL
  public static boolean X_CommaSeparated_20(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_20")) return false;
    if (!nextTokenIs(b, X_TKLCURL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLCURL);
    r = r && X_CommaSeparated_20_1(b, l + 1);
    r = r && consumeToken(b, X_TKRCURL);
    exit_section_(b, m, X_COMMA_SEPARATED_20, r);
    return r;
  }

  // (X_CommaSeparated_19)?
  private static boolean X_CommaSeparated_20_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_20_1")) return false;
    X_CommaSeparated_20_1_0(b, l + 1);
    return true;
  }

  // (X_CommaSeparated_19)
  private static boolean X_CommaSeparated_20_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_20_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CommaSeparated_19(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_ExpressionRef (X_tkCOMMA X_ExpressionRef)* (X_tkCOMMA)?
  public static boolean X_CommaSeparated_21(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_21")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_COMMA_SEPARATED_21, "<x comma separated 21>");
    r = X_ExpressionRef(b, l + 1);
    r = r && X_CommaSeparated_21_1(b, l + 1);
    r = r && X_CommaSeparated_21_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkCOMMA X_ExpressionRef)*
  private static boolean X_CommaSeparated_21_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_21_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_CommaSeparated_21_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_CommaSeparated_21_1", c)) break;
    }
    return true;
  }

  // X_tkCOMMA X_ExpressionRef
  private static boolean X_CommaSeparated_21_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_21_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKCOMMA);
    r = r && X_ExpressionRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkCOMMA)?
  private static boolean X_CommaSeparated_21_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_21_2")) return false;
    consumeToken(b, X_TKCOMMA);
    return true;
  }

  /* ********************************************************** */
  // X_tkLBRACK (X_CommaSeparated_21)? X_tkRBRACK
  public static boolean X_CommaSeparated_22(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_22")) return false;
    if (!nextTokenIs(b, X_TKLBRACK)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLBRACK);
    r = r && X_CommaSeparated_22_1(b, l + 1);
    r = r && consumeToken(b, X_TKRBRACK);
    exit_section_(b, m, X_COMMA_SEPARATED_22, r);
    return r;
  }

  // (X_CommaSeparated_21)?
  private static boolean X_CommaSeparated_22_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_22_1")) return false;
    X_CommaSeparated_22_1_0(b, l + 1);
    return true;
  }

  // (X_CommaSeparated_21)
  private static boolean X_CommaSeparated_22_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_22_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CommaSeparated_21(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_MapLiteralExprEntry (X_tkCOMMA X_MapLiteralExprEntry)* (X_tkCOMMA)?
  public static boolean X_CommaSeparated_23(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_23")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_COMMA_SEPARATED_23, "<x comma separated 23>");
    r = X_MapLiteralExprEntry(b, l + 1);
    r = r && X_CommaSeparated_23_1(b, l + 1);
    r = r && X_CommaSeparated_23_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkCOMMA X_MapLiteralExprEntry)*
  private static boolean X_CommaSeparated_23_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_23_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_CommaSeparated_23_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_CommaSeparated_23_1", c)) break;
    }
    return true;
  }

  // X_tkCOMMA X_MapLiteralExprEntry
  private static boolean X_CommaSeparated_23_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_23_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKCOMMA);
    r = r && X_MapLiteralExprEntry(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkCOMMA)?
  private static boolean X_CommaSeparated_23_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_23_2")) return false;
    consumeToken(b, X_TKCOMMA);
    return true;
  }

  /* ********************************************************** */
  // X_tkLBRACK X_CommaSeparated_23 X_tkRBRACK
  public static boolean X_CommaSeparated_24(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_24")) return false;
    if (!nextTokenIs(b, X_TKLBRACK)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLBRACK);
    r = r && X_CommaSeparated_23(b, l + 1);
    r = r && consumeToken(b, X_TKRBRACK);
    exit_section_(b, m, X_COMMA_SEPARATED_24, r);
    return r;
  }

  /* ********************************************************** */
  // X_CreateExprArg (X_tkCOMMA X_CreateExprArg)* (X_tkCOMMA)?
  public static boolean X_CommaSeparated_25(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_25")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_COMMA_SEPARATED_25, "<x comma separated 25>");
    r = X_CreateExprArg(b, l + 1);
    r = r && X_CommaSeparated_25_1(b, l + 1);
    r = r && X_CommaSeparated_25_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkCOMMA X_CreateExprArg)*
  private static boolean X_CommaSeparated_25_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_25_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_CommaSeparated_25_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_CommaSeparated_25_1", c)) break;
    }
    return true;
  }

  // X_tkCOMMA X_CreateExprArg
  private static boolean X_CommaSeparated_25_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_25_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKCOMMA);
    r = r && X_CreateExprArg(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkCOMMA)?
  private static boolean X_CommaSeparated_25_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_25_2")) return false;
    consumeToken(b, X_TKCOMMA);
    return true;
  }

  /* ********************************************************** */
  // X_tkLPAR (X_CommaSeparated_25)? X_tkRPAR
  public static boolean X_CommaSeparated_26(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_26")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLPAR);
    r = r && X_CommaSeparated_26_1(b, l + 1);
    r = r && consumeToken(b, X_TKRPAR);
    exit_section_(b, m, X_COMMA_SEPARATED_26, r);
    return r;
  }

  // (X_CommaSeparated_25)?
  private static boolean X_CommaSeparated_26_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_26_1")) return false;
    X_CommaSeparated_26_1_0(b, l + 1);
    return true;
  }

  // (X_CommaSeparated_25)
  private static boolean X_CommaSeparated_26_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_26_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CommaSeparated_25(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_CallArg (X_tkCOMMA X_CallArg)* (X_tkCOMMA)?
  public static boolean X_CommaSeparated_27(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_27")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_COMMA_SEPARATED_27, "<x comma separated 27>");
    r = X_CallArg(b, l + 1);
    r = r && X_CommaSeparated_27_1(b, l + 1);
    r = r && X_CommaSeparated_27_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkCOMMA X_CallArg)*
  private static boolean X_CommaSeparated_27_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_27_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_CommaSeparated_27_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_CommaSeparated_27_1", c)) break;
    }
    return true;
  }

  // X_tkCOMMA X_CallArg
  private static boolean X_CommaSeparated_27_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_27_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKCOMMA);
    r = r && X_CallArg(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkCOMMA)?
  private static boolean X_CommaSeparated_27_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_27_2")) return false;
    consumeToken(b, X_TKCOMMA);
    return true;
  }

  /* ********************************************************** */
  // X_tkLPAR (X_CommaSeparated_27)? X_tkRPAR
  public static boolean X_CommaSeparated_28(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_28")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLPAR);
    r = r && X_CommaSeparated_28_1(b, l + 1);
    r = r && consumeToken(b, X_TKRPAR);
    exit_section_(b, m, X_COMMA_SEPARATED_28, r);
    return r;
  }

  // (X_CommaSeparated_27)?
  private static boolean X_CommaSeparated_28_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_28_1")) return false;
    X_CommaSeparated_28_1_0(b, l + 1);
    return true;
  }

  // (X_CommaSeparated_27)
  private static boolean X_CommaSeparated_28_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_28_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CommaSeparated_27(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_VarDeclarator (X_tkCOMMA X_VarDeclarator)* (X_tkCOMMA)?
  public static boolean X_CommaSeparated_29(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_29")) return false;
    if (!nextTokenIs(b, "<x comma separated 29>", ID, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_COMMA_SEPARATED_29, "<x comma separated 29>");
    r = X_VarDeclarator(b, l + 1);
    r = r && X_CommaSeparated_29_1(b, l + 1);
    r = r && X_CommaSeparated_29_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkCOMMA X_VarDeclarator)*
  private static boolean X_CommaSeparated_29_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_29_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_CommaSeparated_29_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_CommaSeparated_29_1", c)) break;
    }
    return true;
  }

  // X_tkCOMMA X_VarDeclarator
  private static boolean X_CommaSeparated_29_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_29_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKCOMMA);
    r = r && X_VarDeclarator(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkCOMMA)?
  private static boolean X_CommaSeparated_29_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_29_2")) return false;
    consumeToken(b, X_TKCOMMA);
    return true;
  }

  /* ********************************************************** */
  // X_tkLT X_CommaSeparated_2 X_tkGT
  public static boolean X_CommaSeparated_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_3")) return false;
    if (!nextTokenIs(b, X_TKLT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLT);
    r = r && X_CommaSeparated_2(b, l + 1);
    r = r && consumeToken(b, X_TKGT);
    exit_section_(b, m, X_COMMA_SEPARATED_3, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkLPAR X_CommaSeparated_29 X_tkRPAR
  public static boolean X_CommaSeparated_30(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_30")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLPAR);
    r = r && X_CommaSeparated_29(b, l + 1);
    r = r && consumeToken(b, X_TKRPAR);
    exit_section_(b, m, X_COMMA_SEPARATED_30, r);
    return r;
  }

  /* ********************************************************** */
  // X_UpdateFromItem (X_tkCOMMA X_UpdateFromItem)* (X_tkCOMMA)?
  public static boolean X_CommaSeparated_31(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_31")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_UpdateFromItem(b, l + 1);
    r = r && X_CommaSeparated_31_1(b, l + 1);
    r = r && X_CommaSeparated_31_2(b, l + 1);
    exit_section_(b, m, X_COMMA_SEPARATED_31, r);
    return r;
  }

  // (X_tkCOMMA X_UpdateFromItem)*
  private static boolean X_CommaSeparated_31_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_31_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_CommaSeparated_31_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_CommaSeparated_31_1", c)) break;
    }
    return true;
  }

  // X_tkCOMMA X_UpdateFromItem
  private static boolean X_CommaSeparated_31_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_31_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKCOMMA);
    r = r && X_UpdateFromItem(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkCOMMA)?
  private static boolean X_CommaSeparated_31_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_31_2")) return false;
    consumeToken(b, X_TKCOMMA);
    return true;
  }

  /* ********************************************************** */
  // X_tkLPAR X_CommaSeparated_31 X_tkRPAR
  public static boolean X_CommaSeparated_32(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_32")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLPAR);
    r = r && X_CommaSeparated_31(b, l + 1);
    r = r && consumeToken(b, X_TKRPAR);
    exit_section_(b, m, X_COMMA_SEPARATED_32, r);
    return r;
  }

  /* ********************************************************** */
  // X_UpdateWhatExpr (X_tkCOMMA X_UpdateWhatExpr)* (X_tkCOMMA)?
  public static boolean X_CommaSeparated_33(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_33")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_COMMA_SEPARATED_33, "<x comma separated 33>");
    r = X_UpdateWhatExpr(b, l + 1);
    r = r && X_CommaSeparated_33_1(b, l + 1);
    r = r && X_CommaSeparated_33_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkCOMMA X_UpdateWhatExpr)*
  private static boolean X_CommaSeparated_33_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_33_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_CommaSeparated_33_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_CommaSeparated_33_1", c)) break;
    }
    return true;
  }

  // X_tkCOMMA X_UpdateWhatExpr
  private static boolean X_CommaSeparated_33_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_33_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKCOMMA);
    r = r && X_UpdateWhatExpr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkCOMMA)?
  private static boolean X_CommaSeparated_33_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_33_2")) return false;
    consumeToken(b, X_TKCOMMA);
    return true;
  }

  /* ********************************************************** */
  // X_tkLPAR X_CommaSeparated_33 X_tkRPAR
  public static boolean X_CommaSeparated_34(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_34")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLPAR);
    r = r && X_CommaSeparated_33(b, l + 1);
    r = r && consumeToken(b, X_TKRPAR);
    exit_section_(b, m, X_COMMA_SEPARATED_34, r);
    return r;
  }

  /* ********************************************************** */
  // X_FormalParameter (X_tkCOMMA X_FormalParameter)* (X_tkCOMMA)?
  public static boolean X_CommaSeparated_35(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_35")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_COMMA_SEPARATED_35, "<x comma separated 35>");
    r = X_FormalParameter(b, l + 1);
    r = r && X_CommaSeparated_35_1(b, l + 1);
    r = r && X_CommaSeparated_35_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkCOMMA X_FormalParameter)*
  private static boolean X_CommaSeparated_35_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_35_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_CommaSeparated_35_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_CommaSeparated_35_1", c)) break;
    }
    return true;
  }

  // X_tkCOMMA X_FormalParameter
  private static boolean X_CommaSeparated_35_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_35_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKCOMMA);
    r = r && X_FormalParameter(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkCOMMA)?
  private static boolean X_CommaSeparated_35_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_35_2")) return false;
    consumeToken(b, X_TKCOMMA);
    return true;
  }

  /* ********************************************************** */
  // X_tkLPAR (X_CommaSeparated_35)? X_tkRPAR
  public static boolean X_CommaSeparated_36(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_36")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLPAR);
    r = r && X_CommaSeparated_36_1(b, l + 1);
    r = r && consumeToken(b, X_TKRPAR);
    exit_section_(b, m, X_COMMA_SEPARATED_36, r);
    return r;
  }

  // (X_CommaSeparated_35)?
  private static boolean X_CommaSeparated_36_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_36_1")) return false;
    X_CommaSeparated_36_1_0(b, l + 1);
    return true;
  }

  // (X_CommaSeparated_35)
  private static boolean X_CommaSeparated_36_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_36_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CommaSeparated_35(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_ImportTargetExactItem (X_tkCOMMA X_ImportTargetExactItem)* (X_tkCOMMA)?
  public static boolean X_CommaSeparated_37(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_37")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_ImportTargetExactItem(b, l + 1);
    r = r && X_CommaSeparated_37_1(b, l + 1);
    r = r && X_CommaSeparated_37_2(b, l + 1);
    exit_section_(b, m, X_COMMA_SEPARATED_37, r);
    return r;
  }

  // (X_tkCOMMA X_ImportTargetExactItem)*
  private static boolean X_CommaSeparated_37_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_37_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_CommaSeparated_37_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_CommaSeparated_37_1", c)) break;
    }
    return true;
  }

  // X_tkCOMMA X_ImportTargetExactItem
  private static boolean X_CommaSeparated_37_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_37_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKCOMMA);
    r = r && X_ImportTargetExactItem(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkCOMMA)?
  private static boolean X_CommaSeparated_37_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_37_2")) return false;
    consumeToken(b, X_TKCOMMA);
    return true;
  }

  /* ********************************************************** */
  // X_tkLCURL X_CommaSeparated_37 X_tkRCURL
  public static boolean X_CommaSeparated_38(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_38")) return false;
    if (!nextTokenIs(b, X_TKLCURL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLCURL);
    r = r && X_CommaSeparated_37(b, l + 1);
    r = r && consumeToken(b, X_TKRCURL);
    exit_section_(b, m, X_COMMA_SEPARATED_38, r);
    return r;
  }

  /* ********************************************************** */
  // X_TypeRef (X_tkCOMMA X_TypeRef)* (X_tkCOMMA)?
  public static boolean X_CommaSeparated_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_4")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_COMMA_SEPARATED_4, "<x comma separated 4>");
    r = X_TypeRef(b, l + 1);
    r = r && X_CommaSeparated_4_1(b, l + 1);
    r = r && X_CommaSeparated_4_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkCOMMA X_TypeRef)*
  private static boolean X_CommaSeparated_4_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_4_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_CommaSeparated_4_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_CommaSeparated_4_1", c)) break;
    }
    return true;
  }

  // X_tkCOMMA X_TypeRef
  private static boolean X_CommaSeparated_4_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_4_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKCOMMA);
    r = r && X_TypeRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkCOMMA)?
  private static boolean X_CommaSeparated_4_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_4_2")) return false;
    consumeToken(b, X_TKCOMMA);
    return true;
  }

  /* ********************************************************** */
  // X_tkLPAR (X_CommaSeparated_4)? X_tkRPAR
  public static boolean X_CommaSeparated_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_5")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLPAR);
    r = r && X_CommaSeparated_5_1(b, l + 1);
    r = r && consumeToken(b, X_TKRPAR);
    exit_section_(b, m, X_COMMA_SEPARATED_5, r);
    return r;
  }

  // (X_CommaSeparated_4)?
  private static boolean X_CommaSeparated_5_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_5_1")) return false;
    X_CommaSeparated_5_1_0(b, l + 1);
    return true;
  }

  // (X_CommaSeparated_4)
  private static boolean X_CommaSeparated_5_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_5_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CommaSeparated_4(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_AnnotationArg (X_tkCOMMA X_AnnotationArg)* (X_tkCOMMA)?
  public static boolean X_CommaSeparated_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_6")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_COMMA_SEPARATED_6, "<x comma separated 6>");
    r = X_AnnotationArg(b, l + 1);
    r = r && X_CommaSeparated_6_1(b, l + 1);
    r = r && X_CommaSeparated_6_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkCOMMA X_AnnotationArg)*
  private static boolean X_CommaSeparated_6_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_6_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_CommaSeparated_6_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_CommaSeparated_6_1", c)) break;
    }
    return true;
  }

  // X_tkCOMMA X_AnnotationArg
  private static boolean X_CommaSeparated_6_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_6_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKCOMMA);
    r = r && X_AnnotationArg(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkCOMMA)?
  private static boolean X_CommaSeparated_6_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_6_2")) return false;
    consumeToken(b, X_TKCOMMA);
    return true;
  }

  /* ********************************************************** */
  // X_tkLPAR (X_CommaSeparated_6)? X_tkRPAR
  public static boolean X_CommaSeparated_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_7")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLPAR);
    r = r && X_CommaSeparated_7_1(b, l + 1);
    r = r && consumeToken(b, X_TKRPAR);
    exit_section_(b, m, X_COMMA_SEPARATED_7, r);
    return r;
  }

  // (X_CommaSeparated_6)?
  private static boolean X_CommaSeparated_7_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_7_1")) return false;
    X_CommaSeparated_7_1_0(b, l + 1);
    return true;
  }

  // (X_CommaSeparated_6)
  private static boolean X_CommaSeparated_7_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_7_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CommaSeparated_6(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_BaseAttributeDefinition (X_tkCOMMA X_BaseAttributeDefinition)* (X_tkCOMMA)?
  public static boolean X_CommaSeparated_8(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_8")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_COMMA_SEPARATED_8, "<x comma separated 8>");
    r = X_BaseAttributeDefinition(b, l + 1);
    r = r && X_CommaSeparated_8_1(b, l + 1);
    r = r && X_CommaSeparated_8_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkCOMMA X_BaseAttributeDefinition)*
  private static boolean X_CommaSeparated_8_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_8_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_CommaSeparated_8_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_CommaSeparated_8_1", c)) break;
    }
    return true;
  }

  // X_tkCOMMA X_BaseAttributeDefinition
  private static boolean X_CommaSeparated_8_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_8_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKCOMMA);
    r = r && X_BaseAttributeDefinition(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkCOMMA)?
  private static boolean X_CommaSeparated_8_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_8_2")) return false;
    consumeToken(b, X_TKCOMMA);
    return true;
  }

  /* ********************************************************** */
  // X_Name (X_tkCOMMA X_Name)* (X_tkCOMMA)?
  public static boolean X_CommaSeparated_9(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_9")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_Name(b, l + 1);
    r = r && X_CommaSeparated_9_1(b, l + 1);
    r = r && X_CommaSeparated_9_2(b, l + 1);
    exit_section_(b, m, X_COMMA_SEPARATED_9, r);
    return r;
  }

  // (X_tkCOMMA X_Name)*
  private static boolean X_CommaSeparated_9_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_9_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_CommaSeparated_9_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_CommaSeparated_9_1", c)) break;
    }
    return true;
  }

  // X_tkCOMMA X_Name
  private static boolean X_CommaSeparated_9_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_9_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKCOMMA);
    r = r && X_Name(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkCOMMA)?
  private static boolean X_CommaSeparated_9_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CommaSeparated_9_2")) return false;
    consumeToken(b, X_TKCOMMA);
    return true;
  }

  /* ********************************************************** */
  // X_tkLPAR X_TypeRef X_tkRPAR X_tkQUESTION
  public static boolean X_ComplexNullableType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ComplexNullableType")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLPAR);
    r = r && X_TypeRef(b, l + 1);
    r = r && consumeTokens(b, 0, X_TKRPAR, X_TKQUESTION);
    exit_section_(b, m, X_COMPLEX_NULLABLE_TYPE, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkVAL X_Name (X_tkCOLON X_TypeRef)? X_tkASSIGN X_ExpressionRef X_tkSEMI
  public static boolean X_ConstantDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ConstantDef")) return false;
    if (!nextTokenIs(b, X_TKVAL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKVAL);
    r = r && X_Name(b, l + 1);
    r = r && X_ConstantDef_2(b, l + 1);
    r = r && consumeToken(b, X_TKASSIGN);
    r = r && X_ExpressionRef(b, l + 1);
    r = r && consumeToken(b, X_TKSEMI);
    exit_section_(b, m, X_CONSTANT_DEF, r);
    return r;
  }

  // (X_tkCOLON X_TypeRef)?
  private static boolean X_ConstantDef_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ConstantDef_2")) return false;
    X_ConstantDef_2_0(b, l + 1);
    return true;
  }

  // X_tkCOLON X_TypeRef
  private static boolean X_ConstantDef_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ConstantDef_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKCOLON);
    r = r && X_TypeRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkCONTINUE X_tkSEMI
  public static boolean X_ContinueStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ContinueStmt")) return false;
    if (!nextTokenIs(b, X_TKCONTINUE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, X_TKCONTINUE, X_TKSEMI);
    exit_section_(b, m, X_CONTINUE_STMT, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkCREATE X_QualifiedName X_CreateExprArgs
  public static boolean X_CreateExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CreateExpr")) return false;
    if (!nextTokenIs(b, X_TKCREATE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKCREATE);
    r = r && X_QualifiedName(b, l + 1);
    r = r && X_CreateExprArgs(b, l + 1);
    exit_section_(b, m, X_CREATE_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // ((X_tkDOT)? X_Name X_tkASSIGN)? X_CallArgValue
  public static boolean X_CreateExprArg(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CreateExprArg")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_CREATE_EXPR_ARG, "<x create expr arg>");
    r = X_CreateExprArg_0(b, l + 1);
    r = r && X_CallArgValue(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ((X_tkDOT)? X_Name X_tkASSIGN)?
  private static boolean X_CreateExprArg_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CreateExprArg_0")) return false;
    X_CreateExprArg_0_0(b, l + 1);
    return true;
  }

  // (X_tkDOT)? X_Name X_tkASSIGN
  private static boolean X_CreateExprArg_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CreateExprArg_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CreateExprArg_0_0_0(b, l + 1);
    r = r && X_Name(b, l + 1);
    r = r && consumeToken(b, X_TKASSIGN);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkDOT)?
  private static boolean X_CreateExprArg_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CreateExprArg_0_0_0")) return false;
    consumeToken(b, X_TKDOT);
    return true;
  }

  /* ********************************************************** */
  // X_CommaSeparated_26
  public static boolean X_CreateExprArgs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CreateExprArgs")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CommaSeparated_26(b, l + 1);
    exit_section_(b, m, X_CREATE_EXPR_ARGS, r);
    return r;
  }

  /* ********************************************************** */
  // X_CreateExpr X_tkSEMI
  public static boolean X_CreateStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_CreateStmt")) return false;
    if (!nextTokenIs(b, X_TKCREATE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CreateExpr(b, l + 1);
    r = r && consumeToken(b, X_TKSEMI);
    exit_section_(b, m, X_CREATE_STMT, r);
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
  // X_tkDELETE X_UpdateTarget X_tkSEMI
  public static boolean X_DeleteStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_DeleteStmt")) return false;
    if (!nextTokenIs(b, X_TKDELETE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKDELETE);
    r = r && X_UpdateTarget(b, l + 1);
    r = r && consumeToken(b, X_TKSEMI);
    exit_section_(b, m, X_DELETE_STMT, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkLBRACK X_tkCOLON X_tkRBRACK
  public static boolean X_EmptyMapLiteralExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EmptyMapLiteralExpr")) return false;
    if (!nextTokenIs(b, X_TKLBRACK)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, X_TKLBRACK, X_TKCOLON, X_TKRBRACK);
    exit_section_(b, m, X_EMPTY_MAP_LITERAL_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkSEMI
  public static boolean X_EmptyStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EmptyStmt")) return false;
    if (!nextTokenIs(b, X_TKSEMI)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKSEMI);
    exit_section_(b, m, X_EMPTY_STMT, r);
    return r;
  }

  /* ********************************************************** */
  // X_CommaSeparated_10
  public static boolean X_EntityAnnotations(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EntityAnnotations")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CommaSeparated_10(b, l + 1);
    exit_section_(b, m, X_ENTITY_ANNOTATIONS, r);
    return r;
  }

  /* ********************************************************** */
  // X_EntityBodyFull
  //    | X_EntityBodyShort
  public static boolean X_EntityBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EntityBody")) return false;
    if (!nextTokenIs(b, "<x entity body>", X_TKLCURL, X_TKSEMI)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_ENTITY_BODY, "<x entity body>");
    r = X_EntityBodyFull(b, l + 1);
    if (!r) r = X_EntityBodyShort(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_tkLCURL (X_RelClause)* X_tkRCURL
  public static boolean X_EntityBodyFull(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EntityBodyFull")) return false;
    if (!nextTokenIs(b, X_TKLCURL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLCURL);
    r = r && X_EntityBodyFull_1(b, l + 1);
    r = r && consumeToken(b, X_TKRCURL);
    exit_section_(b, m, X_ENTITY_BODY_FULL, r);
    return r;
  }

  // (X_RelClause)*
  private static boolean X_EntityBodyFull_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EntityBodyFull_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_EntityBodyFull_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_EntityBodyFull_1", c)) break;
    }
    return true;
  }

  // (X_RelClause)
  private static boolean X_EntityBodyFull_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EntityBodyFull_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_RelClause(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkSEMI
  public static boolean X_EntityBodyShort(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EntityBodyShort")) return false;
    if (!nextTokenIs(b, X_TKSEMI)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKSEMI);
    exit_section_(b, m, X_ENTITY_BODY_SHORT, r);
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
  // X_tkENUM X_Name X_CommaSeparated_12
  public static boolean X_EnumDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EnumDef")) return false;
    if (!nextTokenIs(b, X_TKENUM)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKENUM);
    r = r && X_Name(b, l + 1);
    r = r && X_CommaSeparated_12(b, l + 1);
    exit_section_(b, m, X_ENUM_DEF, r);
    return r;
  }

  /* ********************************************************** */
  // X_NameNode
  public static boolean X_EnumValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_EnumValue")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_NameNode(b, l + 1);
    exit_section_(b, m, X_ENUM_VALUE, r);
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
  // X_tkFOR X_tkLPAR X_VarDeclarator X_tkIN X_Expression X_tkRPAR X_StatementRef
  public static boolean X_ForStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ForStmt")) return false;
    if (!nextTokenIs(b, X_TKFOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, X_TKFOR, X_TKLPAR);
    r = r && X_VarDeclarator(b, l + 1);
    r = r && consumeToken(b, X_TKIN);
    r = r && X_Expression(b, l + 1);
    r = r && consumeToken(b, X_TKRPAR);
    r = r && X_StatementRef(b, l + 1);
    exit_section_(b, m, X_FOR_STMT, r);
    return r;
  }

  /* ********************************************************** */
  // X_Modifiers X_AttrHeader (X_tkASSIGN X_Expression)?
  public static boolean X_FormalParameter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FormalParameter")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_FORMAL_PARAMETER, "<x formal parameter>");
    r = X_Modifiers(b, l + 1);
    r = r && X_AttrHeader(b, l + 1);
    r = r && X_FormalParameter_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkASSIGN X_Expression)?
  private static boolean X_FormalParameter_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FormalParameter_2")) return false;
    X_FormalParameter_2_0(b, l + 1);
    return true;
  }

  // X_tkASSIGN X_Expression
  private static boolean X_FormalParameter_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FormalParameter_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKASSIGN);
    r = r && X_Expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_CommaSeparated_36
  public static boolean X_FormalParameters(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FormalParameters")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CommaSeparated_36(b, l + 1);
    exit_section_(b, m, X_FORMAL_PARAMETERS, r);
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
    if (!nextTokenIs(b, X_TKLCURL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_BlockStmt(b, l + 1);
    exit_section_(b, m, X_FUNCTION_BODY_FULL, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkSEMI
  public static boolean X_FunctionBodyNone(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FunctionBodyNone")) return false;
    if (!nextTokenIs(b, X_TKSEMI)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKSEMI);
    exit_section_(b, m, X_FUNCTION_BODY_NONE, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkASSIGN X_Expression X_tkSEMI
  public static boolean X_FunctionBodyShort(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FunctionBodyShort")) return false;
    if (!nextTokenIs(b, X_TKASSIGN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKASSIGN);
    r = r && X_Expression(b, l + 1);
    r = r && consumeToken(b, X_TKSEMI);
    exit_section_(b, m, X_FUNCTION_BODY_SHORT, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkFUNCTION (X_QualifiedName)? X_FormalParameters (X_tkCOLON X_Type)? X_FunctionBody
  public static boolean X_FunctionDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FunctionDef")) return false;
    if (!nextTokenIs(b, X_TKFUNCTION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKFUNCTION);
    r = r && X_FunctionDef_1(b, l + 1);
    r = r && X_FormalParameters(b, l + 1);
    r = r && X_FunctionDef_3(b, l + 1);
    r = r && X_FunctionBody(b, l + 1);
    exit_section_(b, m, X_FUNCTION_DEF, r);
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

  // (X_tkCOLON X_Type)?
  private static boolean X_FunctionDef_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FunctionDef_3")) return false;
    X_FunctionDef_3_0(b, l + 1);
    return true;
  }

  // X_tkCOLON X_Type
  private static boolean X_FunctionDef_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FunctionDef_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKCOLON);
    r = r && X_Type(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_CommaSeparated_5 X_tkArrow X_TypeRef
  public static boolean X_FunctionType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_FunctionType")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CommaSeparated_5(b, l + 1);
    r = r && consumeToken(b, X_TKARROW);
    r = r && X_TypeRef(b, l + 1);
    exit_section_(b, m, X_FUNCTION_TYPE, r);
    return r;
  }

  /* ********************************************************** */
  // X_QualifiedName X_CommaSeparated_3
  public static boolean X_GenericType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_GenericType")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_QualifiedName(b, l + 1);
    r = r && X_CommaSeparated_3(b, l + 1);
    exit_section_(b, m, X_GENERIC_TYPE, r);
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
    if (!nextTokenIs(b, X_TKGUARD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKGUARD);
    r = r && X_BlockStmt(b, l + 1);
    exit_section_(b, m, X_GUARD_STMT, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkIF X_tkLPAR X_ExpressionRef X_tkRPAR X_ExpressionRef X_tkElse X_ExpressionRef
  public static boolean X_IfExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_IfExpr")) return false;
    if (!nextTokenIs(b, X_TKIF)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, X_TKIF, X_TKLPAR);
    r = r && X_ExpressionRef(b, l + 1);
    r = r && consumeToken(b, X_TKRPAR);
    r = r && X_ExpressionRef(b, l + 1);
    r = r && consumeToken(b, X_TKELSE);
    r = r && X_ExpressionRef(b, l + 1);
    exit_section_(b, m, X_IF_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkIF X_tkLPAR X_Expression X_tkRPAR X_StatementRef (X_tkElse X_StatementRef)?
  public static boolean X_IfStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_IfStmt")) return false;
    if (!nextTokenIs(b, X_TKIF)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, X_TKIF, X_TKLPAR);
    r = r && X_Expression(b, l + 1);
    r = r && consumeToken(b, X_TKRPAR);
    r = r && X_StatementRef(b, l + 1);
    r = r && X_IfStmt_5(b, l + 1);
    exit_section_(b, m, X_IF_STMT, r);
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
    r = consumeToken(b, X_TKELSE);
    r = r && X_StatementRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkIMPORT (X_Name X_tkCOLON)? X_ImportModule (X_ImportTarget)? X_tkSEMI
  public static boolean X_ImportDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportDef")) return false;
    if (!nextTokenIs(b, X_TKIMPORT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKIMPORT);
    r = r && X_ImportDef_1(b, l + 1);
    r = r && X_ImportModule(b, l + 1);
    r = r && X_ImportDef_3(b, l + 1);
    r = r && consumeToken(b, X_TKSEMI);
    exit_section_(b, m, X_IMPORT_DEF, r);
    return r;
  }

  // (X_Name X_tkCOLON)?
  private static boolean X_ImportDef_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportDef_1")) return false;
    X_ImportDef_1_0(b, l + 1);
    return true;
  }

  // X_Name X_tkCOLON
  private static boolean X_ImportDef_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportDef_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_Name(b, l + 1);
    r = r && consumeToken(b, X_TKCOLON);
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
  // X_tkDOT (X_ImportTargetExact | X_ImportTargetWildcard)
  public static boolean X_ImportTarget(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportTarget")) return false;
    if (!nextTokenIs(b, X_TKDOT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKDOT);
    r = r && X_ImportTarget_1(b, l + 1);
    exit_section_(b, m, X_IMPORT_TARGET, r);
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
  // X_CommaSeparated_38
  public static boolean X_ImportTargetExact(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportTargetExact")) return false;
    if (!nextTokenIs(b, X_TKLCURL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CommaSeparated_38(b, l + 1);
    exit_section_(b, m, X_IMPORT_TARGET_EXACT, r);
    return r;
  }

  /* ********************************************************** */
  // (X_NameNode X_tkCOLON)? X_QualifiedNameNode (X_tkDOT X_tkMUL)?
  public static boolean X_ImportTargetExactItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportTargetExactItem")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_ImportTargetExactItem_0(b, l + 1);
    r = r && X_QualifiedNameNode(b, l + 1);
    r = r && X_ImportTargetExactItem_2(b, l + 1);
    exit_section_(b, m, X_IMPORT_TARGET_EXACT_ITEM, r);
    return r;
  }

  // (X_NameNode X_tkCOLON)?
  private static boolean X_ImportTargetExactItem_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportTargetExactItem_0")) return false;
    X_ImportTargetExactItem_0_0(b, l + 1);
    return true;
  }

  // X_NameNode X_tkCOLON
  private static boolean X_ImportTargetExactItem_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportTargetExactItem_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_NameNode(b, l + 1);
    r = r && consumeToken(b, X_TKCOLON);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkDOT X_tkMUL)?
  private static boolean X_ImportTargetExactItem_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportTargetExactItem_2")) return false;
    X_ImportTargetExactItem_2_0(b, l + 1);
    return true;
  }

  // X_tkDOT X_tkMUL
  private static boolean X_ImportTargetExactItem_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportTargetExactItem_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, X_TKDOT, X_TKMUL);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkMUL
  public static boolean X_ImportTargetWildcard(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ImportTargetWildcard")) return false;
    if (!nextTokenIs(b, X_TKMUL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKMUL);
    exit_section_(b, m, X_IMPORT_TARGET_WILDCARD, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkINCLUDE X_tkSTRING X_tkSEMI
  public static boolean X_IncludeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_IncludeDef")) return false;
    if (!nextTokenIs(b, X_TKINCLUDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKINCLUDE);
    r = r && X_tkSTRING(b, l + 1);
    r = r && consumeToken(b, X_TKSEMI);
    exit_section_(b, m, X_INCLUDE_DEF, r);
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
  // X_IncrementOperator X_BaseExpr X_tkSEMI
  public static boolean X_IncrementStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_IncrementStmt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_INCREMENT_STMT, "<x increment stmt>");
    r = X_IncrementOperator(b, l + 1);
    r = r && X_BaseExpr(b, l + 1);
    r = r && consumeToken(b, X_TKSEMI);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // NUMBER
  public static boolean X_IntExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_IntExpr")) return false;
    if (!nextTokenIs(b, "<x int expr>", DECNUM, HEXDIGNUM)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_INT_EXPR, "<x int expr>");
    r = NUMBER(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_KeyIndexKind X_CommaSeparated_8 X_tkSEMI
  public static boolean X_KeyIndexClause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_KeyIndexClause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_KEY_INDEX_CLAUSE, "<x key index clause>");
    r = X_KeyIndexKind(b, l + 1);
    r = r && X_CommaSeparated_8(b, l + 1);
    r = r && consumeToken(b, X_TKSEMI);
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
  // X_KeywordModifier0
  public static boolean X_KeywordModifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_KeywordModifier")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_KEYWORD_MODIFIER, "<x keyword modifier>");
    r = X_KeywordModifier0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'abstract'
  //    | X_tkMUTABLE
  //    | 'override'
  public static boolean X_KeywordModifier0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_KeywordModifier0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_KEYWORD_MODIFIER_0, "<x keyword modifier 0>");
    r = consumeToken(b, "abstract");
    if (!r) r = consumeToken(b, X_TKMUTABLE);
    if (!r) r = consumeToken(b, "override");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_CommaSeparated_22
  public static boolean X_ListLiteralExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ListLiteralExpr")) return false;
    if (!nextTokenIs(b, X_TKLBRACK)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CommaSeparated_22(b, l + 1);
    exit_section_(b, m, X_LIST_LITERAL_EXPR, r);
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
    if (!r) r = consumeToken(b, X_NULLLITERALEXPR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_ExpressionRef X_tkCOLON X_ExpressionRef
  public static boolean X_MapLiteralExprEntry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_MapLiteralExprEntry")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_MAP_LITERAL_EXPR_ENTRY, "<x map literal expr entry>");
    r = X_ExpressionRef(b, l + 1);
    r = r && consumeToken(b, X_TKCOLON);
    r = r && X_ExpressionRef(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_MirrorStructType0
  public static boolean X_MirrorStructExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_MirrorStructExpr")) return false;
    if (!nextTokenIs(b, X_TKSTRUCT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_MirrorStructType0(b, l + 1);
    exit_section_(b, m, X_MIRROR_STRUCT_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // X_MirrorStructType0
  public static boolean X_MirrorStructType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_MirrorStructType")) return false;
    if (!nextTokenIs(b, X_TKSTRUCT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_MirrorStructType0(b, l + 1);
    exit_section_(b, m, X_MIRROR_STRUCT_TYPE, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkSTRUCT X_tkLT (X_tkMUTABLE)? X_TypeRef X_tkGT
  public static boolean X_MirrorStructType0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_MirrorStructType0")) return false;
    if (!nextTokenIs(b, X_TKSTRUCT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, X_TKSTRUCT, X_TKLT);
    r = r && X_MirrorStructType0_2(b, l + 1);
    r = r && X_TypeRef(b, l + 1);
    r = r && consumeToken(b, X_TKGT);
    exit_section_(b, m, X_MIRROR_STRUCT_TYPE_0, r);
    return r;
  }

  // (X_tkMUTABLE)?
  private static boolean X_MirrorStructType0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_MirrorStructType0_2")) return false;
    consumeToken(b, X_TKMUTABLE);
    return true;
  }

  /* ********************************************************** */
  // X_KeywordModifier
  //    | X_Annotation
  public static boolean X_Modifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_Modifier")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_MODIFIER, "<x modifier>");
    r = X_KeywordModifier(b, l + 1);
    if (!r) r = X_Annotation(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (X_Modifier)*
  public static boolean X_Modifiers(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_Modifiers")) return false;
    Marker m = enter_section_(b, l, _NONE_, X_MODIFIERS, "<x modifiers>");
    while (true) {
      int c = current_position_(b);
      if (!X_Modifiers_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_Modifiers", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // (X_Modifier)
  private static boolean X_Modifiers_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_Modifiers_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_Modifier(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_Modifiers X_tkMODULE X_tkSEMI
  public static boolean X_ModuleHeader(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ModuleHeader")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_MODULE_HEADER, "<x module header>");
    r = X_Modifiers(b, l + 1);
    r = r && consumeTokens(b, 0, X_TKMODULE, X_TKSEMI);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_NameNode
  public static boolean X_Name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_Name")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_NameNode(b, l + 1);
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
  // ID
  public static boolean X_NameNode(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_NameNode")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID);
    exit_section_(b, m, X_NAME_NODE, r);
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
  // X_NameNode X_tkCOLON X_Type
  public static boolean X_NameTypeAttrHeader(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_NameTypeAttrHeader")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_NameNode(b, l + 1);
    r = r && consumeToken(b, X_TKCOLON);
    r = r && X_Type(b, l + 1);
    exit_section_(b, m, X_NAME_TYPE_ATTR_HEADER, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkNAMESPACE (X_QualifiedName)? X_tkLCURL (X_AnnotatedDef)* X_tkRCURL
  public static boolean X_NamespaceDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_NamespaceDef")) return false;
    if (!nextTokenIs(b, X_TKNAMESPACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKNAMESPACE);
    r = r && X_NamespaceDef_1(b, l + 1);
    r = r && consumeToken(b, X_TKLCURL);
    r = r && X_NamespaceDef_3(b, l + 1);
    r = r && consumeToken(b, X_TKRCURL);
    exit_section_(b, m, X_NAMESPACE_DEF, r);
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
  // X_CommaSeparated_24
  public static boolean X_NonEmptyMapLiteralExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_NonEmptyMapLiteralExpr")) return false;
    if (!nextTokenIs(b, X_TKLBRACK)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CommaSeparated_24(b, l + 1);
    exit_section_(b, m, X_NON_EMPTY_MAP_LITERAL_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkOBJECT X_Name X_tkLCURL (X_AttributeClause)* X_tkRCURL
  public static boolean X_ObjectDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ObjectDef")) return false;
    if (!nextTokenIs(b, X_TKOBJECT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKOBJECT);
    r = r && X_Name(b, l + 1);
    r = r && consumeToken(b, X_TKLCURL);
    r = r && X_ObjectDef_3(b, l + 1);
    r = r && consumeToken(b, X_TKRCURL);
    exit_section_(b, m, X_OBJECT_DEF, r);
    return r;
  }

  // (X_AttributeClause)*
  private static boolean X_ObjectDef_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ObjectDef_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_ObjectDef_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_ObjectDef_3", c)) break;
    }
    return true;
  }

  // (X_AttributeClause)
  private static boolean X_ObjectDef_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ObjectDef_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_AttributeClause(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkOPERATION X_Name X_FormalParameters X_BlockStmt
  public static boolean X_OpDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_OpDef")) return false;
    if (!nextTokenIs(b, X_TKOPERATION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKOPERATION);
    r = r && X_Name(b, l + 1);
    r = r && X_FormalParameters(b, l + 1);
    r = r && X_BlockStmt(b, l + 1);
    exit_section_(b, m, X_OP_DEF, r);
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
  // X_QualifiedNameNode
  public static boolean X_QualifiedName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_QualifiedName")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_QualifiedNameNode(b, l + 1);
    exit_section_(b, m, X_QUALIFIED_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // X_NameNode (X_tkDOT X_NameNode)*
  public static boolean X_QualifiedNameNode(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_QualifiedNameNode")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_NameNode(b, l + 1);
    r = r && X_QualifiedNameNode_1(b, l + 1);
    exit_section_(b, m, X_QUALIFIED_NAME_NODE, r);
    return r;
  }

  // (X_tkDOT X_NameNode)*
  private static boolean X_QualifiedNameNode_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_QualifiedNameNode_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_QualifiedNameNode_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_QualifiedNameNode_1", c)) break;
    }
    return true;
  }

  // X_tkDOT X_NameNode
  private static boolean X_QualifiedNameNode_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_QualifiedNameNode_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKDOT);
    r = r && X_NameNode(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_FunctionBodyShort
  //    | X_FunctionBodyFull
  public static boolean X_QueryBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_QueryBody")) return false;
    if (!nextTokenIs(b, "<x query body>", X_TKASSIGN, X_TKLCURL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_QUERY_BODY, "<x query body>");
    r = X_FunctionBodyShort(b, l + 1);
    if (!r) r = X_FunctionBodyFull(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_tkQUERY X_Name X_FormalParameters (X_tkCOLON X_Type)? X_QueryBody
  public static boolean X_QueryDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_QueryDef")) return false;
    if (!nextTokenIs(b, X_TKQUERY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKQUERY);
    r = r && X_Name(b, l + 1);
    r = r && X_FormalParameters(b, l + 1);
    r = r && X_QueryDef_3(b, l + 1);
    r = r && X_QueryBody(b, l + 1);
    exit_section_(b, m, X_QUERY_DEF, r);
    return r;
  }

  // (X_tkCOLON X_Type)?
  private static boolean X_QueryDef_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_QueryDef_3")) return false;
    X_QueryDef_3_0(b, l + 1);
    return true;
  }

  // X_tkCOLON X_Type
  private static boolean X_QueryDef_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_QueryDef_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKCOLON);
    r = r && X_Type(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_AttributeClause
  //    | X_KeyIndexClause
  public static boolean X_RelClause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_RelClause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_REL_CLAUSE, "<x rel clause>");
    r = X_AttributeClause(b, l + 1);
    if (!r) r = X_KeyIndexClause(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_KeyIndexKind X_CommaSeparated_8 X_tkSEMI
  public static boolean X_RelKeyIndexClause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_RelKeyIndexClause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_REL_KEY_INDEX_CLAUSE, "<x rel key index clause>");
    r = X_KeyIndexKind(b, l + 1);
    r = r && X_CommaSeparated_8(b, l + 1);
    r = r && consumeToken(b, X_TKSEMI);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_tkDOT (X_QualifiedName)?
  public static boolean X_RelativeImportModule(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_RelativeImportModule")) return false;
    if (!nextTokenIs(b, X_TKDOT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKDOT);
    r = r && X_RelativeImportModule_1(b, l + 1);
    exit_section_(b, m, X_RELATIVE_IMPORT_MODULE, r);
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
  // X_tkRETURN (X_Expression)? X_tkSEMI
  public static boolean X_ReturnStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_ReturnStmt")) return false;
    if (!nextTokenIs(b, X_TKRETURN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKRETURN);
    r = r && X_ReturnStmt_1(b, l + 1);
    r = r && consumeToken(b, X_TKSEMI);
    exit_section_(b, m, X_RETURN_STMT, r);
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
  // X_StructKeyword X_Name X_tkLCURL (X_AttributeClause)* X_tkRCURL
  public static boolean X_StructDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_StructDef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_STRUCT_DEF, "<x struct def>");
    r = X_StructKeyword(b, l + 1);
    r = r && X_Name(b, l + 1);
    r = r && consumeToken(b, X_TKLCURL);
    r = r && X_StructDef_3(b, l + 1);
    r = r && consumeToken(b, X_TKRCURL);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_AttributeClause)*
  private static boolean X_StructDef_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_StructDef_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_StructDef_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_StructDef_3", c)) break;
    }
    return true;
  }

  // (X_AttributeClause)
  private static boolean X_StructDef_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_StructDef_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_AttributeClause(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkSTRUCT
  //    | 'record'
  public static boolean X_StructKeyword(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_StructKeyword")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_STRUCT_KEYWORD, "<x struct keyword>");
    r = consumeToken(b, X_TKSTRUCT);
    if (!r) r = consumeToken(b, "record");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_CommaSeparated_14
  public static boolean X_TupleExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleExpr")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CommaSeparated_14(b, l + 1);
    exit_section_(b, m, X_TUPLE_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // (X_NameNode X_tkASSIGN)? X_ExpressionRef
  public static boolean X_TupleExprField(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleExprField")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TUPLE_EXPR_FIELD, "<x tuple expr field>");
    r = X_TupleExprField_0(b, l + 1);
    r = r && X_ExpressionRef(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_NameNode X_tkASSIGN)?
  private static boolean X_TupleExprField_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleExprField_0")) return false;
    X_TupleExprField_0_0(b, l + 1);
    return true;
  }

  // X_NameNode X_tkASSIGN
  private static boolean X_TupleExprField_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleExprField_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_NameNode(b, l + 1);
    r = r && consumeToken(b, X_TKASSIGN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_CommaSeparated_1
  public static boolean X_TupleType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleType")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CommaSeparated_1(b, l + 1);
    exit_section_(b, m, X_TUPLE_TYPE, r);
    return r;
  }

  /* ********************************************************** */
  // (X_NameNode X_tkCOLON)? X_TypeRef
  public static boolean X_TupleTypeField(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleTypeField")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_TUPLE_TYPE_FIELD, "<x tuple type field>");
    r = X_TupleTypeField_0(b, l + 1);
    r = r && X_TypeRef(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_NameNode X_tkCOLON)?
  private static boolean X_TupleTypeField_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleTypeField_0")) return false;
    X_TupleTypeField_0_0(b, l + 1);
    return true;
  }

  // X_NameNode X_tkCOLON
  private static boolean X_TupleTypeField_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleTypeField_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_NameNode(b, l + 1);
    r = r && consumeToken(b, X_TKCOLON);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_CommaSeparated_30
  public static boolean X_TupleVarDeclarator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_TupleVarDeclarator")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CommaSeparated_30(b, l + 1);
    exit_section_(b, m, X_TUPLE_VAR_DECLARATOR, r);
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
  // X_tkPLUS
  //    | '-'
  //    | 'not'
  //    | X_IncrementOperator
  public static boolean X_UnaryPrefixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UnaryPrefixOperator")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_UNARY_PREFIX_OPERATOR, "<x unary prefix operator>");
    r = consumeToken(b, X_TKPLUS);
    if (!r) r = consumeToken(b, "-");
    if (!r) r = consumeToken(b, "not");
    if (!r) r = X_IncrementOperator(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (X_tkCARET)+ (X_tkDOT X_QualifiedName)?
  public static boolean X_UpImportModule(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpImportModule")) return false;
    if (!nextTokenIs(b, X_TKCARET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_UpImportModule_0(b, l + 1);
    r = r && X_UpImportModule_1(b, l + 1);
    exit_section_(b, m, X_UP_IMPORT_MODULE, r);
    return r;
  }

  // (X_tkCARET)+
  private static boolean X_UpImportModule_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpImportModule_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKCARET);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, X_TKCARET)) break;
      if (!empty_element_parsed_guard_(b, "X_UpImportModule_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkDOT X_QualifiedName)?
  private static boolean X_UpImportModule_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpImportModule_1")) return false;
    X_UpImportModule_1_0(b, l + 1);
    return true;
  }

  // X_tkDOT X_QualifiedName
  private static boolean X_UpImportModule_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpImportModule_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKDOT);
    r = r && X_QualifiedName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_UpdateFromSingle
  //    | X_UpdateFromMulti
  public static boolean X_UpdateFrom(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpdateFrom")) return false;
    if (!nextTokenIs(b, "<x update from>", ID, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_UPDATE_FROM, "<x update from>");
    r = X_UpdateFromSingle(b, l + 1);
    if (!r) r = X_UpdateFromMulti(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (X_NameNode X_tkCOLON)? X_QualifiedName
  public static boolean X_UpdateFromItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpdateFromItem")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_UpdateFromItem_0(b, l + 1);
    r = r && X_QualifiedName(b, l + 1);
    exit_section_(b, m, X_UPDATE_FROM_ITEM, r);
    return r;
  }

  // (X_NameNode X_tkCOLON)?
  private static boolean X_UpdateFromItem_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpdateFromItem_0")) return false;
    X_UpdateFromItem_0_0(b, l + 1);
    return true;
  }

  // X_NameNode X_tkCOLON
  private static boolean X_UpdateFromItem_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpdateFromItem_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_NameNode(b, l + 1);
    r = r && consumeToken(b, X_TKCOLON);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_CommaSeparated_32
  public static boolean X_UpdateFromMulti(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpdateFromMulti")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CommaSeparated_32(b, l + 1);
    exit_section_(b, m, X_UPDATE_FROM_MULTI, r);
    return r;
  }

  /* ********************************************************** */
  // X_QualifiedName
  public static boolean X_UpdateFromSingle(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpdateFromSingle")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_QualifiedName(b, l + 1);
    exit_section_(b, m, X_UPDATE_FROM_SINGLE, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkUPDATE X_UpdateTarget X_UpdateWhat ';'
  public static boolean X_UpdateStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpdateStmt")) return false;
    if (!nextTokenIs(b, X_TKUPDATE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKUPDATE);
    r = r && X_UpdateTarget(b, l + 1);
    r = r && X_UpdateWhat(b, l + 1);
    r = r && consumeToken(b, X_TKSEMI);
    exit_section_(b, m, X_UPDATE_STMT, r);
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
  // X_UpdateFrom X_AtExprAt X_AtExprWhere
  public static boolean X_UpdateTargetAt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpdateTargetAt")) return false;
    if (!nextTokenIs(b, "<x update target at>", ID, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_UPDATE_TARGET_AT, "<x update target at>");
    r = X_UpdateFrom(b, l + 1);
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
  // X_CommaSeparated_34
  public static boolean X_UpdateWhat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpdateWhat")) return false;
    if (!nextTokenIs(b, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_CommaSeparated_34(b, l + 1);
    exit_section_(b, m, X_UPDATE_WHAT, r);
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
  // (X_tkDOT)? X_Name X_AssignOp
  public static boolean X_UpdateWhatNameOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpdateWhatNameOp")) return false;
    if (!nextTokenIs(b, "<x update what name op>", ID, X_TKDOT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_UPDATE_WHAT_NAME_OP, "<x update what name op>");
    r = X_UpdateWhatNameOp_0(b, l + 1);
    r = r && X_Name(b, l + 1);
    r = r && X_AssignOp(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkDOT)?
  private static boolean X_UpdateWhatNameOp_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_UpdateWhatNameOp_0")) return false;
    consumeToken(b, X_TKDOT);
    return true;
  }

  /* ********************************************************** */
  // X_SimpleVarDeclarator
  //    | X_TupleVarDeclarator
  public static boolean X_VarDeclarator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_VarDeclarator")) return false;
    if (!nextTokenIs(b, "<x var declarator>", ID, X_TKLPAR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_VAR_DECLARATOR, "<x var declarator>");
    r = X_SimpleVarDeclarator(b, l + 1);
    if (!r) r = X_TupleVarDeclarator(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_VarVal X_VarDeclarator (X_tkASSIGN X_Expression)? X_tkSEMI
  public static boolean X_VarStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_VarStmt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_VAR_STMT, "<x var stmt>");
    r = X_VarVal(b, l + 1);
    r = r && X_VarDeclarator(b, l + 1);
    r = r && X_VarStmt_2(b, l + 1);
    r = r && consumeToken(b, X_TKSEMI);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkASSIGN X_Expression)?
  private static boolean X_VarStmt_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_VarStmt_2")) return false;
    X_VarStmt_2_0(b, l + 1);
    return true;
  }

  // X_tkASSIGN X_Expression
  private static boolean X_VarStmt_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_VarStmt_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKASSIGN);
    r = r && X_Expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // X_tkVAL
  //    | 'var'
  public static boolean X_VarVal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_VarVal")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_VAR_VAL, "<x var val>");
    r = consumeToken(b, X_TKVAL);
    if (!r) r = consumeToken(b, "var");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_tkVIRTUAL X_tkLT X_TypeRef X_tkGT
  public static boolean X_VirtualType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_VirtualType")) return false;
    if (!nextTokenIs(b, X_TKVIRTUAL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, X_TKVIRTUAL, X_TKLT);
    r = r && X_TypeRef(b, l + 1);
    r = r && consumeToken(b, X_TKGT);
    exit_section_(b, m, X_VIRTUAL_TYPE, r);
    return r;
  }

  /* ********************************************************** */
  // X_VirtualType
  public static boolean X_VirtualTypeExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_VirtualTypeExpr")) return false;
    if (!nextTokenIs(b, X_TKVIRTUAL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = X_VirtualType(b, l + 1);
    exit_section_(b, m, X_VIRTUAL_TYPE_EXPR, r);
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
  // X_tkElse
  public static boolean X_WhenConditionElse(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenConditionElse")) return false;
    if (!nextTokenIs(b, X_TKELSE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKELSE);
    exit_section_(b, m, X_WHEN_CONDITION_ELSE, r);
    return r;
  }

  /* ********************************************************** */
  // X_ExpressionRef (X_tkCOMMA X_ExpressionRef)* (X_tkCOMMA)?
  public static boolean X_WhenConditionExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenConditionExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_WHEN_CONDITION_EXPR, "<x when condition expr>");
    r = X_ExpressionRef(b, l + 1);
    r = r && X_WhenConditionExpr_1(b, l + 1);
    r = r && X_WhenConditionExpr_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkCOMMA X_ExpressionRef)*
  private static boolean X_WhenConditionExpr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenConditionExpr_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_WhenConditionExpr_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_WhenConditionExpr_1", c)) break;
    }
    return true;
  }

  // X_tkCOMMA X_ExpressionRef
  private static boolean X_WhenConditionExpr_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenConditionExpr_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKCOMMA);
    r = r && X_ExpressionRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkCOMMA)?
  private static boolean X_WhenConditionExpr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenConditionExpr_2")) return false;
    consumeToken(b, X_TKCOMMA);
    return true;
  }

  /* ********************************************************** */
  // X_tkWHEN (X_tkLPAR X_ExpressionRef X_tkRPAR)? X_tkLCURL X_WhenExprCase (X_tkSEMI X_WhenExprCase)* (X_tkSEMI)? X_tkRCURL
  public static boolean X_WhenExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenExpr")) return false;
    if (!nextTokenIs(b, X_TKWHEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKWHEN);
    r = r && X_WhenExpr_1(b, l + 1);
    r = r && consumeToken(b, X_TKLCURL);
    r = r && X_WhenExprCase(b, l + 1);
    r = r && X_WhenExpr_4(b, l + 1);
    r = r && X_WhenExpr_5(b, l + 1);
    r = r && consumeToken(b, X_TKRCURL);
    exit_section_(b, m, X_WHEN_EXPR, r);
    return r;
  }

  // (X_tkLPAR X_ExpressionRef X_tkRPAR)?
  private static boolean X_WhenExpr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenExpr_1")) return false;
    X_WhenExpr_1_0(b, l + 1);
    return true;
  }

  // X_tkLPAR X_ExpressionRef X_tkRPAR
  private static boolean X_WhenExpr_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenExpr_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLPAR);
    r = r && X_ExpressionRef(b, l + 1);
    r = r && consumeToken(b, X_TKRPAR);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkSEMI X_WhenExprCase)*
  private static boolean X_WhenExpr_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenExpr_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!X_WhenExpr_4_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "X_WhenExpr_4", c)) break;
    }
    return true;
  }

  // X_tkSEMI X_WhenExprCase
  private static boolean X_WhenExpr_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenExpr_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKSEMI);
    r = r && X_WhenExprCase(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (X_tkSEMI)?
  private static boolean X_WhenExpr_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenExpr_5")) return false;
    consumeToken(b, X_TKSEMI);
    return true;
  }

  /* ********************************************************** */
  // X_WhenCondition X_tkArrow X_ExpressionRef
  public static boolean X_WhenExprCase(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenExprCase")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_WHEN_EXPR_CASE, "<x when expr case>");
    r = X_WhenCondition(b, l + 1);
    r = r && consumeToken(b, X_TKARROW);
    r = r && X_ExpressionRef(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // X_tkWHEN (X_tkLPAR X_ExpressionRef X_tkRPAR)? X_tkLCURL (X_WhenStmtCase)* X_tkRCURL
  public static boolean X_WhenStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenStmt")) return false;
    if (!nextTokenIs(b, X_TKWHEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKWHEN);
    r = r && X_WhenStmt_1(b, l + 1);
    r = r && consumeToken(b, X_TKLCURL);
    r = r && X_WhenStmt_3(b, l + 1);
    r = r && consumeToken(b, X_TKRCURL);
    exit_section_(b, m, X_WHEN_STMT, r);
    return r;
  }

  // (X_tkLPAR X_ExpressionRef X_tkRPAR)?
  private static boolean X_WhenStmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenStmt_1")) return false;
    X_WhenStmt_1_0(b, l + 1);
    return true;
  }

  // X_tkLPAR X_ExpressionRef X_tkRPAR
  private static boolean X_WhenStmt_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenStmt_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, X_TKLPAR);
    r = r && X_ExpressionRef(b, l + 1);
    r = r && consumeToken(b, X_TKRPAR);
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
  // X_WhenCondition X_tkArrow X_StatementRef (X_tkSEMI)?
  public static boolean X_WhenStmtCase(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenStmtCase")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, X_WHEN_STMT_CASE, "<x when stmt case>");
    r = X_WhenCondition(b, l + 1);
    r = r && consumeToken(b, X_TKARROW);
    r = r && X_StatementRef(b, l + 1);
    r = r && X_WhenStmtCase_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (X_tkSEMI)?
  private static boolean X_WhenStmtCase_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhenStmtCase_3")) return false;
    consumeToken(b, X_TKSEMI);
    return true;
  }

  /* ********************************************************** */
  // X_tkWHILE X_tkLPAR X_Expression X_tkRPAR X_StatementRef
  public static boolean X_WhileStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "X_WhileStmt")) return false;
    if (!nextTokenIs(b, X_TKWHILE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, X_TKWHILE, X_TKLPAR);
    r = r && X_Expression(b, l + 1);
    r = r && consumeToken(b, X_TKRPAR);
    r = r && X_StatementRef(b, l + 1);
    exit_section_(b, m, X_WHILE_STMT, r);
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

}
