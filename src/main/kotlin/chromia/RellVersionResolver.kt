package net.postchain.rellide.jetbrains.chromia

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.io.write
import com.intellij.util.messages.Topic
import net.postchain.rell.toolbox.chromia.ChromiaModelProvider
import net.postchain.rellide.jetbrains.chromia.RellVersionResolution.Origin
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.createTempFile
import kotlin.io.path.deleteIfExists

/**
 * Resolves the Rell version governing a file from the Chromia settings files that claim it.
 *
 * A settings file is `chromia.yml` or any `*.yml` that parses with the toolchain parser and has a
 * top-level `blockchains` section (see [ChromiaSettingsFiles]) — `chr` reads such files via
 * `-s/--settings`. A settings file *claims* the files under its source root: `compile.source`
 * when declared, else the toolchain's default layout chain (`rell/src`, `rell`, `src`, the config
 * directory itself). Walking up from a file, the deepest directory with a claiming settings file
 * wins (an inner project shadows an outer one); when no source root claims the file, the nearest
 * directory holding any settings files governs it, preserving proximity semantics for files
 * outside every source tree.
 *
 * When the claiming files agree on a version (after dropping below-floor ones from the
 * comparison), resolution behaves as with a single `chromia.yml`. When they disagree, the active
 * settings file ([ChromiaActiveSettings], defaulting per [ChromiaSettingsFiles.defaultChoice])
 * decides, and the outcome is [RellVersionResolution.Conflicting] so the banner can surface the
 * disagreement.
 *
 * Parsing goes through the toolchain's own parser (`rell-toolbox-common`), so the semantics —
 * `.yml` only, blank values treated as absent, parse failures swallowed — are identical to
 * `chr` and the language server.
 */
@Service(Service.Level.PROJECT)
class RellVersionResolver(private val project: Project) {
    private val configCache = ConcurrentHashMap<String, ParsedConfig>()

    fun resolve(file: VirtualFile): RellVersionResolution {
        val group = findClaimGroup(file)
            ?: return RellVersionResolution.Supported(RellVersionRegistry.max, Origin.NO_CONFIG, null)

        val claimants = group.configs.map { Evaluated(it, parsedFor(it)) }
        val chosen = chooseGoverning(group.directory, claimants)
        val distinctInScope = claimants.mapNotNullTo(HashSet()) { it.effective }

        return if (chosen.belowFloor || distinctInScope.size <= 1) {
            outcomeOf(chosen)
        } else {
            RellVersionResolution.Conflicting(
                checkNotNull(chosen.effective) { "non-below-floor claimant has an effective version" },
                chosen.configFile,
                claimants.map { it.toClaimant() },
            )
        }
    }

    /** Every settings file claiming [file], with its evaluation — empty when none does. */
    fun claimants(file: VirtualFile): List<RellSettingsClaimant> =
        findClaimGroup(file)?.configs?.map { Evaluated(it, parsedFor(it)).toClaimant() } ?: emptyList()

    /**
     * How [configFile] itself evaluates — its declared version and the version its toolchain would
     * run. Null when it is not a Chromia settings file.
     */
    fun evaluateConfig(configFile: VirtualFile): RellSettingsClaimant? {
        val parsed = parsedFor(configFile)
        if (!parsed.qualifies) return null
        return Evaluated(configFile, parsed).toClaimant()
    }

    fun dropCaches() {
        configCache.clear()
    }

    /** The raw `compile.rellVersion` string currently on disk in [configFile]; null when absent or unreadable. */
    fun declaredVersion(configFile: VirtualFile): String? = parsedFor(configFile).declaredVersion

    /** Whether [file] counts as a Chromia settings file, parsing (and caching) it if needed. */
    fun isSettingsCandidate(file: VirtualFile): Boolean = parsedFor(file).qualifies

    /** Cache-only peek at [isSettingsCandidate] — never parses, for hot paths like icon lookups. */
    fun isKnownCandidate(path: String): Boolean = configCache[path]?.qualifies == true

