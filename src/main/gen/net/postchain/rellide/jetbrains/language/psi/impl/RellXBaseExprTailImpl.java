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

public class RellXBaseExprTailImpl extends ASTWrapperPsiElement implements RellXBaseExprTail {

  public RellXBaseExprTailImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RellVisitor visitor) {
    visitor.visitXBaseExprTail(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RellVisitor) accept((RellVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RellXBaseExprTailAt getXBaseExprTailAt() {
    return findChildByClass(RellXBaseExprTailAt.class);
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
  @Nullable
  public RellXBaseExprTailSafeMember getXBaseExprTailSafeMember() {
    return findChildByClass(RellXBaseExprTailSafeMember.class);
  }

  @Override
  @Nullable
  public RellXBaseExprTailSubscript getXBaseExprTailSubscript() {
    return findChildByClass(RellXBaseExprTailSubscript.class);
  }

  @Override
  @Nullable
  public RellXBaseExprTailUnaryPostfixOp getXBaseExprTailUnaryPostfixOp() {
    return findChildByClass(RellXBaseExprTailUnaryPostfixOp.class);
  }

}
