package net.postchain.rellide.jetbrains.chromia

import com.intellij.openapi.vfs.VirtualFile

/**
 * Outcome of resolving the Rell version for a file from its nearest `chromia.yml`
 * (see docs/COMPATIBILITY.md for the full rules).
 */
sealed interface RellVersionResolution {
    /** The `chromia.yml` that determined the outcome, null when none was found. */
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

    enum class Origin {
        /** `compile.rellVersion` named a supported version. */
        DECLARED,

        /** No `chromia.yml` above the file — the plugin's newest version applies. */
        NO_CONFIG,

        /** `chromia.yml` exists but has no usable `compile.rellVersion` value. */
        NO_VERSION_KEY,

        /** `chromia.yml` could not be read or parsed. */
        UNREADABLE_CONFIG,

        /** `compile.rellVersion` is not a `major.minor.patch` version string. */
        MALFORMED_VERSION,
    }
}
