package net.postchain.rellide.jetbrains.chromia

/** Textual access to the `rellVersion` value of a Chromia settings file. */
object ChromiaConfigQuickFix {
    // Line-anchored so commented-out declarations (# rellVersion: …) never match, and horizontal
    // whitespace only so the match cannot cross lines.
    private val RELL_VERSION_VALUE =
        Regex("""(?m)^([ \t]*rellVersion[ \t]*:[ \t]*)("[^"\r\n]*"|'[^'\r\n]*'|[^ \t#\r\n]+)""")

    /**
     * The `rellVersion` value declared in [configText], unquoted; null when absent. Textual
     * extraction only — used to compare an unsaved editor buffer against the applied on-disk
     * state, not for resolution.
     */
    fun extractDeclaredVersion(configText: String): String? =
        RELL_VERSION_VALUE.find(configText)?.groupValues?.get(2)
            ?.trim('"', '\'')
            ?.takeIf { it.isNotBlank() }
}
