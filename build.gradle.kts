import java.security.MessageDigest
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.plugins.antlr.AntlrTask
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

plugins {
    antlr
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.intellij.platform)
    alias(libs.plugins.changelog)
    alias(libs.plugins.qodana)
    alias(libs.plugins.kover)
    alias(libs.plugins.sentry)
}

val sentryAuthToken: String? = System.getenv("SENTRY_AUTH_TOKEN")

sentry {
    // Auto-installation injects extra Sentry modules (e.g. sentry-jdbc at the plugin's bundled SDK
    // version) into every resolved configuration — including the detached LSP runtime classpath,
    // where they clash with the SDK version rell-toolbox ships. Sentry refuses mixed versions at
    // runtime, which kills the language server on startup. All Sentry deps are declared explicitly.
    autoInstallation {
        enabled = false
    }

    // Generates a JVM (Java, Kotlin, etc.) source bundle and uploads your source code to Sentry.
    // This enables source context, allowing you to see your source
    // code as part of your stack traces in Sentry.
    includeSourceContext = sentryAuthToken != null
    org = "chromaway-ab-za"
    projectName = "rell-jetbrains"
    authToken = System.getenv("SENTRY_AUTH_TOKEN") ?: ""
}

// Path of the local Rell clone in dev mode (work/local-lsp.sh); null for a regular build.
val rellLocal: String? = providers.gradleProperty("rellLocal").orNull

// Rell's ANTLR grammar (Rell.g4) ships in the `frontend` sources jar; we extract it at build time so
// the editor parser always tracks the `rell` version in libs.versions.toml — no vendored grammar copy.
// In dev mode it comes from the clone's source tree instead: a `:sources@jar` request can't be served
// by the composite build's project substitution.
val rellGrammar: Configuration by configurations.creating { isTransitive = false }

dependencies {
    if (rellLocal == null) rellGrammar("net.postchain.rell:frontend:${libs.versions.rell.get()}:sources@jar")
    antlr(libs.antlr)
    implementation(libs.antlr.runtime)

    // Chromia project-model parsing (chromia.yml) — the same parser the Rell toolchain uses, so
    // compile.rellVersion resolution matches `chr` and the language server exactly. Only the
    // ChromiaModelProvider/ChromiaModel classes are used; the excluded modules serve toolbox code
    // the plugin never loads:
    implementation(libs.rell.toolbox.common) {
        exclude(group = "net.postchain.rell", module = "rell-base")
        exclude(group = "org.antlr")                    // the editor grammar declares its own antlr4-runtime
        exclude(group = "org.ec4j.core")
        exclude(group = "org.slf4j")                    // provided by the platform
        exclude(group = "org.jetbrains.kotlin")         // the platform stdlib must win (see 6b7203c)
        exclude(group = "com.fasterxml.jackson.module") // -kotlin module unused by the untyped YAML parse
    }

    implementation(libs.sentry)
    testImplementation(libs.junit)
    testImplementation(libs.opentest4j)

    intellijPlatform {
        plugins(providers.gradleProperty("platformPlugins").map { it.split(',') })
        bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(',') })
        create(properties("platformType"), properties("platformVersion"))
        pluginVerifier()
        zipSigner()
        testFramework(TestFrameworkType.Platform)
    }
}

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()

repositories {
    mavenCentral()
    maven("https://maven.emrld.io") {
        name = "etherjar"
    }
    maven("https://gitlab.com/api/v4/projects/32802097/packages/maven") {
        name = "rell"
    }
    maven("https://gitlab.com/api/v4/projects/32294340/packages/maven") {
        name = "postchain"
    }
    maven("https://gitlab.com/api/v4/projects/50818999/packages/maven") {
        name = "chromia-parent"
    }
    maven("https://gitlab.com/api/v4/projects/64941451/packages/maven") {
        name = "chromia-cli-tools"
    }
    intellijPlatform {
        defaultRepositories()
    }
}

val antlrPackage = "net.postchain.rellide.jetbrains.language.parser"
val grammarDir = layout.buildDirectory.dir("rell-grammar")

// Unpack Rell.g4 from the frontend sources jar into a build dir that the antlr source set reads from.
val extractRellGrammar by tasks.registering(Sync::class) {
    group = "build setup"
    description = "Unpacks Rell.g4 from the Rell frontend sources jar into the ANTLR grammar source dir."
    if (rellLocal == null) {
        from({ zipTree(rellGrammar.singleFile) }) { include("Rell.g4") }
    } else {
        from("$rellLocal/rell-base/frontend/src/main/antlr") { include("Rell.g4") }
    }
    into(grammarDir)
}

sourceSets["main"].extensions.getByName<SourceDirectorySet>("antlr")
    .setSrcDirs(listOf(grammarDir.get().asFile))

// Compatibility mode (docs/COMPATIBILITY.md): every Rell release this plugin build supports,
// oldest first. Bumping `rell` in libs.versions.toml requires appending the new version here —
// the generation task fails otherwise, so the two can't drift.
val supportedRellVersions = listOf("0.16.0", "0.16.1")

