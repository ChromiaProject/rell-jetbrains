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

public class RellXAtExprOffsetImpl extends ASTWrapperPsiElement implements RellXAtExprOffset {

  public RellXAtExprOffsetImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RellVisitor visitor) {
    visitor.visitXAtExprOffset(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RellVisitor) accept((RellVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public RellXExpressionRef getXExpressionRef() {
    return findNotNullChildByClass(RellXExpressionRef.class);
  }

}
