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

public class RellXStructDefImpl extends ASTWrapperPsiElement implements RellXStructDef {

  public RellXStructDefImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RellVisitor visitor) {
    visitor.visitXStructDef(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RellVisitor) accept((RellVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<RellXAttributeClause> getXAttributeClauseList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RellXAttributeClause.class);
  }

  @Override
  @NotNull
  public RellXName getXName() {
    return findNotNullChildByClass(RellXName.class);
  }

  @Override
  @NotNull
  public RellXStructKeyword getXStructKeyword() {
    return findNotNullChildByClass(RellXStructKeyword.class);
  }

}
