package net.postchain.rellide.jetbrains.chromia

import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.PsiTestUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import net.postchain.rellide.jetbrains.chromia.RellVersionResolution.Origin
import java.io.File

/**
 * Uses real-filesystem temp dirs (not the in-memory fixture) because the toolbox parser reads
 * `chromia.yml` from an on-disk [java.nio.file.Path]. The `content/` subdir of the temp root is
 * registered as a content root so the resolver's project-bounded walk applies to it.
 */
class RellVersionResolverTest : BasePlatformTestCase() {

    private lateinit var tempRoot: File
    private lateinit var contentRoot: File
    private lateinit var vContentRoot: VirtualFile
    private lateinit var resolver: RellVersionResolver

    override fun setUp() {
        super.setUp()
        tempRoot = FileUtil.createTempDirectory("rell-resolver", null)
        contentRoot = File(tempRoot, "content").apply { mkdirs() }
        vContentRoot = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(contentRoot)
            ?: error("VFS refresh failed for $contentRoot")
        PsiTestUtil.addContentRoot(myFixture.module, vContentRoot)
        resolver = RellVersionResolver.getInstance(project)
        resolver.dropCaches()
    }

    override fun tearDown() {
        try {
            PsiTestUtil.removeContentEntry(myFixture.module, vContentRoot)
            FileUtil.delete(tempRoot)
        } finally {
            super.tearDown()
        }
    }

    fun testNoConfigResolvesToNewestSupported() {
        val source = file("proj/src/main.rell")
        assertEquals(
            RellVersionResolution.Supported(RellVersionRegistry.max, Origin.NO_CONFIG, null),
            resolver.resolve(source),
        )
    }

    fun testDeclaredSupportedVersionsResolveExactly() {
        for (version in listOf("0.16.0", "0.16.1")) {
            val dir = "proj-$version"
            val config = file("$dir/chromia.yml", yml(version))
            val source = file("$dir/src/main.rell")
            assertEquals(
                RellVersionResolution.Supported(RellVersion.parse(version)!!, Origin.DECLARED, config),
                resolver.resolve(source),
            )
        }
    }

    fun testBelowFloorIsUnsupported() {
        for (version in listOf("0.15.4", "0.14.5", "0.13.0")) {
            val dir = "proj-$version"
            val config = file("$dir/chromia.yml", yml(version))
            val source = file("$dir/src/main.rell")
            assertEquals(
                RellVersionResolution.Unsupported(RellVersion.parse(version)!!, config),
                resolver.resolve(source),
            )
        }
    }

    fun testUnknownNewerVersionsClampToNewestSupported() {
        for (version in listOf("0.16.9", "0.17.0", "1.0.0")) {
            val dir = "proj-$version"
            val config = file("$dir/chromia.yml", yml(version))
            val source = file("$dir/src/main.rell")
            assertEquals(
                RellVersionResolution.Clamped(RellVersion.parse(version)!!, RellVersionRegistry.max, config),
                resolver.resolve(source),
            )
        }
    }

    fun testMissingOrBlankVersionKeyFallsBackToNewest() {
        val cases = mapOf(
            "empty-value" to "compile:\n  rellVersion:\n  source: src\n",
            "no-key" to "compile:\n  source: src\n",
            "no-compile-section" to "database:\n  schema: foo\n",
            "empty-file" to "",
        )
        for ((dir, content) in cases) {
            val config = file("$dir/chromia.yml", content)
            val source = file("$dir/src/main.rell")
            assertEquals(
                "case: $dir",
                RellVersionResolution.Supported(RellVersionRegistry.max, Origin.NO_VERSION_KEY, config),
                resolver.resolve(source),
            )
        }
    }

    fun testMalformedVersionStringFallsBackToNewest() {
        for ((dir, value) in mapOf("banana" to "banana", "two-part" to "\"0.16\"", "suffixed" to "\"0.16.1-rc1\"")) {
            val config = file("proj-$dir/chromia.yml", "compile:\n  rellVersion: $value\n")
            val source = file("proj-$dir/src/main.rell")
            assertEquals(
                "case: $dir",
                RellVersionResolution.Supported(RellVersionRegistry.max, Origin.MALFORMED_VERSION, config),
                resolver.resolve(source),
            )
        }
    }

    fun testUnparseableYamlFallsBackToNewest() {
        // A comment-only file counts as unparseable: Jackson throws "no content to map" for it,
        // so the toolchain (and therefore the resolver) treats it like malformed YAML. Only a
        // fully blank file gets the empty-model special case.
        val cases = mapOf(
            "broken" to "compile:\n\trellVersion: [unclosed\n  bad",
            "comment-only" to "# nothing here\n",
        )
        for ((dir, content) in cases) {
            val config = file("$dir/chromia.yml", content)
            val source = file("$dir/src/main.rell")
            assertEquals(
                "case: $dir",
                RellVersionResolution.Supported(RellVersionRegistry.max, Origin.UNREADABLE_CONFIG, config),
                resolver.resolve(source),
            )
        }
    }

    // The plugin classpath deliberately excludes rell-base (see build.gradle.kts): parsing a config
    // with a libs section constructs RellLibraryModel, whose `rid` field type comes from the
    // excluded jar. This guards the extract-and-discard contract in RellVersionResolver.readConfig.
    fun testLibsSectionParsesWithoutExcludedClasses() {
        val config = file(
            "with-libs/chromia.yml",
            """
            compile:
              rellVersion: "0.16.0"
              source: src
            libs:
              ft4:
                registry: https://gitlab.com/chromaway/ft4-lib.git
                tagOrBranch: v1.1.0
                path: rell/src/lib/ft4
                version: "1.1"
              gamma:
            """.trimIndent(),
        )
        val source = file("with-libs/src/main.rell")
        assertEquals(
            RellVersionResolution.Supported(RellVersion(0, 16, 0), Origin.DECLARED, config),
            resolver.resolve(source),
        )
    }

