// This is a generated file. Not intended for manual editing.
package net.postchain.rellide.jetbrains.language.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static net.postchain.rellide.jetbrains.language.psi.RellTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import net.postchain.rellide.jetbrains.language.psi.*;

public class RellXBaseExprHeadImpl extends ASTWrapperPsiElement implements RellXBaseExprHead {

  public RellXBaseExprHeadImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RellVisitor visitor) {
    visitor.visitXBaseExprHead(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RellVisitor) accept((RellVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RellXAttrExpr getXAttrExpr() {
    return findChildByClass(RellXAttrExpr.class);
  }

  @Override
  @Nullable
  public RellXBigIntExpr getXBigIntExpr() {
    return findChildByClass(RellXBigIntExpr.class);
  }

  @Override
  @Nullable
  public RellXBytesExpr getXBytesExpr() {
    return findChildByClass(RellXBytesExpr.class);
  }

  @Override
  @Nullable
  public RellXCreateExpr getXCreateExpr() {
    return findChildByClass(RellXCreateExpr.class);
  }

  @Override
  @Nullable
  public RellXDecimalExpr getXDecimalExpr() {
    return findChildByClass(RellXDecimalExpr.class);
  }

  @Override
  @Nullable
  public RellXDollarExpr getXDollarExpr() {
    return findChildByClass(RellXDollarExpr.class);
  }

  @Override
  @Nullable
  public RellXEmptyMapLiteralExpr getXEmptyMapLiteralExpr() {
    return findChildByClass(RellXEmptyMapLiteralExpr.class);
  }

  @Override
  @Nullable
  public RellXGenericTypeExpr getXGenericTypeExpr() {
    return findChildByClass(RellXGenericTypeExpr.class);
  }

  @Override
  @Nullable
  public RellXIntExpr getXIntExpr() {
    return findChildByClass(RellXIntExpr.class);
  }

  @Override
  @Nullable
  public RellXListLiteralExpr getXListLiteralExpr() {
    return findChildByClass(RellXListLiteralExpr.class);
  }

  @Override
  @Nullable
  public RellXMirrorStructExpr getXMirrorStructExpr() {
    return findChildByClass(RellXMirrorStructExpr.class);
  }

  @Override
  @Nullable
  public RellXNameExpr getXNameExpr() {
    return findChildByClass(RellXNameExpr.class);
  }

  @Override
  @Nullable
  public RellXNonEmptyMapLiteralExpr getXNonEmptyMapLiteralExpr() {
    return findChildByClass(RellXNonEmptyMapLiteralExpr.class);
  }

  @Override
  @Nullable
  public RellXNullLiteralExpr getXNullLiteralExpr() {
    return findChildByClass(RellXNullLiteralExpr.class);
  }

  @Override
  @Nullable
  public RellXParenthesesExpr getXParenthesesExpr() {
    return findChildByClass(RellXParenthesesExpr.class);
  }

  @Override
  @Nullable
  public RellXStringExpr getXStringExpr() {
    return findChildByClass(RellXStringExpr.class);
  }

  @Override
  @Nullable
  public RellXVirtualTypeExpr getXVirtualTypeExpr() {
    return findChildByClass(RellXVirtualTypeExpr.class);
  }

}
