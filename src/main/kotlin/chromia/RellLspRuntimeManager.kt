package net.postchain.rellide.jetbrains.chromia

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.util.io.HttpRequests
import com.intellij.util.io.createDirectories
import java.io.IOException
import java.nio.file.Path
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.*

/**
 * Provides version-exact language-server runtimes. The newest supported version is bundled with
 * the plugin; older supported versions are downloaded on demand into
 * `<system>/rell-lsp/<version>/` from the lockfile artifacts (checksum-verified) and reused across
 * IDE sessions. A wrong-version server is never substituted: until the runtime is ready, the
 * project simply has no LSP (see RellVersionedDocumentMatcher).
 */
@Service
class RellLspRuntimeManager {

    private val inFlight = ConcurrentHashMap.newKeySet<RellVersion>()
    private val validatedReady = ConcurrentHashMap.newKeySet<RellVersion>()

    /** Versions whose download failed; retried only via the notification's Retry action. */
    private val failed = ConcurrentHashMap.newKeySet<RellVersion>()

    fun cachedRuntimeDir(version: RellVersion): Path =
        Path.of(PathManager.getSystemPath(), "rell-lsp", version.toString())

    fun isRuntimeReady(version: RellVersion): Boolean {
        if (version == RellVersionRegistry.max) return true
        if (version in validatedReady) return true
        // The marker must match the lockfile of the *current* plugin build: a plugin upgrade can
        // re-pin the same Rell version to different artifacts, and a stale classpath is exactly
        // the mixed-versions failure mode the language server refuses to start with.
        val markerFile = cachedRuntimeDir(version) / COMPLETE_MARKER
        if (!markerFile.isRegularFile()) return false
        val upToDate = try {
            markerFile.readText() == markerContent(RellLspLockfile.load(version))
        } catch (_: IOException) {
            false
        }
        if (upToDate) validatedReady.add(version)
        return upToDate
    }

    fun ensureRuntimeAsync(version: RellVersion, project: Project) {
        if (isRuntimeReady(version) || ApplicationManager.getApplication().isUnitTestMode) return
        if (version in failed || !inFlight.add(version)) return
        object : Task.Backgroundable(project, "Downloading Rell $version language server", true) {
            override fun run(indicator: ProgressIndicator) = downloadRuntime(version, indicator)

            override fun onSuccess() {
                // Re-run highlighting in every open project so lsp4ij re-evaluates the document
                // matchers and connects the now-available server.
                for (openProject in ProjectManager.getInstance().openProjects) {
                    if (!openProject.isDisposed) {
                        DaemonCodeAnalyzer.getInstance(openProject).restart("Rell $version LSP runtime downloaded")
                    }
                }
            }

            override fun onThrowable(error: Throwable) {
                LOG.warn("Rell $version language-server download failed", error)
                failed.add(version)
                notifyFailure(version, project, error)
            }

            override fun onFinished() {
                inFlight.remove(version)
            }
        }.queue()
    }

    private fun retryDownload(version: RellVersion, project: Project) {
        failed.remove(version)
        ensureRuntimeAsync(version, project)
    }

    private fun downloadRuntime(version: RellVersion, indicator: ProgressIndicator) {
        val artifacts = RellLspLockfile.load(version)
        val targetDir = cachedRuntimeDir(version)
        targetDir.createDirectories()
        (targetDir / COMPLETE_MARKER).deleteIfExists()
        validatedReady.remove(version)

        for ((index, artifact) in artifacts.withIndex()) {
            indicator.checkCanceled()
            indicator.fraction = index.toDouble() / artifacts.size
            indicator.text2 = artifact.fileName
            val target = targetDir / artifact.fileName
            if (target.isRegularFile() && sha256(target) == artifact.sha256) continue
            downloadArtifact(artifact, target, indicator)
            val actual = sha256(target)
            if (actual != artifact.sha256) {
                target.deleteIfExists()
                throw IOException("Checksum mismatch for ${artifact.fileName}: expected ${artifact.sha256}, got $actual")
            }
        }

        (targetDir / COMPLETE_MARKER).writeText(markerContent(artifacts))
    }

    private fun markerContent(artifacts: List<RellLspLockfile.Artifact>): String =
        artifacts.joinToString("\n") { "${it.gav} ${it.sha256}" }

    private fun downloadArtifact(artifact: RellLspLockfile.Artifact, target: Path, indicator: ProgressIndicator) {
        var lastError: IOException? = null
        for (repository in MAVEN_REPOSITORIES) {
            try {
                HttpRequests.request("$repository/${artifact.mavenPath}").saveToFile(target, indicator)
                return
            } catch (e: IOException) {
                lastError = e
            }
        }
        throw IOException("Could not download ${artifact.gav} from any repository", lastError)
    }

    private fun sha256(path: Path): String {
        val digest = MessageDigest.getInstance("SHA-256")

        path.inputStream().use { input ->
            val buffer = ByteArray(64 * 1024)
            while (true) {
                val read = input.read(buffer)
                if (read < 0) break
                digest.update(buffer, 0, read)
            }
        }

        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    private fun notifyFailure(version: RellVersion, project: Project, error: Throwable) {
        NotificationGroupManager.getInstance().getNotificationGroup("Rell")
            .createNotification(
                "Rell $version language server unavailable",
                "The one-time download of the Rell $version tooling failed: ${error.message} " +
                        "Language features stay off for projects on Rell $version until it succeeds.",
                NotificationType.ERROR,
            )
            .addAction(NotificationAction.createSimpleExpiring("Retry") { retryDownload(version, project) })
            .notify(project)
    }

    companion object {
        private const val COMPLETE_MARKER = ".complete"

        // Mirrors the repositories in build.gradle.kts: every lockfile artifact resolves from one
        // of these.
        private val MAVEN_REPOSITORIES = listOf(
            "https://repo1.maven.org/maven2",
            "https://maven.emrld.io",
            "https://gitlab.com/api/v4/projects/32802097/packages/maven",
            "https://gitlab.com/api/v4/projects/32294340/packages/maven",
            "https://gitlab.com/api/v4/projects/50818999/packages/maven",
            "https://gitlab.com/api/v4/projects/64941451/packages/maven",
        )

        private val LOG = logger<RellLspRuntimeManager>()

        fun getInstance(): RellLspRuntimeManager =
            ApplicationManager.getApplication().getService(RellLspRuntimeManager::class.java)
    }
}
