package net.postchain.rellide.jetbrains

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.intellij.testFramework.ParsingTestCase
import com.intellij.testFramework.ParsingTestUtil
import net.postchain.rellide.jetbrains.language.RellParserDefinition
import java.io.File

@JsonIgnoreProperties(ignoreUnknown = true)
data class RellTestCaseSnippet(val files: Map<String, String>, val parsing: Map<String, List<Map<String, String>>>)

class RellParsingTest : ParsingTestCase("", "rell", true, RellParserDefinition()) {

    override fun getTestDataPath() = "build/rell-test-cases/test-cases"

    fun testRellParser() {
        val snippetFiles = getSnippetFiles()
        val mapper = jacksonObjectMapper()
        snippetFiles.forEach { snippetFile ->
            val cases = mapper.readValue<List<RellTestCaseSnippet>>(snippetFile)
            cases.forEach(::validateTestCase)
        }
    }

    private fun validateTestCase(case: RellTestCaseSnippet) {
        for (file in case.files) {
            val isSuccessful = tryParsing(file.key, file.value)
            val expectedErrors = case.parsing[file.key] ?: listOf()

            assertTrue(isSuccessful == expectedErrors.isEmpty())
        }
    }

    private fun getSnippetFiles() = File(testDataPath).walk().filter {
        it.isFile && it.extension == "json"
    }.toList()

    private fun tryParsing(fileName: String, text: String): Boolean {
        return try {
            val parsedFile = parseFile(fileName, text)
            ParsingTestUtil.assertNoPsiErrorElements(parsedFile)
            true
        } catch (e: Throwable) {
            false
        }
    }
}