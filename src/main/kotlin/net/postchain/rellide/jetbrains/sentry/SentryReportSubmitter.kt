package net.postchain.rellide.jetbrains.sentry

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.diagnostic.ErrorReportSubmitter
import com.intellij.openapi.diagnostic.IdeaLoggingEvent
import com.intellij.openapi.diagnostic.SubmittedReportInfo
import com.intellij.openapi.diagnostic.SubmittedReportInfo.SubmissionStatus.NEW_ISSUE
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.util.SystemInfo
import com.intellij.util.Consumer
import io.sentry.Scope
import io.sentry.Scopes
import io.sentry.SentryClient
import io.sentry.SentryEvent
import io.sentry.SentryLevel
import io.sentry.SentryOptions
import io.sentry.protocol.Message
import java.awt.Component


class SentryReportSubmitter : ErrorReportSubmitter() {

    private val rellRelevantPackages = setOf(
        "net.postchain.rellide.jetbrains",
        "com.redhat.devtools.lsp4ij"
    )

    private val pluginVersion = PluginManagerCore.getPlugin(PluginId.getId("net.postchain.rellide.jetbrains"))?.version ?: "unknown"

    private val scopes: Scopes by lazy {
        val options = SentryOptions().apply {
            dsn = "https://428edffffc07a615e459992d991f5640@o4508080756162560.ingest.de.sentry.io/4508210896765009"
            tracesSampleRate = 1.0
            isDebug = false
            environment = "production"
            release = "rell-jetbrains@$pluginVersion"
            setBeforeSend { event, _ ->
                val throwable = event.throwable
                if (throwable != null && !throwable.isFromRellPlugin()) {
                    return@setBeforeSend null
                }
                event
            }
        }

        val globalScope = Scope(options)
        val isolationScope = Scope(options)
        val scope = Scope(options)

        globalScope.bindClient(SentryClient(options))

        Scopes(scope, isolationScope, globalScope, "RellPlugin.init")
    }

    override fun getReportActionText() = "Report error to Rell plugin maintainers"

    override fun submit(
            events: Array<out IdeaLoggingEvent>,
            additionalInfo: String?,
            parentComponent: Component,
            consumer: Consumer<in SubmittedReportInfo>
    ): Boolean {
        if (events.isEmpty()) {
            return true
        }

        val rellRelatedEvents = events.filter { event ->
            isRellPluginRelated(event)
        }

        if (rellRelatedEvents.isEmpty()) {
            consumer.consume(SubmittedReportInfo(null, "Error not related to Rell plugin", NEW_ISSUE))
            return true
        }

        for (event in rellRelatedEvents) {
            val exception = event.throwable
            val sentryEvent = SentryEvent(exception).apply {
                message = Message().apply {  message = event.message }
                level = SentryLevel.ERROR
                release = pluginVersion
                setTag("build", ApplicationInfo.getInstance().build.asString())
                setTag("plugin_version", pluginVersion)
                setTag("os", SystemInfo.OS_NAME)
                setTag("os_version", SystemInfo.OS_VERSION)
                setTag("os_arch", SystemInfo.OS_ARCH)
                setTag("java_version", SystemInfo.JAVA_VERSION)
                setTag("java_runtime_version", SystemInfo.JAVA_RUNTIME_VERSION)
                setTag("java_vendor", SystemInfo.JAVA_VENDOR)
                additionalInfo?.let {
                    setExtra("additional_info", additionalInfo)
                }
            }
            scopes.captureEvent(sentryEvent)
        }

        consumer.consume(SubmittedReportInfo(null, "Error has been successfully reported", NEW_ISSUE))
        return true
    }

    private fun isRellPluginRelated(event: IdeaLoggingEvent): Boolean =
            event.throwable?.isFromRellPlugin() == true || event.isRellReportingEvent()

    private fun IdeaLoggingEvent.isRellReportingEvent(): Boolean =
            this.plugin?.pluginId?.idString in rellRelevantPackages

    private fun Throwable?.isFromRellPlugin(): Boolean = this
                    ?.stackTrace
                    ?.asSequence()
                    ?.any { it.className.startsWithAny(rellRelevantPackages) }
                    ?: false

    private fun String.startsWithAny(prefixes: Set<String>): Boolean =
            prefixes.any { startsWith(it) }
}
