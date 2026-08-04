package net.postchain.rellide.jetbrains.projectview

import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.testFramework.PsiTestUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import net.postchain.rellide.jetbrains.chromia.RellVersionResolver
import java.io.File
import javax.swing.Icon

/**
 * Directory decoration end to end: source-root resolution, icon, and module-name label, over the
 * two layouts that exist in practice — a declared `compile.source: rell/src` (ft4-lib) and the
 * default chain landing on `src` (Atbash-Dashboard/contracts).
 *
 * Real-filesystem temp dirs, like [net.postchain.rellide.jetbrains.chromia.RellVersionResolverTest]:
 * the toolbox parser reads settings files from an on-disk path.
 */
class RellProjectViewDecorationTest : BasePlatformTestCase() {

    private lateinit var tempRoot: File
    private lateinit var contentRoot: File
    private lateinit var vContentRoot: VirtualFile

    override fun setUp() {
        super.setUp()
        tempRoot = FileUtil.createTempDirectory("rell-projectview", null)
        contentRoot = File(tempRoot, "content").apply { mkdirs() }
        vContentRoot = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(contentRoot)
            ?: error("VFS refresh failed for $contentRoot")
        PsiTestUtil.addContentRoot(myFixture.module, vContentRoot)
        RellVersionResolver.getInstance(project).dropCaches()
    }

    override fun tearDown() {
        try {
            PsiTestUtil.removeContentEntry(myFixture.module, vContentRoot)
            FileUtil.delete(tempRoot)
        } finally {
            super.tearDown()
        }
    }

    fun testDefaultLayoutMarksSrcAsSourceRootAndNestsModuleNames() {
        file("contracts/chromia.yml", settingsYml(source = null))
        file("contracts/src/main/core/module.rell", "module;\n")

        assertEquals(AllIcons.Modules.SourceRoot, iconOf(dir("contracts/src")))
        assertEquals(AllIcons.Nodes.Package, iconOf(dir("contracts/src/main")))
        assertEquals(AllIcons.Nodes.Package, iconOf(dir("contracts/src/main/core")))

        assertNull("The source root labels itself with its icon, not a module name", labelOf(dir("contracts/src")))
        assertEquals("main", labelOf(dir("contracts/src/main")))
        assertEquals("main.core", labelOf(dir("contracts/src/main/core")))
    }

    fun testDeclaredSourceHonoured() {
        file("lib/chromia.yml", settingsYml(source = "rell/src"))
        file("lib/rell/src/lib/ft4/module.rell", "module;\n")

        assertEquals(AllIcons.Modules.SourceRoot, iconOf(dir("lib/rell/src")))
        assertEquals("lib.ft4", labelOf(dir("lib/rell/src/lib/ft4")))

        assertNull("`rell` is on the way to the source root, not inside it", iconOf(dir("lib/rell")))
        assertNull(labelOf(dir("lib/rell")))
    }

    fun testDirectoriesOutsideTheSourceTreeAreLeftAlone() {
        file("contracts/chromia.yml", settingsYml(source = null))
        file("contracts/src/main/module.rell", "module;\n")
        file("contracts/build/output.txt")

        assertNull(iconOf(dir("contracts")))
        assertNull(iconOf(dir("contracts/build")))
        assertNull(labelOf(dir("contracts/build")))
    }

    fun testDirectoryRellCannotNameIsNotDecorated() {
        file("contracts/chromia.yml", settingsYml(source = null))
        file("contracts/src/my-scripts/notes.rell")

        assertNull("`my-scripts` is not a valid Rell name", iconOf(dir("contracts/src/my-scripts")))
        assertNull(labelOf(dir("contracts/src/my-scripts")))
    }

    private fun iconOf(directory: VirtualFile): Icon? {
        val psi = PsiManager.getInstance(project).findDirectory(directory) ?: error("no PSI for $directory")
        return RellDirectoryIconProvider().getIcon(psi, 0)
    }

    private fun labelOf(directory: VirtualFile): String? {
        val psi = PsiManager.getInstance(project).findDirectory(directory) ?: error("no PSI for $directory")
        val data = PresentationData()
        RellModulePathDecorator().decorate(PsiDirectoryNode(project, psi, null), data)
        return data.locationString
    }

    private fun settingsYml(source: String?): String =
        "blockchains:\n  my_chain:\n    module: main\ncompile:\n  rellVersion: \"0.16.4\"\n" +
                (source?.let { "  source: $it\n" } ?: "")

    private fun dir(relPath: String): VirtualFile =
        LocalFileSystem.getInstance().refreshAndFindFileByIoFile(File(contentRoot, relPath))
            ?: error("VFS refresh failed for $relPath")

    private fun file(relPath: String, content: String = ""): VirtualFile {
        val f = File(contentRoot, relPath)
        f.parentFile.mkdirs()
        f.writeText(content)
        return LocalFileSystem.getInstance().refreshAndFindFileByIoFile(f)
            ?: error("VFS refresh failed for $f")
    }
}
