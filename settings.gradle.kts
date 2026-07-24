rootProject.name = "rell-jetbrains"

// Dev-only composite build (see work/local-lsp.sh): build the language server and the Chromia
// project-model parser from a local Rell clone instead of the published release.
val rellLocal = providers.gradleProperty("rellLocal").orNull

if (rellLocal != null) {
    // Substitution is declared explicitly and version-exact. A rule without a version matches every
    // request for the module, which would also capture the pinned older releases that compatibility
    // mode resolves — their LSP lockfiles have to keep describing the real published artifacts.
    val rellVersion = File(settingsDir, "gradle/libs.versions.toml").readLines()
        .first { it.startsWith("rell = ") }
        .substringAfter('"')
        .substringBefore('"')

    includeBuild(rellLocal) {
        dependencySubstitution {
            substitute(module("net.postchain.rell:rell-toolbox-language-server:$rellVersion"))
                .using(project(":rell-toolbox:language-server"))
            substitute(module("net.postchain.rell:rell-toolbox-common:$rellVersion"))
                .using(project(":rell-toolbox:common"))
        }
    }
}