    fun testNearestEnclosingConfigWins() {
        val outer = file("nested/chromia.yml", yml("0.16.1"))
        val inner = file("nested/sub/chromia.yml", yml("0.16.0"))
        val innerSource = file("nested/sub/src/main.rell")
        val outerSource = file("nested/src/main.rell")

        assertEquals(
            RellVersionResolution.Supported(RellVersion(0, 16, 0), Origin.DECLARED, inner),
            resolver.resolve(innerSource),
        )
        assertEquals(
            RellVersionResolution.Supported(RellVersion(0, 16, 1), Origin.DECLARED, outer),
            resolver.resolve(outerSource),
        )
    }

    fun testRellSubdirLayoutResolvesFromItsOwnConfig() {
        val config = file("layout/rell/chromia.yml", yml("0.16.0"))
        val source = file("layout/rell/src/main.rell")
        assertEquals(
            RellVersionResolution.Supported(RellVersion(0, 16, 0), Origin.DECLARED, config),
            resolver.resolve(source),
        )
    }

    fun testConfigAboveContentRootIsIgnored() {
        externalFile("chromia.yml", yml("0.14.5"))
        val source = file("proj/src/main.rell")
        assertEquals(
            RellVersionResolution.Supported(RellVersionRegistry.max, Origin.NO_CONFIG, null),
            resolver.resolve(source),
        )
    }

    fun testFileOutsideProjectContentGetsNoConfig() {
        externalFile("outside/chromia.yml", yml("0.14.5"))
        val source = externalFile("outside/src/main.rell")
        assertEquals(
            RellVersionResolution.Supported(RellVersionRegistry.max, Origin.NO_CONFIG, null),
            resolver.resolve(source),
        )
    }

    fun testConfigEditInvalidatesCacheViaVfsListener() {
        val config = file("edited/chromia.yml", yml("0.16.0"))
        val source = file("edited/src/main.rell")
        assertEquals(RellVersion(0, 16, 0), resolver.resolve(source).effectiveVersion)

        runWriteAction { VfsUtil.saveText(config, yml("0.16.1")) }

        assertEquals(RellVersion(0, 16, 1), resolver.resolve(source).effectiveVersion)
    }

    fun testNonVersionConfigEditKeepsCacheFresh() {
        val config = file("libs-edit/chromia.yml", yml("0.16.0"))
        val source = file("libs-edit/src/main.rell")
        assertEquals(RellVersion(0, 16, 0), resolver.resolve(source).effectiveVersion)

        runWriteAction {
            VfsUtil.saveText(config, yml("0.16.0") + "libs:\n  ft4:\n    version: \"1.1\"\n")
        }

        assertEquals(RellVersion(0, 16, 0), resolver.resolve(source).effectiveVersion)
    }

    fun testRefreshConfigReportsOnlyRealVersionDeltas() {
        val config = file("refresh/chromia.yml", yml("0.16.0"))
        val source = file("refresh/src/main.rell")

        assertFalse("Uncached config has no dependents", resolver.refreshConfig(config))

        resolver.resolve(source)
        File(contentRoot, "refresh/chromia.yml").writeText(yml("0.16.0") + "libs:\n  ft4:\n    version: \"1.1\"\n")
        assertFalse("Same declared version is not a delta", resolver.refreshConfig(config))

        File(contentRoot, "refresh/chromia.yml").writeText(yml("0.16.1"))
        assertTrue("Declared version change is a delta", resolver.refreshConfig(config))
        assertEquals(RellVersion(0, 16, 1), resolver.resolve(source).effectiveVersion)
    }

    // Directory renames fire a single VFS event for the directory only; the cache must not keep
    // serving the old project's config for a path another project has been renamed onto.
    fun testDirectoryRenameDoesNotServeStaleConfig() {
        file("proj/chromia.yml", yml("0.16.1"))
        val projSource = file("proj/src/main.rell")
        assertEquals(RellVersion(0, 16, 1), resolver.resolve(projSource).effectiveVersion)

        file("proj2/chromia.yml", yml("0.15.0"))
        val proj2Source = file("proj2/src/main.rell")

        runWriteAction {
            vContentRoot.findChild("proj")!!.rename(this, "proj-old")
            vContentRoot.findChild("proj2")!!.rename(this, "proj")
        }

        val resolution = resolver.resolve(proj2Source)
        assertEquals(
            RellVersionResolution.Unsupported(RellVersion(0, 15, 0), resolution.configFile!!),
            resolution,
        )
        assertEquals("chromia.yml under the renamed directory", "proj", resolution.configFile!!.parent.name)
    }

    private fun yml(version: String) = "compile:\n  rellVersion: \"$version\"\n  source: src\n"

    private fun file(relPath: String, content: String = ""): VirtualFile =
        createFile(File(contentRoot, relPath), content)

    /** A file in the temp dir but outside the registered content root. */
    private fun externalFile(relPath: String, content: String = ""): VirtualFile =
        createFile(File(tempRoot, relPath), content)

    private fun createFile(f: File, content: String): VirtualFile {
        f.parentFile.mkdirs()
        f.writeText(content)
        return LocalFileSystem.getInstance().refreshAndFindFileByIoFile(f)
            ?: error("VFS refresh failed for $f")
    }
}
