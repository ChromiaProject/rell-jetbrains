package net.postchain.rellide.jetbrains.testing

import com.intellij.execution.PsiLocation
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RellTestLocatorTest : BasePlatformTestCase() {

    private lateinit var locator: RellTestLocator

    override fun setUp() {
        super.setUp()
        locator = RellTestLocator()
    }

    fun testWrongProtocolReturnsEmptyList() {
        val result = locator.getLocation(
            "wrong_protocol",
            "some/path.rell",
            project,
            GlobalSearchScope.projectScope(project)
        )
        assertEmpty(result)
    }

    fun testAbsolutePathFindsFile() {
        val psiFile = myFixture.configureByText("test_example.rell", "@test module;\nfunction test_foo() {}")
        val absolutePath = psiFile.virtualFile.path

        val result = locator.getLocation(
            RellTestLocator.PROTOCOL,
            absolutePath,
            project,
            GlobalSearchScope.projectScope(project)
        )

        assertEquals(1, result.size)
        assertInstanceOf(result[0], PsiLocation::class.java)
        assertEquals(psiFile.virtualFile, result[0].virtualFile)
    }

    fun testAbsolutePathWithLineNumberAndFunctionName() {
        val psiFile = myFixture.configureByText("test_with_info.rell", "@test module;\nfunction test_bar() {}")
        val absolutePath = psiFile.virtualFile.path

        val result = locator.getLocation(
            RellTestLocator.PROTOCOL,
            "$absolutePath:2:test_bar",
            project,
            GlobalSearchScope.projectScope(project)
        )

        assertEquals(1, result.size)
        assertEquals(psiFile.virtualFile, result[0].virtualFile)
    }

    fun testFallbackToFilenameSearch() {
        val psiFile = myFixture.configureByText("my_test_module.rell", "@test module;\nfunction test_baz() {}")

        val result = locator.getLocation(
            RellTestLocator.PROTOCOL,
            "/nonexistent/path/my_test_module.rell",
            project,
            GlobalSearchScope.projectScope(project)
        )

        assertFalse("Should find file by filename fallback", result.isEmpty())
        assertEquals(psiFile.virtualFile.name, result[0].virtualFile!!.name)
    }

    fun testEmptyPathReturnsEmptyList() {
        val result = locator.getLocation(
            RellTestLocator.PROTOCOL,
            "",
            project,
            GlobalSearchScope.projectScope(project)
        )

        assertEmpty(result)
    }

    fun testProtocolConstant() {
        assertEquals("rell_test", RellTestLocator.PROTOCOL)
    }
}
