package net.postchain.rellide.jetbrains.chromia

/**
 * A Rell language version in strict `major.minor.patch` form.
 *
 * Parsing mirrors rell-base's `R_LangVersion`: exactly three dot-separated numeric components
 * without leading zeros. Anything else (including two-component strings YAML may produce) is
 * rejected so the plugin never acts on a version the toolchain itself would not accept.
 */
data class RellVersion(val major: Int, val minor: Int, val patch: Int) : Comparable<RellVersion> {

    override fun compareTo(other: RellVersion): Int =
        compareValuesBy(this, other, RellVersion::major, RellVersion::minor, RellVersion::patch)

    override fun toString(): String = "$major.$minor.$patch"

    companion object {
        private val COMPONENT = Regex("0|[1-9][0-9]*")

        fun parse(text: String): RellVersion? {
            val parts = text.split('.')
            if (parts.size != 3 || parts.any { !COMPONENT.matches(it) }) return null
            val (major, minor, patch) = parts.map { it.toIntOrNull() ?: return null }
            return RellVersion(major, minor, patch)
        }
    }
}
