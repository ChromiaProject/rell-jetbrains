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

public class RellXPrimaryTypeImpl extends ASTWrapperPsiElement implements RellXPrimaryType {

  public RellXPrimaryTypeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RellVisitor visitor) {
    visitor.visitXPrimaryType(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RellVisitor) accept((RellVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RellXGenericType getXGenericType() {
    return findChildByClass(RellXGenericType.class);
  }

  @Override
  @Nullable
  public RellXMirrorStructType getXMirrorStructType() {
    return findChildByClass(RellXMirrorStructType.class);
  }

  @Override
  @Nullable
  public RellXNameType getXNameType() {
    return findChildByClass(RellXNameType.class);
  }

  @Override
  @Nullable
  public RellXTupleType getXTupleType() {
    return findChildByClass(RellXTupleType.class);
  }

  @Override
  @Nullable
  public RellXVirtualType getXVirtualType() {
    return findChildByClass(RellXVirtualType.class);
  }

}
