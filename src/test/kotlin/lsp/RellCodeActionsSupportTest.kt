package net.postchain.rellide.jetbrains.lsp

import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspClient
import com.intellij.platform.lsp.api.LspClientDescriptor
import com.intellij.platform.lsp.api.LspIntegrationProvider
import com.intellij.platform.lsp.api.LspServer
import com.intellij.platform.lsp.api.LspServerState
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo
import org.eclipse.lsp4j.CodeAction
import org.eclipse.lsp4j.CodeActionKind
import org.eclipse.lsp4j.InitializeResult
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.TextDocumentIdentifier
import org.eclipse.lsp4j.TextEdit
import org.eclipse.lsp4j.WorkspaceEdit
import org.eclipse.lsp4j.services.LanguageServer
import java.util.concurrent.CompletableFuture

/**
 * Pins the code-action channel partition. Quick-fix kinds ride the diagnostic's quick-fix channel;
 * everything else — the server's `source` actions and the body-conversion `refactor.rewrite` pair
 * ("Convert to expression body" / "Convert to block body") — reaches the editor only as a caret
 * intention. The platform already drops `quickfix.*` kinds from the intention channel, and
 * [RellCodeActionsSupport] drops every other kind from the quick-fix channel, so no action can
 * show up twice in the Alt+Enter popup.
 */
class RellCodeActionsSupportTest : BasePlatformTestCase() {

    fun testQuickFixChannelServesQuickFixKinds() {
        assertNotNull(RellCodeActionsSupport.createQuickFix(fakeLspClient, action(CodeActionKind.QuickFix)))
    }

    fun testQuickFixChannelDropsEveryOtherKind() {
        assertNull(RellCodeActionsSupport.createQuickFix(fakeLspClient, action(CodeActionKind.RefactorRewrite)))
        assertNull(RellCodeActionsSupport.createQuickFix(fakeLspClient, action(CodeActionKind.Source)))
        assertNull(RellCodeActionsSupport.createQuickFix(fakeLspClient, action(kind = null)))
    }

    fun testIntentionChannelServesRefactorRewriteKinds() {
        assertNotNull(RellCodeActionsSupport.createIntentionAction(fakeLspClient, action(CodeActionKind.RefactorRewrite)))
    }

    // The platform's preview mutates the preview document, which trips application-wide document
    // listeners inside the preview's side-effect guard (see RellLspIntentionAction). The preview
    // must therefore be a string diff, produced without any document write.
    fun testPreviewIsAStringDiffComputedWithoutDocumentWrites() {
        val psiFile = myFixture.configureByText(
            "main.rell",
            "module;\nfunction f(x: integer): integer {\n    return x + 1;\n}\n",
        )
        val convertToExpressionBody = action(CodeActionKind.RefactorRewrite).also {
            it.edit = WorkspaceEdit(
                mapOf(
                    "file:///main.rell" to listOf(
                        TextEdit(Range(Position(1, 32), Position(3, 1)), "= x + 1;")
                    )
                )
            )
        }
        val intention = RellCodeActionsSupport.createIntentionAction(fakeLspClient, convertToExpressionBody)

        val preview = intention.generatePreview(project, myFixture.editor, psiFile)

        val diff = preview as IntentionPreviewInfo.CustomDiff
        assertEquals("module;\nfunction f(x: integer): integer {\n    return x + 1;\n}\n", diff.originalText())
        assertEquals("module;\nfunction f(x: integer): integer = x + 1;\n", diff.modifiedText())
        // The physical document is untouched — the preview only read it.
        assertEquals("module;\nfunction f(x: integer): integer {\n    return x + 1;\n}\n", psiFile.text)
    }

    private fun action(kind: String?): CodeAction = CodeAction("Convert to expression body").also { it.kind = kind }

    // Statically typed as the current interface so the calls hit the overridden overload — an
    // LspServer-typed argument would resolve to the deprecated LspServer overload and bypass
    // the guard under test.
    private val fakeLspClient: LspClient = FakeLspServer

    /**
     * The channel decision must need nothing from the client; every member trips if that changes.
     * The platform's non-deprecated factory overloads still cast their [LspClient] to the
     * deprecated [LspServer] for delegation, so the fake must implement the old interface.
     */
    @Suppress("DEPRECATION")
    private object FakeLspServer : LspServer {
        override val providerClass: Class<out LspIntegrationProvider> get() = error("unused")
        override val project: Project get() = error("unused")
        override val descriptor: LspClientDescriptor get() = error("unused")
        override val state: LspServerState get() = error("unused")
        // Read by LspIntentionAction's constructor (resolve-support probing); null means "no
        // capabilities known", which is fine for constructing the action.
        override val initializeResult: InitializeResult? get() = null
        override fun sendNotification(lsp4jSender: (LanguageServer) -> Unit) = error("unused")

        override suspend fun <Lsp4jResponse> sendRequest(
            lsp4jSender: (LanguageServer) -> CompletableFuture<Lsp4jResponse>,
        ): Lsp4jResponse? = error("unused")

        override fun <Lsp4jResponse> sendRequestSync(
            timeoutMs: Int,
            lsp4jSender: (LanguageServer) -> CompletableFuture<Lsp4jResponse>,
        ): Lsp4jResponse? = error("unused")

        override fun getDocumentIdentifier(file: VirtualFile): TextDocumentIdentifier = error("unused")
        override fun getDocumentVersion(document: Document): Int = error("unused")
    }
}