val generateRellVersionRegistry by tasks.registering {
    group = "build setup"
    description = "Writes the supported Rell version list read by RellVersionRegistry at runtime."
    val versions = supportedRellVersions
    val rellVersion = libs.versions.rell.get()
    val outDir = layout.buildDirectory.dir("generated-resources/rell-versions")
    inputs.property("versions", versions)
    inputs.property("rellVersion", rellVersion)
    outputs.dir(outDir)
    doLast {
        check(versions.last() == rellVersion) {
            "supportedRellVersions must end with the `rell` version from libs.versions.toml ($rellVersion), was: $versions"
        }
        val file = outDir.get().file("rell/supported-versions.txt").asFile
        file.parentFile.mkdirs()
        file.writeText(versions.joinToString("\n"))
    }
}

sourceSets["main"].resources.srcDir(generateRellVersionRegistry)

// Version-exact grammars for the supported Rell versions below the newest. The newest grammar keeps
// driving the editor PSI through the default antlr pipeline above (IElementType identity must come
// from a single grammar); each older version compiles into a version-suffixed package that
// RellVersionSyntaxAnnotator runs for version-true syntax errors.
val versionedGrammarRoots = supportedRellVersions.dropLast(1).map { version ->
    val suffix = "v" + version.replace('.', '_')
    val grammarConfig = configurations.create("rellGrammar${suffix.replaceFirstChar { it.uppercase() }}") {
        isTransitive = false
    }
    dependencies { grammarConfig("net.postchain.rell:frontend:$version:sources@jar") }

    val versionedGrammarDir = layout.buildDirectory.dir("rell-grammar-$suffix")
    val extract = tasks.register<Sync>("extractRellGrammar${suffix.replaceFirstChar { it.uppercase() }}") {
        group = "build setup"
        description = "Unpacks Rell.g4 of Rell $version for the versioned syntax annotator."
        from({ zipTree(grammarConfig.singleFile) }) { include("Rell.g4") }
        into(versionedGrammarDir)
    }

    val outputRoot = layout.buildDirectory.dir("generated-src/antlr/$suffix")
    val generate = tasks.register<AntlrTask>("generateRellGrammar${suffix.replaceFirstChar { it.uppercase() }}") {
        group = "build setup"
        description = "Generates the Rell $version ANTLR parser for the versioned syntax annotator."
        dependsOn(extract)
        setSource(versionedGrammarDir)
        maxHeapSize = "1g"
        arguments = arguments + listOf("-package", "$antlrPackage.$suffix", "-no-listener", "-no-visitor")
        outputDirectory = outputRoot.get().dir("${antlrPackage.replace('.', '/')}/$suffix").asFile
    }

    sourceSets["main"].java.srcDir(outputRoot)
    generate
}

tasks.generateGrammarSource {
    dependsOn(extractRellGrammar)
    maxHeapSize = "1g"
    arguments = arguments + listOf("-package", antlrPackage, "-no-listener", "-no-visitor")

    outputDirectory = layout.buildDirectory
        .dir("generated-src/antlr/main/${antlrPackage.replace('.', '/')}")
        .get().asFile
}

// The ANTLR Gradle plugin only wires generation ahead of compileJava by default.
tasks.compileKotlin {
    dependsOn(tasks.generateGrammarSource, versionedGrammarRoots)
}

tasks.compileJava {
    dependsOn(versionedGrammarRoots)
}

// The Sentry source-context tasks read the source sets (which include the ANTLR output dirs)
// without declaring a dependency, tripping Gradle's implicit-dependency validation.
tasks.matching { it.name == "generateSentryBundleIdJava" || it.name == "sentryCollectSourcesJava" }.configureEach {
    dependsOn(tasks.generateGrammarSource, tasks.named("generateTestGrammarSource"), versionedGrammarRoots)
}

tasks.compileTestKotlin {
    dependsOn(tasks.named("generateTestGrammarSource"))
}

// The antlr plugin leaks the ANTLR tool (+ ST4, antlr2/3 runtimes) onto the compile/api classpath.
// We only need the tool for code generation, so detach it; the runtime is declared explicitly above.
configurations.api.get().let { api ->
    api.setExtendsFrom(api.extendsFrom.filterNot { it.name == "antlr" })
}

// Set the JVM language level used to build the project. Use Java 11 for 2020.3+, and Java 17 for 2022.2+.
kotlin.jvmToolchain(21)

// Don't generate DefaultImpls compatibility bridges: they surface as usages of deprecated
// platform interface defaults (e.g. ToolWindowFactory.isApplicable) in the plugin verifier.
kotlin.compilerOptions.jvmDefault = JvmDefaultMode.NO_COMPATIBILITY

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