    /**
     * Whether [path] has been parsed before, qualifying or not. Re-reading such a file costs one
     * parse that already happened once, so callers that must stay off slow paths (the VFS
     * listener) can still diff it inline.
     */
    fun isCached(path: String): Boolean = configCache.containsKey(path)

    /**
     * Re-reads [configFile] and replaces its cache entry, returning whether anything resolution
     * changes on — declared version, settings-file qualification, or source root — differs from
     * what consumers may have seen. An uncached config has no dependents, so a fresh read is
     * never a change.
     */
    fun refreshConfig(configFile: VirtualFile): Boolean {
        val fresh = readConfig(configFile)
        val previous = configCache.put(configFile.path, fresh)
        return previous != null && previous.resolutionKey != fresh.resolutionKey
    }

    /** Whether any cached settings file lies at or under [path] — used for directory-level VFS events. */
    fun hasCachedConfigUnder(path: String): Boolean {
        val prefix = "$path/"
        return configCache.any { (cachedPath, config) ->
            config.qualifies && (cachedPath == path || cachedPath.startsWith(prefix))
        }
    }

    /**
     * Whether [directoryPath] is, or lies on the path to, a source root of some cached settings
     * file — creating, deleting, or renaming such a directory changes which files that settings
     * file claims. Covers declared `compile.source` values as well as the default layout chain,
     * and matches prefixes so that creating `rell` (on the way to `rell/src`) counts while an
     * unrelated `sub/src` next to a settings file does not.
     */
    fun affectsCachedSourceRoot(directoryPath: String): Boolean = configCache.any { (cachedPath, config) ->
        if (!config.qualifies) return@any false
        val configDir = cachedPath.substringBeforeLast('/')
        val candidates = listOfNotNull(config.source) + ChromiaSettingsFiles.DEFAULT_SOURCE_LAYOUTS
        candidates.any { candidate -> isOnPathToSourceRoot(directoryPath, configDir, candidate) }
    }

    private fun isOnPathToSourceRoot(directoryPath: String, configDir: String, candidate: String): Boolean {
        val root = when {
            candidate.isEmpty() || candidate == "." -> configDir
            isAbsolutePath(candidate) -> candidate
            else -> "$configDir/$candidate"
        }
        return root == directoryPath || root.startsWith("$directoryPath/")
    }

    /** The settings files claiming [file], with the directory whose active choice arbitrates them. */
    private class ClaimGroup(val directory: VirtualFile, val configs: List<VirtualFile>)

    private fun findClaimGroup(file: VirtualFile): ClaimGroup? =
        ApplicationManager.getApplication().runReadAction<ClaimGroup?> {
            // The LSP only discovers settings files below its workspace folders, so the walk is
            // bounded by project content: ancestors outside every content root (including anything
            // above the project) are never consulted, and files outside the project get no config.
            val fileIndex = ProjectFileIndex.getInstance(project)
            var nearestWeak: ClaimGroup? = null
            var dir = file.parent
            while (dir != null && fileIndex.isInContent(dir)) {
                val candidates = settingsCandidates(dir)
                if (candidates.isNotEmpty()) {
                    val claiming = candidates.filter { claims(it, file) }
                    if (claiming.isNotEmpty()) return@runReadAction ClaimGroup(dir, claiming)
                    if (nearestWeak == null) nearestWeak = ClaimGroup(dir, candidates)
                }
                dir = dir.parent
            }
            nearestWeak
        }

    private fun settingsCandidates(dir: VirtualFile): List<VirtualFile> = dir.children
        .filter { !it.isDirectory && it.isValid && ChromiaSettingsFiles.isYmlName(it.name) && parsedFor(it).qualifies }
        .sortedBy { it.name.lowercase() }

    private fun claims(config: VirtualFile, file: VirtualFile): Boolean =
        sourceRoot(config)?.let { VfsUtilCore.isAncestor(it, file, false) } == true

