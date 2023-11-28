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

public class RellXStatementImpl extends ASTWrapperPsiElement implements RellXStatement {

  public RellXStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RellVisitor visitor) {
    visitor.visitXStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RellVisitor) accept((RellVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RellXAssignStmt getXAssignStmt() {
    return findChildByClass(RellXAssignStmt.class);
  }

  @Override
  @Nullable
  public RellXBlockStmt getXBlockStmt() {
    return findChildByClass(RellXBlockStmt.class);
  }

  @Override
  @Nullable
  public RellXBreakStmt getXBreakStmt() {
    return findChildByClass(RellXBreakStmt.class);
  }

  @Override
  @Nullable
  public RellXCallStmt getXCallStmt() {
    return findChildByClass(RellXCallStmt.class);
  }

  @Override
  @Nullable
  public RellXContinueStmt getXContinueStmt() {
    return findChildByClass(RellXContinueStmt.class);
  }

  @Override
  @Nullable
  public RellXCreateStmt getXCreateStmt() {
    return findChildByClass(RellXCreateStmt.class);
  }

  @Override
  @Nullable
  public RellXDeleteStmt getXDeleteStmt() {
    return findChildByClass(RellXDeleteStmt.class);
  }

  @Override
  @Nullable
  public RellXEmptyStmt getXEmptyStmt() {
    return findChildByClass(RellXEmptyStmt.class);
  }

  @Override
  @Nullable
  public RellXForStmt getXForStmt() {
    return findChildByClass(RellXForStmt.class);
  }

  @Override
  @Nullable
  public RellXGuardStmt getXGuardStmt() {
    return findChildByClass(RellXGuardStmt.class);
  }

  @Override
  @Nullable
  public RellXIfStmt getXIfStmt() {
    return findChildByClass(RellXIfStmt.class);
  }

  @Override
  @Nullable
  public RellXIncrementStmt getXIncrementStmt() {
    return findChildByClass(RellXIncrementStmt.class);
  }

  @Override
  @Nullable
  public RellXReturnStmt getXReturnStmt() {
    return findChildByClass(RellXReturnStmt.class);
  }

  @Override
  @Nullable
  public RellXUpdateStmt getXUpdateStmt() {
    return findChildByClass(RellXUpdateStmt.class);
  }

  @Override
  @Nullable
  public RellXVarStmt getXVarStmt() {
    return findChildByClass(RellXVarStmt.class);
  }

  @Override
  @Nullable
  public RellXWhenStmt getXWhenStmt() {
    return findChildByClass(RellXWhenStmt.class);
  }

  @Override
  @Nullable
  public RellXWhileStmt getXWhileStmt() {
    return findChildByClass(RellXWhileStmt.class);
  }

}
