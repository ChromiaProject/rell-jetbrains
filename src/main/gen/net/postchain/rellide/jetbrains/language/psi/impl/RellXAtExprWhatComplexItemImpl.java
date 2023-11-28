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

public class RellXAtExprWhatComplexItemImpl extends ASTWrapperPsiElement implements RellXAtExprWhatComplexItem {

  public RellXAtExprWhatComplexItemImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RellVisitor visitor) {
    visitor.visitXAtExprWhatComplexItem(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RellVisitor) accept((RellVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<RellXAnnotation> getXAnnotationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RellXAnnotation.class);
  }

  @Override
  @Nullable
  public RellXAtExprWhatName getXAtExprWhatName() {
    return findChildByClass(RellXAtExprWhatName.class);
  }

  @Override
  @NotNull
  public RellXExpressionRef getXExpressionRef() {
    return findNotNullChildByClass(RellXExpressionRef.class);
  }

}