    /** The directory whose files [config] claims — see the class doc for the chain. */
    private fun sourceRoot(config: VirtualFile): VirtualFile? {
        val dir = config.parent ?: return null
        for (candidate in sourceRootCandidates(config)) {
            findRelative(dir, candidate)?.takeIf { it.isDirectory }?.let { return it }
        }
        return dir
    }

    /**
     * The relative (or absolute) paths [config] would use as its source root, in precedence order:
     * the declared `compile.source` first, then the toolchain's default layout chain. Paths are
     * `/`-separated regardless of platform, matching [VirtualFile.findFileByRelativePath].
     */
    private fun sourceRootCandidates(config: VirtualFile): List<String> =
        listOfNotNull(parsedFor(config).source) + ChromiaSettingsFiles.DEFAULT_SOURCE_LAYOUTS

    private fun findRelative(dir: VirtualFile, path: String): VirtualFile? = when {
        path.isEmpty() || path == "." -> dir
        isAbsolutePath(path) -> dir.fileSystem.findFileByPath(path)
        else -> dir.findFileByRelativePath(path)
    }

    /** `/src` on POSIX, `C:/src` on Windows — both already `/`-separated by [slashSeparated]. */
    private fun isAbsolutePath(path: String): Boolean =
        path.startsWith('/') || path.length >= 2 && path[1] == ':'

    private fun chooseGoverning(directory: VirtualFile, claimants: List<Evaluated>): Evaluated {
        val active = ChromiaActiveSettings.getInstance(project).activeFileName(directory.path)
        if (active != null) {
            claimants.find { it.configFile.name.equals(active, ignoreCase = true) }?.let { return it }
        }
        val byName = claimants.associateBy { it.configFile.name }
        val chosenName = ChromiaSettingsFiles.defaultChoice(byName.keys.toList()) { byName.getValue(it).declared }
        return byName.getValue(chosenName)
    }

