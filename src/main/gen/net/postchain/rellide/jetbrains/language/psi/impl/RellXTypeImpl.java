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

public class RellXTypeImpl extends ASTWrapperPsiElement implements RellXType {

  public RellXTypeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RellVisitor visitor) {
    visitor.visitXType(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RellVisitor) accept((RellVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RellXBasicType getXBasicType() {
    return findChildByClass(RellXBasicType.class);
  }

  @Override
  @Nullable
  public RellXComplexNullableType getXComplexNullableType() {
    return findChildByClass(RellXComplexNullableType.class);
  }

  @Override
  @Nullable
  public RellXFunctionType getXFunctionType() {
    return findChildByClass(RellXFunctionType.class);
  }

}
