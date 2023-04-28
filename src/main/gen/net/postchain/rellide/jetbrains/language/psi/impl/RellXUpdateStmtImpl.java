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

public class RellXUpdateStmtImpl extends ASTWrapperPsiElement implements RellXUpdateStmt {

  public RellXUpdateStmtImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RellVisitor visitor) {
    visitor.visitXUpdateStmt(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RellVisitor) accept((RellVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public RellXUpdateTarget getXUpdateTarget() {
    return findNotNullChildByClass(RellXUpdateTarget.class);
  }

  @Override
  @NotNull
  public List<RellXUpdateWhatExpr> getXUpdateWhatExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RellXUpdateWhatExpr.class);
  }

  @Override
  @NotNull
  public RellXTkUPDATE getXTkUPDATE() {
    return findNotNullChildByClass(RellXTkUPDATE.class);
  }

}