    private fun outcomeOf(chosen: Evaluated): RellVersionResolution {
        val configFile = chosen.configFile

        if (!chosen.parsed.readable) {
            return RellVersionResolution.Supported(RellVersionRegistry.max, Origin.UNREADABLE_CONFIG, configFile)
        }

        val declared = chosen.parsed.declaredVersion
            ?: return RellVersionResolution.Supported(RellVersionRegistry.max, Origin.NO_VERSION_KEY, configFile)

        val version = chosen.declared

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

    private class Evaluated(val configFile: VirtualFile, val parsed: ParsedConfig) {
        val declared: RellVersion? = parsed.declaredVersion?.let(RellVersion::parse)

        val belowFloor: Boolean
            get() = declared != null && declared < RellVersionRegistry.floor

        val effective: RellVersion?
            get() = ChromiaSettingsFiles.effectiveVersion(declared)

        fun toClaimant(): RellSettingsClaimant = RellSettingsClaimant(configFile, declared, effective)
    }

    private fun parsedFor(configFile: VirtualFile): ParsedConfig =
        configCache.computeIfAbsent(configFile.path) { readConfig(configFile) }

    private fun readConfig(configFile: VirtualFile): ParsedConfig {
        val path = try {
            configFile.toNioPath()
        } catch (_: UnsupportedOperationException) {
            null
        }
        // The blockchains scan reads the same bytes the toolchain parser is about to read — disk
        // for local files — so qualification can never disagree with the parse result.
        if (path != null) {
            return parseWithToolchain(path, configFile, hasBlockchainsSection(path))
        }

        // Non-local files (e.g. in-memory fixtures) expose no on-disk path; snapshot the content
        // to a temp file so the toolchain parser still defines the semantics.
        val bytes = try {
            configFile.contentsToByteArray()
        } catch (_: IOException) {
            null
        }
        val hasBlockchains = bytes != null &&
                ChromiaSettingsFiles.TOP_LEVEL_BLOCKCHAINS.containsMatchIn(String(bytes, configFile.charset))
        return snapshotToTempFile(bytes)?.let { tempPath ->
            try {
                parseWithToolchain(tempPath, configFile, hasBlockchains)
            } finally {
                tempPath.deleteIfExists()
            }
        } ?: unreadable(configFile, hasBlockchains)
    }

    private fun parseWithToolchain(path: Path, configFile: VirtualFile, hasBlockchains: Boolean): ParsedConfig {
        // Extract-and-discard: the toolbox model classes reference a type excluded from the plugin
        // classpath (RellLibraryModel.rid: WrappedByteArray). Constructing them and reading fields
        // is safe, but toString/equals/hashCode on them throws NoClassDefFoundError — never retain,
        // compare, or log these objects.
        val model = ChromiaModelProvider.loadChromiaModelFromFile(path) ?: return unreadable(configFile, hasBlockchains)
        return ParsedConfig(
            declaredVersion = model.compile.rellVersion,
            readable = true,
            qualifies = ChromiaSettingsFiles.isDefaultName(configFile.name) || hasBlockchains,
            source = relativeSource(model.compile.source, path),
        )
    }

    /**
     * `compile.source` as a `/`-separated path relative to the config's directory, which is what
     * [VirtualFile.findFileByRelativePath] consumes — `Path.toString()` would yield `\` on Windows
     * and never resolve. The toolchain resolves the key against the parsed file's directory, which
     * for a snapshotted non-local config is the temp directory, so a source that does not live
     * under it is kept as an absolute `/`-separated path instead of a `../..` walk out of temp.
     */
    private fun relativeSource(source: Path?, configPath: Path): String? {
        val sourcePath = source ?: return null
        val configDir = configPath.parent ?: return slashSeparated(sourcePath)
        return if (sourcePath.startsWith(configDir)) {
            slashSeparated(configDir.relativize(sourcePath))
        } else {
            slashSeparated(sourcePath)
        }
    }

    /** [path] with `/` separators, keeping the root (the drive prefix on Windows) when absolute. */
    private fun slashSeparated(path: Path): String {
        val relative = path.joinToString("/")
        val root = path.root?.toString()?.replace('\\', '/') ?: return relative
        return if (root.endsWith("/")) "$root$relative" else "$root/$relative"
    }

    private fun hasBlockchainsSection(path: Path): Boolean = try {
        ChromiaSettingsFiles.TOP_LEVEL_BLOCKCHAINS.containsMatchIn(Files.readString(path))
    } catch (_: IOException) {
        false
    }

    private fun snapshotToTempFile(bytes: ByteArray?): Path? = try {
        bytes?.let { content -> createTempFile("chromia-snapshot", ".yml").also { it.write(content) } }
    } catch (_: IOException) {
        null
    }

    private fun unreadable(configFile: VirtualFile, hasBlockchains: Boolean): ParsedConfig {
        LOG.warn("Unreadable Chromia settings file at ${configFile.path}; using ${RellVersionRegistry.max}")
        return ParsedConfig(
            declaredVersion = null,
            readable = false,
            // An unparseable alternate never qualifies: the blockchains gate exists to exclude
            // unrelated YAML, and an unreadable file cannot prove it is a settings file.
            qualifies = ChromiaSettingsFiles.isDefaultName(configFile.name),
            source = null,
        )
    }

    private data class ParsedConfig(
        val declaredVersion: String?,
        val readable: Boolean,
        val qualifies: Boolean,
        /** `compile.source` relative to the config's directory, null when not declared. */
        val source: String?,
    ) {
        /** The fields a changed value of which can move files to a different resolution. */
        val resolutionKey: Triple<String?, Boolean, String?>
            get() = Triple(declaredVersion, qualifies, source)
    }

    companion object {
        const val CHROMIA_YML = ChromiaSettingsFiles.CHROMIA_YML

        /** Notified after any VFS change to a Chromia settings file, once resolver caches are dropped. */
        val TOPIC: Topic<ChromiaConfigListener> =
            Topic.create("Rell Chromia settings changes", ChromiaConfigListener::class.java)

        private val LOG = logger<RellVersionResolver>()

        fun getInstance(project: Project): RellVersionResolver = project.getService(RellVersionResolver::class.java)
    }
}

fun interface ChromiaConfigListener {
    fun chromiaConfigChanged()
}
