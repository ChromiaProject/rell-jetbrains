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

public class RellXGenericTypeExprImpl extends ASTWrapperPsiElement implements RellXGenericTypeExpr {

  public RellXGenericTypeExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RellVisitor visitor) {
    visitor.visitXGenericTypeExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RellVisitor) accept((RellVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RellXBaseExprTailCall getXBaseExprTailCall() {
    return findChildByClass(RellXBaseExprTailCall.class);
  }

  @Override
  @Nullable
  public RellXBaseExprTailMember getXBaseExprTailMember() {
    return findChildByClass(RellXBaseExprTailMember.class);
  }

  @Override
  @NotNull
  public RellXGenericType getXGenericType() {
    return findNotNullChildByClass(RellXGenericType.class);
  }

}
