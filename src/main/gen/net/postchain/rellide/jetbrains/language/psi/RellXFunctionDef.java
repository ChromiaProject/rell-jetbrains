// This is a generated file. Not intended for manual editing.
package net.postchain.rellide.jetbrains.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RellXFunctionDef extends PsiElement {

  @NotNull
  List<RellXFormalParameter> getXFormalParameterList();

  @NotNull
  RellXFunctionBody getXFunctionBody();

  @Nullable
  RellXQualifiedName getXQualifiedName();

  @Nullable
  RellXType getXType();

  @NotNull
  RellXTkFUNCTION getXTkFUNCTION();

}
