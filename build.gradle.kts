import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

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
    // Generates a JVM (Java, Kotlin, etc.) source bundle and uploads your source code to Sentry.
    // This enables source context, allowing you to see your source
    // code as part of your stack traces in Sentry.
    includeSourceContext = sentryAuthToken != null
    org = "chromaway-ab-za"
    projectName = "rell-jetbrains"
    authToken = System.getenv("SENTRY_AUTH_TOKEN") ?: ""
}

// Rell's ANTLR grammar (Rell.g4) ships in the `frontend` sources jar; we extract it at build time so
// the editor parser always tracks the `rell` version in libs.versions.toml — no vendored grammar copy.
val rellGrammar: Configuration by configurations.creating { isTransitive = false }

dependencies {
    rellGrammar("net.postchain.rell:frontend:${libs.versions.rell.get()}:sources@jar")
    antlr(libs.antlr)
    implementation(libs.antlr.runtime)

    implementation(libs.sentry)
    testImplementation(libs.junit)
    testImplementation(libs.jackson.kotlin)
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
    from({ zipTree(rellGrammar.singleFile) }) { include("Rell.g4") }
    into(grammarDir)
}

sourceSets["main"].extensions.getByName<SourceDirectorySet>("antlr")
    .setSrcDirs(listOf(grammarDir.get().asFile))

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
    dependsOn(tasks.generateGrammarSource)
}
tasks.named("compileTestKotlin") {
    dependsOn(tasks.named("generateTestGrammarSource"))
}

// The antlr plugin leaks the ANTLR tool (+ ST4, antlr2/3 runtimes) onto the compile/api classpath.
// We only need the tool for code generation, so detach it; the runtime is declared explicitly above.
configurations.findByName("api")?.let { api ->
    api.setExtendsFrom(api.extendsFrom.filterNot { it.name == "antlr" })
}

// Set the JVM language level used to build the project. Use Java 11 for 2020.3+, and Java 17 for 2022.2+.
kotlin.jvmToolchain(21)
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
}

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

