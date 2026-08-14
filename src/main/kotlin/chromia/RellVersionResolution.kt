package net.postchain.rellide.jetbrains.chromia

import com.intellij.openapi.vfs.VirtualFile

/**
 * Outcome of resolving the Rell version for a file from the Chromia settings files claiming it
 * (see docs/COMPATIBILITY.md for the full rules).
 *
 * The version is shown, never enforced: every `.rell` file is served by the one bundled language
 * server, which reads `compile.rellVersion` itself and compiles the project against it.
 */
sealed interface RellVersionResolution {
    /** The settings file that determined the outcome, null when none was found. */
    val configFile: VirtualFile?

    /** The version this file is analysed at — what the language server reads from the settings file. */
    val effectiveVersion: RellVersion

    data class Supported(
        val version: RellVersion,
        val origin: Origin,
        override val configFile: VirtualFile?,
    ) : RellVersionResolution {
        override val effectiveVersion: RellVersion
            get() = version
    }

    /**
     * Several settings files claim the file but disagree on the Rell version. The active (or
     * default-chosen) file's version governs; no banner is shown, because the file gets a working
     * toolchain either way — the status-bar widget names the governing file, reports the
     * disagreement, and offers switching.
     */
    data class Conflicting(
        val version: RellVersion,
        override val configFile: VirtualFile,
    ) : RellVersionResolution {
        override val effectiveVersion: RellVersion
            get() = version
    }

    enum class Origin {
        /** `compile.rellVersion` named a version. */
        DECLARED,

        /** No settings file claims the file — the bundled version applies. */
        NO_CONFIG,

        /** The settings file exists but has no usable `compile.rellVersion` value. */
        NO_VERSION_KEY,

        /** The settings file could not be read or parsed. */
        UNREADABLE_CONFIG,

        /** `compile.rellVersion` is not a `major.minor.patch` version string. */
        MALFORMED_VERSION,
    }
}

/**
 * One settings file claiming a given source file, with its parsed `compile.rellVersion` (null when
 * absent or malformed).
 */
data class RellSettingsClaimant(
    val configFile: VirtualFile,
    val declared: RellVersion?,
)
