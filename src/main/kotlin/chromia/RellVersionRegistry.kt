package net.postchain.rellide.jetbrains.chromia

/**
 * The Rell versions this plugin build supports, generated at build time from
 * `supportedRellVersions` in build.gradle.kts (see docs/COMPATIBILITY.md).
 */
object RellVersionRegistry {

    /** All supported versions, ascending. */
    val supported: List<RellVersion> = loadSupportedVersions()

    /** The compatibility floor: versions below it get no toolchain at all. */
    val floor: RellVersion = supported.first()

    /** The newest supported version — the bundled toolchain and the default when nothing is declared. */
    val max: RellVersion = supported.last()

    fun isSupported(version: RellVersion): Boolean = version in supported

    private fun loadSupportedVersions(): List<RellVersion> {
        val resource = "rell/supported-versions.txt"
        val text = RellVersionRegistry::class.java.classLoader.getResourceAsStream(resource)
            ?.bufferedReader()?.use { it.readText() }
            ?: error("Missing build-generated resource $resource")
        val versions = text.lines().filter { it.isNotBlank() }.map { line ->
            RellVersion.parse(line) ?: error("Malformed version '$line' in $resource")
        }
        check(versions.isNotEmpty() && versions == versions.sorted().distinct()) {
            "$resource must list distinct versions in ascending order, was: $versions"
        }
        return versions
    }
}
