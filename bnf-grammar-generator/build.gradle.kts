plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

application {
    mainClass.set("net.postchain.rellide.jetbrains.grammar.BnfGrammarGenerator")
}

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
}

dependencies {
    implementation(libs.rell.base)
}

group = "net.postchain.rellide.jetbrains"
version = "0.0.1"

kotlin.jvmToolchain(21)
