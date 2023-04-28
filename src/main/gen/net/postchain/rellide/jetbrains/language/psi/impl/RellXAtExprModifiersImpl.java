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

public class RellXAtExprModifiersImpl extends ASTWrapperPsiElement implements RellXAtExprModifiers {

  public RellXAtExprModifiersImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RellVisitor visitor) {
    visitor.visitXAtExprModifiers(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RellVisitor) accept((RellVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RellXAtExprLimit getXAtExprLimit() {
    return findChildByClass(RellXAtExprLimit.class);
  }

  @Override
  @Nullable
  public RellXAtExprOffset getXAtExprOffset() {
    return findChildByClass(RellXAtExprOffset.class);
  }

  @Override
  @NotNull
  public RellXExpressionRef getXExpressionRef() {
    return findNotNullChildByClass(RellXExpressionRef.class);
  }

}