intellijPlatform {
    pluginConfiguration {
        version = properties("pluginVersion")

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        description = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with(it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        }

        val changelog = project.changelog // local variable for configuration cache compatibility

        // Get the latest available change notes from the changelog file
        changeNotes = providers.gradleProperty("pluginVersion").map { pluginVersion ->
            with(changelog) {
                renderItem(
                        (get(pluginVersion))
                                .withHeader(false)
                                .withEmptySections(false),
                        Changelog.OutputType.HTML,
                )
            }
        }

        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
            untilBuild = providers.gradleProperty("pluginUntilBuild")
        }
    }

    if (environment("CERTIFICATE_CHAIN").isPresent) {
        signing {
            certificateChain = file(environment("CERTIFICATE_CHAIN")).readText()
            privateKey = file(environment("PRIVATE_KEY")).readText()
            password = environment("PRIVATE_KEY_PASSWORD")
        }
    }

    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
        // The pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels = providers.gradleProperty("pluginVersion").map { listOf(it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" }) }
    }

    pluginVerification {
        ides {
            recommended()
        }
    }
}

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin

changelog {
    groups.empty()
    repositoryUrl.set(properties("pluginRepositoryUrl"))
    header.set(provider { version.get() })
    headerParserRegex.set("(\\d\\.\\d+(.\\d+)?)".toRegex())
    keepUnreleasedSection.set(false)
}

kover.reports {
    total {
        xml {
            onCheck = true
        }
    }
}

val rellLanguageServerRuntime: Configuration = configurations.detachedConfiguration(
    dependencies.create("net.postchain.rell:rell-toolbox-language-server:${libs.versions.rell.get()}")
).apply {
    isTransitive = true
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class, Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class, Category.LIBRARY))
    }
    // The Sentry plugin's auto-install grafts sentry-jdbc (at this project's Sentry version) onto the
    // postgresql driver via a component-metadata rule, and those rule results stick around in Gradle's
    // metadata cache even with autoInstallation disabled. A mismatched module makes the Sentry SDK
    // refuse to start inside the language server, killing it on launch — keep the LSP classpath to
    // exactly what rell-toolbox declares.
    exclude(group = "io.sentry", module = "sentry-jdbc")
}

// Lockfiles for the on-demand language-server runtimes of older supported Rell versions: the
// plugin's RellLspRuntimeManager downloads exactly these artifacts at runtime, checksum-verified,
// instead of bundling every version into the distribution.
val olderRellLspRuntimes = supportedRellVersions.dropLast(1).map { version ->
    version to configurations.detachedConfiguration(
        dependencies.create("net.postchain.rell:rell-toolbox-language-server:$version")
    ).apply {
        isTransitive = true
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class, Usage.JAVA_RUNTIME))
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class, Category.LIBRARY))
        }
        // Same metadata-cache hazard as rellLanguageServerRuntime above.
        exclude(group = "io.sentry", module = "sentry-jdbc")
    }
}

val generateRellLspLockfiles by tasks.registering {
    group = "build setup"
    description = "Writes GAV + SHA-256 lockfiles for the downloadable Rell language-server runtimes."

    val outDir = layout.buildDirectory.dir("generated-resources/rell-lsp-lockfiles")
    val artifactsPerVersion = olderRellLspRuntimes.map { (version, cfg) ->
        version to cfg.incoming.artifacts.resolvedArtifacts.map { artifacts ->
            artifacts.mapNotNull { artifact ->
                val id = artifact.id.componentIdentifier as? ModuleComponentIdentifier ?: return@mapNotNull null
                Triple("${id.group}:${id.module}:${id.version}", artifact.file.name, artifact.file)
            }
        }
    }
    inputs.files(olderRellLspRuntimes.map { it.second })
    outputs.dir(outDir)

    doLast {
        // Recreate the lockfile dir so versions dropped from supportedRellVersions don't leave
        // stale lockfiles behind that processResources would pack into the plugin.
        val lockDir = outDir.get().dir("rell/lsp-lockfiles").asFile
        lockDir.deleteRecursively()
        lockDir.mkdirs()
        for ((version, provider) in artifactsPerVersion) {
            val lines = provider.get().sortedBy { it.second }.map { (gav, fileName, file) ->
                val digest = MessageDigest.getInstance("SHA-256")
                val sha256 = digest.digest(file.readBytes()).joinToString("") { byte -> "%02x".format(byte) }
                "$gav $fileName $sha256"
            }
            File(lockDir, "$version.lock").writeText(lines.joinToString("\n"))
        }
    }
}

sourceSets.main.get().resources.srcDir(generateRellLspLockfiles)

tasks {
    prepareSandbox {
        from(rellLanguageServerRuntime) {
            into("${properties("pluginName").get()}/language-server")
        }
    }
    runIde {
        // Pass specific JVM args only when a specific property is set
        if (project.hasProperty("useSocket")) {
            jvmArgs("-Drell.lsp.useSocket=true")
        }
    }
}

intellijPlatformTesting.runIde {
    register("runIdeForUiTests") {
        task {
            jvmArgumentProviders += CommandLineArgumentProvider {
                listOf(
                        "-Drobot-server.port=8082",
                        "-Dide.mac.message.dialogs.as.sheets=false",
                        "-Djb.privacy.policy.text=<!--999.999-->",
                        "-Djb.consents.confirmation.enabled=false",
                )
            }
        }

        plugins {
            robotServerPlugin()
        }
    }
}
