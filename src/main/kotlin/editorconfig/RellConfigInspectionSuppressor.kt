package net.postchain.rellide.jetbrains.editorconfig

import com.intellij.codeInspection.InspectionSuppressor
import com.intellij.codeInspection.SuppressQuickFix
import com.intellij.psi.PsiElement

/**
 * `.rell_lint` and `.rell_format` open as EditorConfig files (see rell-withEditorConfig.xml), but
 * their `rule_*` and formatter keys are Rell's, not EditorConfig's, so the EditorConfig plugin's
 * key-correctness inspection would mark every property "not supported".
 */
class RellConfigInspectionSuppressor : InspectionSuppressor {

    override fun isSuppressedFor(element: PsiElement, toolId: String): Boolean =
        toolId == KEY_CORRECTNESS_TOOL_ID && element.containingFile?.name in RELL_CONFIG_FILE_NAMES

    override fun getSuppressActions(element: PsiElement?, toolId: String): Array<SuppressQuickFix> =
        SuppressQuickFix.EMPTY_ARRAY
}

private const val KEY_CORRECTNESS_TOOL_ID = "EditorConfigKeyCorrectness"

internal val RELL_CONFIG_FILE_NAMES = setOf(".rell_lint", ".rell_format")
