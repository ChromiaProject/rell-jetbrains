package net.postchain.rellide.jetbrains.lsp

import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspClient
import com.intellij.platform.lsp.api.customization.*
import com.intellij.psi.PsiFile
import com.intellij.util.concurrency.AppExecutorUtil
import net.postchain.rellide.jetbrains.colors.RellColor
import org.eclipse.lsp4j.CodeAction
import org.eclipse.lsp4j.CodeActionKind
import org.eclipse.lsp4j.Command
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.SemanticTokenTypes
import org.eclipse.lsp4j.TextEdit
import java.util.concurrent.TimeUnit

object RellLspCustomization : LspCustomization() {
    override val semanticTokensCustomizer: LspSemanticTokensCustomizer = RellSemanticTokensSupport

    override val codeActionsCustomizer: LspCodeActionsCustomizer = RellCodeActionsSupport

    override val commandsCustomizer: LspCommandsCustomizer = RellCommandsSupport

    override val formattingCustomizer: LspFormattingCustomizer = RellFormattingSupport

    // The Rell language server never provides document colors, so keep the feature permanently
    // disabled instead of asking on every highlighting pass.
    override val documentColorCustomizer: LspDocumentColorCustomizer = LspDocumentColorDisabled
}


/**
 * The default support only lets the server format a file when it *dynamically* registered
 * `textDocument/formatting` for it; the Rell server announces the capability statically in its
 * `initialize` reply instead, so the platform would fall back to the core formatter — and with no
 * `lang.formatter` for Rell, Reformat Code ends up disabled (hidden in context menus). The server
 * is the only formatter Rell has, so let it format every file this client already claims.
 */
object RellFormattingSupport : LspFormattingSupport() {
    override fun shouldFormatThisFileExclusivelyByServer(
        file: VirtualFile,
        ideCanFormatThisFileItself: Boolean,
        serverExplicitlyWantsToFormatThisFile: Boolean,
    ): Boolean = true
}


/**
 * `rell.disableRule` makes the server write `.rell_lint` on disk behind the VFS's back, so the
 * created or updated config would surface in the project view and editor only on the next
 * external-change sync. Refresh the file's ancestor directories shortly after sending the command
 * (it is fire-and-forget, so the write is awaited by delay, not by response) to make it appear
 * promptly.
 */
object RellCommandsSupport : LspCommandsSupport() {

    private const val DISABLE_RULE_COMMAND = "rell.disableRule"

    override fun executeCommand(lspClient: LspClient, contextFile: VirtualFile, command: Command) {
        super.executeCommand(lspClient, contextFile, command)
        if (command.command != DISABLE_RULE_COMMAND) return

        // The server puts the config at its workspace root or up to two parents — all of them
        // ancestors of the file the action was invoked in.
        val ancestors = generateSequence(contextFile.parent) { it.parent }.toList()
        val scheduler = AppExecutorUtil.getAppScheduledExecutorService()
        for (delayMillis in longArrayOf(500, 3000)) {
            scheduler.schedule(
                { LocalFileSystem.getInstance().refreshFiles(ancestors, true, false, null) },
                delayMillis,
                TimeUnit.MILLISECONDS,
            )
        }
    }
}


/**
 * Rell language servers up to and including 0.16.2 ignore `CodeActionContext.only` and answer a
 * quickfix-only request with their `source` actions too. The platform trusts the server and would
 * attach those to the diagnostic as quick fixes, listing "Disable linter for this line" twice in
 * the popup (once as a quick fix, once as a caret intention). Dropping non-quickfix kinds from the
 * quick-fix channel restores the intended partition for every server version, including the
 * already-published runtimes downloaded for older Rell versions.
 */
object RellCodeActionsSupport : LspCodeActionsSupport() {
    override fun createQuickFix(lspClient: LspClient, codeAction: CodeAction): LspIntentionAction? =
        if (codeAction.kind?.startsWith(CodeActionKind.QuickFix) == true) {
            RellLspIntentionAction(lspClient, codeAction)
        } else {
            null
        }

    override fun createIntentionAction(lspClient: LspClient, codeAction: CodeAction): LspIntentionAction =
        RellLspIntentionAction(lspClient, codeAction)
}


/**
 * [LspIntentionAction] whose preview never writes to a [Document]. The platform's
 * [LspIntentionAction.generatePreview] applies the workspace edit to the non-physical preview
 * document; that document change still reaches application-wide document listeners such as
 * BackgroundHighlighter, whose alarm cancellation dispatches to the EDT — forbidden inside the
 * preview's side-effect guard (`SideEffectNotAllowedException: INVOKE_LATER`, seen on 2026.1.4),
 * killing the preview for every action that carries an inline edit. Diffing plain strings computes
 * the same preview without a document change. Actions without an inline single-file edit (e.g. the
 * lazily resolved fix-all) keep the platform behavior: their preview resolves the action first,
 * which string diffing cannot reproduce.
 */
