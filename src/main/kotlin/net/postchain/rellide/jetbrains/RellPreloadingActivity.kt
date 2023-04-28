package net.postchain.rellide.jetbrains

import com.intellij.openapi.application.PreloadingActivity
import com.intellij.openapi.progress.ProgressIndicator
import org.wso2.lsp4intellij.IntellijLanguageClient
import org.wso2.lsp4intellij.client.languageserver.serverdefinition.ProcessBuilderServerDefinition
import org.wso2.lsp4intellij.client.languageserver.serverdefinition.RawCommandServerDefinition
import org.wso2.lsp4intellij.requests.Timeouts

const val FIVE_SECONDS = 5_000;
const val TEN_SECONDS = 10_000;
const val TWENTY_SECONDS = 20_000;
const val THIRTY_SECONDS = 30_000;

class RellPreloadingActivity : PreloadingActivity() {
    override fun preload(indicator: ProgressIndicator) {
        setTimeouts();
        val runProcess = ProcessBuilder("java", "-jar", "C:\\Users\\Administrator\\Documents\\GitHub\\Gitlab\\rell-eclipse\\net.postchain.rellide.parent\\net.postchain.rellide.lsp\\target\\net.postchain.rellide.lsp-1.0.0-SNAPSHOT-ls.jar")

        IntellijLanguageClient.addServerDefinition(
            ProcessBuilderServerDefinition(
                "rell",
                runProcess
            )
        )

    }

    private fun setTimeouts() {
        IntellijLanguageClient.setTimeout(Timeouts.INIT, THIRTY_SECONDS);
        IntellijLanguageClient.setTimeout(Timeouts.SHUTDOWN, TEN_SECONDS);
        IntellijLanguageClient.setTimeout(Timeouts.DOC_HIGHLIGHT, FIVE_SECONDS);
        IntellijLanguageClient.setTimeout(Timeouts.CODEACTION, TEN_SECONDS);
        IntellijLanguageClient.setTimeout(Timeouts.EXECUTE_COMMAND, TEN_SECONDS);
        IntellijLanguageClient.setTimeout(Timeouts.COMPLETION, TEN_SECONDS);
        IntellijLanguageClient.setTimeout(Timeouts.FORMATTING, TEN_SECONDS);
        IntellijLanguageClient.setTimeout(Timeouts.HOVER, TEN_SECONDS);
        IntellijLanguageClient.setTimeout(Timeouts.REFERENCES, TWENTY_SECONDS);
        IntellijLanguageClient.setTimeout(Timeouts.WILLSAVE, TEN_SECONDS);
        IntellijLanguageClient.setTimeout(Timeouts.DEFINITION, TWENTY_SECONDS);
        IntellijLanguageClient.setTimeout(Timeouts.SIGNATURE, FIVE_SECONDS);
        IntellijLanguageClient.setTimeout(Timeouts.SYMBOLS, TWENTY_SECONDS);
        IntellijLanguageClient.setTimeout(Timeouts.CODELENS, TEN_SECONDS);
    }
}