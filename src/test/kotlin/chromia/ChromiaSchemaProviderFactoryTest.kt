package net.postchain.rellide.jetbrains.chromia

import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.PsiTestUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider
import com.jetbrains.jsonSchema.extension.SchemaType
import com.jetbrains.jsonSchema.impl.JsonSchemaVersion
import java.io.File

/**
 * Real-filesystem temp dirs for the same reason as [RellVersionResolverTest]: qualification runs
 * through the toolbox parser, which reads an on-disk path.
 *
 * [JsonSchemaFileProvider.getSchemaFile] is deliberately never called — it resolves the schema over
 * HTTP, which a unit test has no business doing.
 */
class ChromiaSchemaProviderFactoryTest : BasePlatformTestCase() {

    private lateinit var contentRoot: File
    private lateinit var provider: JsonSchemaFileProvider

    override fun setUp() {
        super.setUp()
        contentRoot = File(FileUtil.createTempDirectory("rell-schema", null), "content").apply { mkdirs() }
        val vContentRoot = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(contentRoot)
            ?: error("VFS refresh failed for $contentRoot")
        PsiTestUtil.addContentRoot(myFixture.module, vContentRoot)
        RellVersionResolver.getInstance(project).dropCaches()
        provider = ChromiaSchemaProviderFactory().getProviders(project).single()
    }

    fun testDefaultNameGetsTheSchema() {
        assertTrue(
            "chromia.yml is a settings file by name alone",
            provider.isAvailable(file("chromia.yml", settingsYml())),
        )
    }

    fun testAlternateNameWithBlockchainsGetsTheSchema() {
        assertTrue(
            "A -s/--settings file must validate like chromia.yml",
            provider.isAvailable(file("atbash-dev.yml", settingsYml())),
        )
    }

    fun testUnrelatedYamlIsLeftAlone() {
        assertFalse(
            "Without a top-level blockchains section a .yml is not a settings file",
            provider.isAvailable(file("qodana.yml", "version: 1.0\nprofile:\n  name: qodana.starter\n")),
        )
    }

    fun testNestedBlockchainsKeyDoesNotQualify() {
        assertFalse(
            "The blockchains gate is anchored to column 0",
            provider.isAvailable(file("ci.yml", "job:\n  blockchains:\n    x: y\n")),
        )
    }

    fun testYamlExtensionIsNotASettingsFile() {
        assertFalse(
            "chr only ever reads .yml, so .yaml gets nothing from this provider",
            provider.isAvailable(file("atbash-dev.yaml", settingsYml())),
        )
    }

    fun testNonYamlFileIsNotConsidered() {
        assertFalse(provider.isAvailable(file("main.rell", "module;")))
    }

    fun testServesTheCatalogSchemaRemotely() {
        assertEquals("Chromia Model", provider.name)
        assertEquals(SchemaType.remoteSchema, provider.schemaType)
        assertEquals(JsonSchemaVersion.SCHEMA_2020_12, provider.schemaVersion)
        assertEquals(
            "Must stay the URL the JSON Schema Store catalog points at",
            "https://gitlab.com/chromaway/core-tools/chromia-cli-tools/-/raw/dev/" +
                    "chromia-build-tools/src/main/resources/chromia-model-schema.json",
            provider.remoteSource,
        )
    }

    private fun settingsYml() = "blockchains:\n  my_chain:\n    module: main\ncompile:\n  source: src\n"

    private fun file(relPath: String, content: String): VirtualFile {
        val f = File(contentRoot, relPath)
        f.parentFile.mkdirs()
        f.writeText(content)
        return LocalFileSystem.getInstance().refreshAndFindFileByIoFile(f)
            ?: error("VFS refresh failed for $f")
    }
}