private class RellLspIntentionAction(
    lspClient: LspClient,
    private val codeAction: CodeAction,
) : LspIntentionAction(lspClient, codeAction) {
    override fun generatePreview(
        project: Project,
        editor: Editor,
        nonPhysicalPsiFile: PsiFile,
    ): IntentionPreviewInfo {
        val edits = codeAction.edit?.changes?.values?.singleOrNull()
            ?: return super.generatePreview(project, editor, nonPhysicalPsiFile)

        val document = nonPhysicalPsiFile.viewProvider.document
            ?: return super.generatePreview(project, editor, nonPhysicalPsiFile)

        val originalText = document.text

        return IntentionPreviewInfo.CustomDiff(
            nonPhysicalPsiFile.fileType,
            nonPhysicalPsiFile.name,
            originalText,
            applyEdits(originalText, document, edits),
        )
    }

    /** Offsets are resolved against the original text, so edits apply back to front. */
    private fun applyEdits(text: String, document: Document, edits: List<TextEdit>): String {
        val backToFront = edits.sortedWith(
            compareByDescending<TextEdit> { it.range.start.line }.thenByDescending { it.range.start.character }
        )
        var result = text
        for (edit in backToFront) {
            result = result.take(offsetOf(document, edit.range.start)) +
                edit.newText +
                result.substring(offsetOf(document, edit.range.end))
        }
        return result
    }

    private fun offsetOf(document: Document, position: Position): Int =
        if (position.line >= document.lineCount) {
            document.textLength
        } else {
            (document.getLineStartOffset(position.line) + position.character)
                .coerceAtMost(document.getLineEndOffset(position.line))
        }
}


enum class RellTokenModifier(val modifierStringId: String) {
    GLOBAL_CONSTANT("rell-global_constant"),
    LOCAL_VAL("rell-local_val"),
    LOCAL_VAR("rell-local_var"),
    NAMED_ARGUMENT("rell-named_argument"),
    ENTITY("rell-entity"),
    OBJECT("rell-object"),
    QUERY("rell-query"),
    OPERATION("rell-operation"),
    PARAMETER("rell-parameter"),
    CALL("rell-call"),
}


object RellSemanticTokensSupport : LspSemanticTokensSupport() {
    override val tokenModifiers: List<String>
        get() = super.tokenModifiers + RellTokenModifier.entries.map { it.modifierStringId }

    // The default implementation asks for semantic tokens only in plain-text and TextMate files,
    // assuming any other language brings its own highlighting. Rell has a lexer-based highlighter
    // for keywords, literals and comments only — everything identifier-shaped (declaration names,
    // annotations, constants) is coloured from the server's tokens, so they must be requested.
    override fun shouldAskServerForSemanticTokens(psiFile: PsiFile): Boolean = true

    override fun getTextAttributesKey(
        tokenType: String,
        modifiers: List<String>,
    ): TextAttributesKey? {
        val textAttributes = when (tokenType) {
            SemanticTokenTypes.Keyword -> RellColor.KEYWORD.textAttributesKey
            SemanticTokenTypes.Decorator -> RellColor.ANNOTATION.textAttributesKey
            SemanticTokenTypes.Namespace -> RellColor.NAMESPACE_NAME.textAttributesKey
            SemanticTokenTypes.Type -> RellColor.TYPE_REFERENCE.textAttributesKey
            SemanticTokenTypes.Enum -> RellColor.ENUM_NAME.textAttributesKey
            SemanticTokenTypes.EnumMember -> RellColor.STATE_VARIABLE.textAttributesKey

            SemanticTokenTypes.Variable -> {
                when {
                    modifiers.contains(RellTokenModifier.GLOBAL_CONSTANT.modifierStringId) ->
                        RellColor.GLOBAL.textAttributesKey

                    modifiers.contains(RellTokenModifier.PARAMETER.modifierStringId) ->
                        RellColor.PARAMETER.textAttributesKey

                    modifiers.contains(RellTokenModifier.LOCAL_VAL.modifierStringId) ->
                        RellColor.CONSTANT.textAttributesKey

                    modifiers.contains(RellTokenModifier.LOCAL_VAR.modifierStringId) ->
                        RellColor.LOCAL_VARIABLE.textAttributesKey

                    modifiers.contains(RellTokenModifier.NAMED_ARGUMENT.modifierStringId) ->
                        RellColor.PARAMETER.textAttributesKey

                    else -> null
                }
            }

            SemanticTokenTypes.Class -> {
                when {
                    modifiers.contains(RellTokenModifier.ENTITY.modifierStringId) ->
                        RellColor.ENTITY_NAME.textAttributesKey

                    modifiers.contains(RellTokenModifier.OBJECT.modifierStringId) ->
                        RellColor.OBJECT_NAME.textAttributesKey

                    else -> null
                }
            }

            SemanticTokenTypes.Property -> RellColor.STATE_VARIABLE.textAttributesKey
            SemanticTokenTypes.Struct -> RellColor.STRUCT_NAME.textAttributesKey

            SemanticTokenTypes.Function -> {
                when {
                    modifiers.contains(RellTokenModifier.CALL.modifierStringId) ->
                        RellColor.FUNCTION_CALL.textAttributesKey

                    modifiers.contains(RellTokenModifier.OPERATION.modifierStringId) ->
                        RellColor.OPERATION_NAME.textAttributesKey

                    modifiers.contains(RellTokenModifier.QUERY.modifierStringId) ->
                        RellColor.QUERY_NAME.textAttributesKey

                    else -> RellColor.FUNCTION_DECLARATION.textAttributesKey
                }
            }

            SemanticTokenTypes.Parameter -> RellColor.PARAMETER.textAttributesKey
            else -> null
        }

        return textAttributes ?: super.getTextAttributesKey(tokenType, modifiers)
    }
}
