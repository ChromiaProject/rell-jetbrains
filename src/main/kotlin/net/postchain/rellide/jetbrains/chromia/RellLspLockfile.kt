package net.postchain.rellide.jetbrains.chromia

/**
 * Build-generated lockfiles (`rell/lsp-lockfiles/<version>.lock`) listing the exact runtime
 * classpath of each downloadable language-server version: one `group:module:version fileName sha256`
 * line per artifact (see `generateRellLspLockfiles` in build.gradle.kts).
 */
object RellLspLockfile {

    data class Artifact(
        val group: String,
        val module: String,
        val version: String,
        val fileName: String,
        val sha256: String,
    ) {
        val gav: String
            get() = "$group:$module:$version"

        /** Repository-relative Maven layout path of this artifact. */
        val mavenPath: String
            get() = "${group.replace('.', '/')}/$module/$version/$fileName"
    }

    fun load(version: RellVersion): List<Artifact> {
        val resource = "rell/lsp-lockfiles/$version.lock"

        val text = RellLspLockfile::class.java.classLoader.getResourceAsStream(resource)
            ?.bufferedReader()?.use { it.readText() }
            ?: error("Missing build-generated lockfile $resource")

        return parse(text)
    }

    fun parse(text: String): List<Artifact> = text.lines().filter { it.isNotBlank() }.map { line ->
        val parts = line.trim().split(Regex("\\s+"))
        require(parts.size == 3) { "Malformed lockfile line: $line" }
        val gav = parts[0].split(':')
        require(gav.size == 3 && gav.none { it.isEmpty() }) { "Malformed artifact coordinates: ${parts[0]}" }
        Artifact(group = gav[0], module = gav[1], version = gav[2], fileName = parts[1], sha256 = parts[2])
    }
}
