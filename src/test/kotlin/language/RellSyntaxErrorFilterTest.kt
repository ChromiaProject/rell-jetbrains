package net.postchain.rellide.jetbrains.language

import com.intellij.psi.PsiErrorElement
import com.intellij.psi.util.PsiTreeUtil
import net.postchain.rellide.jetbrains.chromia.RellVersionAwareTestCase

class RellSyntaxErrorFilterTest : RellVersionAwareTestCase() {
    private val filter = RellSyntaxErrorFilter()

    private fun firstPsiError(dir: String): PsiErrorElement {
        val psiFile = myFixture.addFileToProject("$dir/src/main.rell", "module;\n\nentity {\n\n}\n")
        return PsiTreeUtil.findChildOfType(psiFile, PsiErrorElement::class.java)
            ?: error("expected a PSI error element for the unnamed entity")
    }

    fun testPsiErrorsAreHiddenInFilesTheLanguageServerServes() {
        assertFalse(filter.shouldHighlightErrorElement(firstPsiError("f-served")))
    }

    fun testPsiErrorsStayVisibleBelowTheCompatibilityFloor() {
        myFixture.addFileToProject(
            "f-ceased/chromia.yml",
            "blockchains:\n  my_chain:\n    module: main\ncompile:\n  rellVersion: \"0.14.5\"\n",
        )
        assertTrue(filter.shouldHighlightErrorElement(firstPsiError("f-ceased")))
    }

    fun testNonRellPsiErrorsAreUntouched() {
        val psiFile = myFixture.addFileToProject("f-foreign/broken.json", "{")
        val error = PsiTreeUtil.findChildOfType(psiFile, PsiErrorElement::class.java)
            ?: error("expected a PSI error element in broken JSON")
        assertTrue(filter.shouldHighlightErrorElement(error))
    }
}
