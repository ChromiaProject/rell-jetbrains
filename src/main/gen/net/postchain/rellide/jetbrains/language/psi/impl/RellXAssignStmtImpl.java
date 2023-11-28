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

public class RellXAssignStmtImpl extends ASTWrapperPsiElement implements RellXAssignStmt {

  public RellXAssignStmtImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RellVisitor visitor) {
    visitor.visitXAssignStmt(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RellVisitor) accept((RellVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public RellXAssignOp getXAssignOp() {
    return findNotNullChildByClass(RellXAssignOp.class);
  }

  @Override
  @NotNull
  public RellXBaseExpr getXBaseExpr() {
    return findNotNullChildByClass(RellXBaseExpr.class);
  }

  @Override
  @NotNull
  public RellXExpression getXExpression() {
    return findNotNullChildByClass(RellXExpression.class);
  }

}
