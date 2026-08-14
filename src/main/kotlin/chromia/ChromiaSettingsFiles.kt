package net.postchain.rellide.jetbrains.chromia

/**
 * What counts as a Chromia settings file, shared by the resolver, the tool window discovery, and
 * the VFS listener. `chr` reads `chromia.yml` by default and any other file via `-s/--settings`,
 * so the default name always qualifies while alternate names must look like a settings file:
 * parse with the toolchain parser and declare a top-level `blockchains` section. The section gate
 * keeps unrelated YAML (CI configs, qodana.yml) out — the toolchain parser alone accepts any
 * mapping, and its model does not expose `blockchains`, hence the textual check.
 */
object ChromiaSettingsFiles {
    const val CHROMIA_YML = "chromia.yml"

    /** `chr` only ever reads `.yml`, never `.yaml`. */
    private const val YML_EXTENSION = ".yml"

    /** Anchored to column 0 so only a top-level key matches, never a nested or commented one. */
    val TOP_LEVEL_BLOCKCHAINS = Regex("""(?m)^blockchains[ \t]*:""")

    /** The layouts the language server probes when `compile.source` is absent, in its order. */
    val DEFAULT_SOURCE_LAYOUTS = listOf("rell/src", "rell", "src")

    fun isDefaultName(name: String?): Boolean = CHROMIA_YML.equals(name, ignoreCase = true)

    fun isYmlName(name: String?): Boolean = name?.endsWith(YML_EXTENSION, ignoreCase = true) == true

    /** The version a config declaring [declared] is analysed at — the bundled one when it declares nothing. */
    fun effectiveVersion(declared: RellVersion?): RellVersion = declared ?: BundledRellVersion.version

    /**
     * The settings file that governs a directory when the user has not chosen one: `chromia.yml`
     * if present, else the candidate declaring the newest version, ties broken by name for
     * determinism. [declaredOf] supplies each candidate's parsed `compile.rellVersion` (null when
     * absent or malformed).
     */
    fun defaultChoice(names: List<String>, declaredOf: (String) -> RellVersion?): String {
        require(names.isNotEmpty()) { "defaultChoice needs at least one candidate" }
        names.firstOrNull { isDefaultName(it) }?.let { return it }
        return names.sortedWith(
            compareByDescending<String> { effectiveVersion(declaredOf(it)) }.thenBy { it.lowercase() },
        ).first()
    }
}
