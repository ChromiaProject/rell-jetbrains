package net.postchain.rellide.jetbrains.chromia

import com.intellij.openapi.vfs.VirtualFile

/**
 * Outcome of resolving the Rell version for a file from the Chromia settings files claiming it
 * (see docs/COMPATIBILITY.md for the full rules).
 */
sealed interface RellVersionResolution {
    /** The settings file that determined the outcome, null when none was found. */
    val configFile: VirtualFile?

    /** The toolchain version consumers (grammar, LSP) must use; null means hard cease. */
    val effectiveVersion: RellVersion?

    data class Supported(
        val version: RellVersion,
        val origin: Origin,
        override val configFile: VirtualFile?,
    ) : RellVersionResolution {
        override val effectiveVersion: RellVersion
            get() = version
    }

    /** The declared version is unknown to this plugin build; the newest supported one is used instead. */
    data class Clamped(
        val declared: RellVersion,
        val effective: RellVersion,
        override val configFile: VirtualFile,
    ) : RellVersionResolution {
        override val effectiveVersion: RellVersion
            get() = effective
    }

    /** The declared version predates the compatibility floor: no toolchain runs for this project. */
    data class Unsupported(
        val declared: RellVersion,
        override val configFile: VirtualFile,
    ) : RellVersionResolution {
        override val effectiveVersion: RellVersion?
            get() = null
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
        /** `compile.rellVersion` named a supported version. */
        DECLARED,

        /** No settings file claims the file — the plugin's newest version applies. */
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
 * One settings file claiming a given source file: its parsed `compile.rellVersion` (null when
 * absent or malformed) and the version its toolchain would use (null below the compatibility
 * floor — such claimants are out of scope for conflict detection).
 */
data class RellSettingsClaimant(
    val configFile: VirtualFile,
    val declared: RellVersion?,
    val effectiveVersion: RellVersion?,
) {
    val belowFloor: Boolean
        get() = declared != null && declared < RellVersionRegistry.floor
}
