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

public class RellXAnyDefImpl extends ASTWrapperPsiElement implements RellXAnyDef {

  public RellXAnyDefImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RellVisitor visitor) {
    visitor.visitXAnyDef(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RellVisitor) accept((RellVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RellXConstantDef getXConstantDef() {
    return findChildByClass(RellXConstantDef.class);
  }

  @Override
  @Nullable
  public RellXEntityDef getXEntityDef() {
    return findChildByClass(RellXEntityDef.class);
  }

  @Override
  @Nullable
  public RellXEnumDef getXEnumDef() {
    return findChildByClass(RellXEnumDef.class);
  }

  @Override
  @Nullable
  public RellXFunctionDef getXFunctionDef() {
    return findChildByClass(RellXFunctionDef.class);
  }

  @Override
  @Nullable
  public RellXImportDef getXImportDef() {
    return findChildByClass(RellXImportDef.class);
  }

  @Override
  @Nullable
  public RellXIncludeDef getXIncludeDef() {
    return findChildByClass(RellXIncludeDef.class);
  }

  @Override
  @Nullable
  public RellXNamespaceDef getXNamespaceDef() {
    return findChildByClass(RellXNamespaceDef.class);
  }

  @Override
  @Nullable
  public RellXObjectDef getXObjectDef() {
    return findChildByClass(RellXObjectDef.class);
  }

  @Override
  @Nullable
  public RellXOpDef getXOpDef() {
    return findChildByClass(RellXOpDef.class);
  }

  @Override
  @Nullable
  public RellXQueryDef getXQueryDef() {
    return findChildByClass(RellXQueryDef.class);
  }

  @Override
  @Nullable
  public RellXStructDef getXStructDef() {
    return findChildByClass(RellXStructDef.class);
  }

}
