import org.gradle.kotlin.dsl.intellijPlatform
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

val rellTestCasesConfiguration by configurations.creating


plugins {
    // Java support
    id("java")
    // Kotlin support
    id("org.jetbrains.kotlin.jvm") version "2.2.0"
    // Gradle IntelliJ Plugin
    id("org.jetbrains.intellij.platform") version "2.10.4"
    // Gradle Changelog Plugin
    id("org.jetbrains.changelog") version "2.2.1"
    // Gradle Qodana Plugin
    id("org.jetbrains.qodana") version "2024.1.9"
    // Gradle Kover Plugin
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
    // Sentry Plugin
    id("io.sentry.jvm.gradle") version "5.8.0"
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

dependencies {
    rellTestCasesConfiguration(group = "net.postchain.rell", name = "rell-api-gtx", version = properties("rellVersion").get(), classifier = "rell-test-cases", ext = "zip")
    implementation("io.sentry:sentry:8.29.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")
    testImplementation("org.opentest4j:opentest4j:1.3.0")
    intellijPlatform {
        plugins(providers.gradleProperty("platformPlugins").map { it.split(',') })
        bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(',') })
        create(properties("platformType"), properties("platformVersion"))
        pluginVerifier()
        zipSigner()
        testFramework(TestFrameworkType.Platform)
    }
}

val testCasesDir = layout.buildDirectory.dir("rell-test-cases")

val copyTestCases by tasks.registering(Copy::class) {
    from({ zipTree(rellTestCasesConfiguration.singleFile) })
    into(testCasesDir.map { it.dir("test-cases") })
}

tasks.compileTestKotlin {
    dependsOn(copyTestCases)
}

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()

// Configure project's dependencies
repositories {
    mavenCentral()
    maven {
        name = "etherjar"
        url = uri("https://maven.emrld.io")
    }
    maven {
        name = "Rell GitLab Registry"
        url = uri("https://gitlab.com/api/v4/projects/32802097/packages/maven")
    }
    maven {
        name = "Postchain GitLab Registry"
        url = uri("https://gitlab.com/api/v4/projects/32294340/packages/maven")
    }
    maven {
        name = "Chromia parent GitLab Registry"
        url = uri("https://gitlab.com/api/v4/projects/50818999/packages/maven")
    }
    // IntelliJ Platform Gradle Plugin Repositories Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-repositories-extension.html
    intellijPlatform {
        defaultRepositories()
    }
}

sourceSets["main"].java.srcDirs("src/main/gen")

// Set the JVM language level used to build the project. Use Java 11 for 2020.3+, and Java 17 for 2022.2+.
kotlin {
    jvmToolchain(21)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
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

kover {
    reports {
        total {
            xml {
                onCheck = true
            }
        }
    }
}

tasks {
    wrapper {
        gradleVersion = properties("gradleVersion").get()
    }
    prepareSandbox {
        doLast {
            copy {
                from("${project.projectDir}/language-server")
                into("${destinationDir.path}/${properties("pluginName").get()}/language-server")
            }
        }
    }
    runIde {
        // Pass specific JVM args only when a specific property is set
        if (project.hasProperty("useSocket")) {
            jvmArgs("-Drell.lsp.useSocket=true")
        }
    }
}

intellijPlatformTesting {
    runIde {
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
}