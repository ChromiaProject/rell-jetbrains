package net.postchain.rellide.jetbrains.chromia

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.io.write
import com.intellij.util.messages.Topic
import net.postchain.rell.toolbox.chromia.ChromiaModelProvider
import net.postchain.rellide.jetbrains.chromia.RellVersionResolution.Origin
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.createTempFile
import kotlin.io.path.deleteIfExists

/**
 * Resolves the Rell version governing a file from the nearest enclosing `chromia.yml`,
 * mirroring how the Rell language server anchors an index root at each `chromia.yml` directory.
 *
 * Parsing goes through the toolchain's own parser (`rell-toolbox-common`), so the semantics —
 * `.yml` only, blank values treated as absent, parse failures swallowed — are identical to
 * `chr` and the language server.
 */
@Service(Service.Level.PROJECT)
class RellVersionResolver(private val project: Project) {

    private val configCache = ConcurrentHashMap<String, ParsedConfig>()

    fun resolve(file: VirtualFile): RellVersionResolution {
        val configFile = findNearestConfig(file)
            ?: return RellVersionResolution.Supported(RellVersionRegistry.max, Origin.NO_CONFIG, null)
        val parsed = configCache.computeIfAbsent(configFile.path) { readConfig(configFile) }

        if (!parsed.readable) {
            return RellVersionResolution.Supported(RellVersionRegistry.max, Origin.UNREADABLE_CONFIG, configFile)
        }
        val declared = parsed.declaredVersion
            ?: return RellVersionResolution.Supported(RellVersionRegistry.max, Origin.NO_VERSION_KEY, configFile)
        val version = RellVersion.parse(declared)
        if (version == null) {
            LOG.warn("Malformed compile.rellVersion '$declared' in ${configFile.path}; using ${RellVersionRegistry.max}")
            return RellVersionResolution.Supported(RellVersionRegistry.max, Origin.MALFORMED_VERSION, configFile)
        }
        return when {
            version < RellVersionRegistry.floor -> RellVersionResolution.Unsupported(version, configFile)
            RellVersionRegistry.isSupported(version) ->
                RellVersionResolution.Supported(version, Origin.DECLARED, configFile)
            else -> RellVersionResolution.Clamped(version, RellVersionRegistry.max, configFile)
        }
    }

    fun dropCaches() {
        configCache.clear()
    }

    /** The raw `compile.rellVersion` string currently on disk in [configFile]; null when absent or unreadable. */
    fun declaredVersion(configFile: VirtualFile): String? =
        configCache.computeIfAbsent(configFile.path) { readConfig(configFile) }.declaredVersion

    /**
     * Re-reads [configFile] and replaces its cache entry, returning whether the declared
     * `compile.rellVersion` changed relative to what consumers may have seen. An uncached config
     * has no dependents, so a fresh read is never a change.
     */
    fun refreshConfig(configFile: VirtualFile): Boolean {
        val fresh = readConfig(configFile)
        val previous = configCache.put(configFile.path, fresh)
        return previous != null && previous.declaredVersion != fresh.declaredVersion
    }

    /** Whether any cached `chromia.yml` entry lies at or under [path] — used for directory-level VFS events. */
    fun hasCachedConfigUnder(path: String): Boolean {
        val prefix = "$path/"
        return configCache.keys.any { it == path || it.startsWith(prefix) }
    }

    private fun findNearestConfig(file: VirtualFile): VirtualFile? =
        ApplicationManager.getApplication().runReadAction<VirtualFile?> {
            // The LSP only discovers chromia.yml below its workspace folders, so the walk is bounded
            // by project content: ancestors outside every content root (including anything above the
            // project) are never consulted, and files outside the project get no config at all.
            val fileIndex = ProjectFileIndex.getInstance(project)
            var dir = file.parent
            while (dir != null && fileIndex.isInContent(dir)) {
                val config = dir.findChild(CHROMIA_YML)?.takeIf { it.isValid && !it.isDirectory }
                if (config != null) return@runReadAction config
                dir = dir.parent
            }
            null
        }

    private fun readConfig(configFile: VirtualFile): ParsedConfig {
        val path = try {
            configFile.toNioPath()
        } catch (_: UnsupportedOperationException) {
            // Non-local files (e.g. in-memory fixtures) expose no on-disk path; snapshot the content
            // to a temp file so the toolchain parser still defines the semantics.
            return snapshotToTempFile(configFile)?.let { tempPath ->
                try {
                    parseWithToolchain(tempPath, configFile)
                } finally {
                    tempPath.deleteIfExists()
                }
            } ?: unreadable(configFile)
        }
        return parseWithToolchain(path, configFile)
    }

    private fun parseWithToolchain(path: java.nio.file.Path, configFile: VirtualFile): ParsedConfig {
        // Extract-and-discard: the toolbox model classes reference a type excluded from the plugin
        // classpath (RellLibraryModel.rid: WrappedByteArray). Constructing them and reading fields
        // is safe, but toString/equals/hashCode on them throws NoClassDefFoundError — never retain,
        // compare, or log these objects.
        val model = ChromiaModelProvider.loadChromiaModelFromFile(path) ?: return unreadable(configFile)
        return ParsedConfig(declaredVersion = model.compile.rellVersion, readable = true)
    }

    private fun snapshotToTempFile(configFile: VirtualFile): java.nio.file.Path? = try {
        createTempFile("chromia-snapshot", ".yml").also { it.write(configFile.contentsToByteArray()) }
    } catch (_: IOException) {
        null
    }

    private fun unreadable(configFile: VirtualFile): ParsedConfig {
        LOG.warn("Unreadable chromia.yml at ${configFile.path}; using ${RellVersionRegistry.max}")
        return ParsedConfig(declaredVersion = null, readable = false)
    }

    private data class ParsedConfig(val declaredVersion: String?, val readable: Boolean)

    companion object {
        const val CHROMIA_YML = "chromia.yml"

        /** Notified after any VFS change to a `chromia.yml`, once resolver caches are dropped. */
        val TOPIC: Topic<ChromiaConfigListener> =
            Topic.create("Rell chromia.yml changes", ChromiaConfigListener::class.java)

        private val LOG = logger<RellVersionResolver>()

        fun getInstance(project: Project): RellVersionResolver = project.getService(RellVersionResolver::class.java)
    }
}

fun interface ChromiaConfigListener {
    fun chromiaConfigChanged()
}
