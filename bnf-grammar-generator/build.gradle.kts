fun properties(key: String) = providers.gradleProperty(key)

val rellVersion = properties("rellVersion").get()

plugins {
    // Java support
    id("java")
    // Kotlin support
    id("org.jetbrains.kotlin.jvm") version "1.8.10"
    // Make jar runnable
    id("application")
}

application {
    mainClass.set("net.postchain.rellide.jetbrains.grammar.BnfGrammarGenerator")
}

dependencies {
    implementation(group="net.postchain.rell", name="rell", version=rellVersion, ext="pom")
    implementation("net.postchain.rell:rell-base:$rellVersion")
    implementation("net.postchain.rell:rell-tools:$rellVersion")

    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.0.1")
}

group = "net.postchain.rellide.jetbrains"
version = "0.0.1"

// Configure project's dependencies
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
    maven {
        name = "bintray"
        url = uri("https://jcenter.bintray.com")
    }
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
}

kotlin {
    jvmToolchain(17)
}