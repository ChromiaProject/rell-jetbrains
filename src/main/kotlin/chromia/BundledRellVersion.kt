package net.postchain.rellide.jetbrains.chromia

/**
 * The Rell version of the language server bundled with this plugin build, generated at build time
 * from `rell` in libs.versions.toml (see `generateBundledRellVersion` in build.gradle.kts).
 *
 * Needed for UI text only. The bundled server reads `compile.rellVersion` from each project's
 * settings file and compiles that project against it, so nothing here selects a toolchain.
 */
object BundledRellVersion {

    val version: RellVersion = load()

    private fun load(): RellVersion {
        val resource = "rell/bundled-version.txt"
        val text = BundledRellVersion::class.java.classLoader.getResourceAsStream(resource)
            ?.bufferedReader()?.use { it.readText() }
            ?: error("Missing build-generated resource $resource")
        return RellVersion.parse(text.trim())
            ?: error("Malformed version '${text.trim()}' in $resource")
    }
}
