package net.postchain.rellide.jetbrains.settings

import com.intellij.execution.ExecutionException
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.ProjectManager
import net.postchain.rellide.jetbrains.chromia.RellVersion
import net.postchain.rellide.jetbrains.chromia.RellVersionResolver
import java.util.concurrent.atomic.AtomicBoolean

private const val PROBE_TIMEOUT_MS = 30_000

/**
 * Passively keeps the recorded `chr --version` info (see [RellPluginSettingsState]) fresh, so
 * consumers like the Chromia tool window get the CLI's Rell version without the user ever pressing
 * the Test button. When the recorded info does not match the currently effective command, a single
 * background probe runs `chr --version`, records the result, and refreshes the Chromia tool
 * windows via [RellVersionResolver.TOPIC]. A failing command is not retried until it changes, so a
 * broken CLI setting cannot spawn a process on every tree rebuild.
 */
@Service(Service.Level.APP)
class ChrVersionService {
    private val probeInFlight = AtomicBoolean()

    @Volatile
    private var failedCommand: String? = null

    /**
     * The maximal Rell version the effective Chromia CLI reports, or null while unknown — in which
     * case a background probe may be started and callers are notified through
     * [RellVersionResolver.TOPIC] once it delivers.
     */
    fun maxRellVersion(): RellVersion? {
        val state = RellPluginSettingsState.instance
        if (!state.chrVersionInfoIsCurrent()) {
            probeAsync(state)
            return null
        }
        return state.reportedMaxRellVersion()
    }

    private fun probeAsync(state: RellPluginSettingsState) {
        val command = state.effectiveCommand() ?: return
        if (command == failedCommand) return
        if (!probeInFlight.compareAndSet(false, true)) return
        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                // Build from the captured command, not the live setting — it may change mid-probe,
                // and the output must be recorded against the command that actually produced it.
                val commandLine = state.buildChromiaCliCommandLine(listOf("--version"), overrideCommand = command)
                    ?: return@executeOnPooledThread
                val output = CapturingProcessHandler(commandLine).runProcess(PROBE_TIMEOUT_MS)
                if (!output.isTimeout && output.exitCode == 0) {
                    state.recordChrVersionOutput(command, output.stdout)
                    failedCommand = null
                    notifyChromiaToolWindows()
                } else {
                    LOG.info("chr --version probe failed for '$command' (exit ${output.exitCode}, timeout ${output.isTimeout})")
                    failedCommand = command
                }
            } catch (e: ExecutionException) {
                LOG.info("chr --version probe failed for '$command': ${e.message}")
                failedCommand = command
            } finally {
                probeInFlight.set(false)
            }
        }
    }

    private fun notifyChromiaToolWindows() {
        ApplicationManager.getApplication().invokeLater {
            ProjectManager.getInstance().openProjects.forEach { project ->
                if (!project.isDisposed) {
                    project.messageBus.syncPublisher(RellVersionResolver.TOPIC).chromiaConfigChanged()
                }
            }
        }
    }

    companion object {
        private val LOG = logger<ChrVersionService>()

        fun getInstance(): ChrVersionService =
            ApplicationManager.getApplication().getService(ChrVersionService::class.java)
    }
}
