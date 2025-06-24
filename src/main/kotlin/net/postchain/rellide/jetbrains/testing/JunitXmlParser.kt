package net.postchain.rellide.jetbrains.testing

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.io.InputStream
import java.time.Duration
import javax.xml.parsers.DocumentBuilderFactory

class JunitXmlParser {
    
    fun parseTestReport(xmlFile: File): JunitTestReports {
        return parseTestReport(xmlFile.inputStream())
    }
    
    fun parseTestReport(xmlContent: String): JunitTestReports {
        return parseTestReport(xmlContent.byteInputStream())
    }
    
    fun parseTestReport(inputStream: InputStream): JunitTestReports {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val document = builder.parse(inputStream)
        document.documentElement.normalize()
        
        return when (document.documentElement.nodeName) {
            "testsuites" -> parseTestSuites(document)
            "testsuite" -> JunitTestReports(listOf(parseTestSuite(document.documentElement)))
            else -> throw IllegalArgumentException("Unknown root element: ${document.documentElement.nodeName}")
        }
    }
    
    private fun parseTestSuites(document: Document): JunitTestReports {
        val testSuitesElement = document.documentElement
        val testSuiteNodes = testSuitesElement.getElementsByTagName("testsuite")
        val testSuites = mutableListOf<JunitTestSuite>()
        
        for (i in 0 until testSuiteNodes.length) {
            val node = testSuiteNodes.item(i)
            if (node.nodeType == Node.ELEMENT_NODE) {
                testSuites.add(parseTestSuite(node as Element))
            }
        }
        
        return JunitTestReports(testSuites)
    }
    
    private fun parseTestSuite(element: Element): JunitTestSuite {
        val name = element.getAttribute("name") ?: ""
        val tests = element.getAttribute("tests").toIntOrNull() ?: 0
        val failures = element.getAttribute("failures").toIntOrNull() ?: 0
        val errors = element.getAttribute("errors").toIntOrNull() ?: 0
        val skipped = element.getAttribute("skipped").toIntOrNull() ?: 0
        val time = parseDuration(element.getAttribute("time"))
        val timestamp = element.getAttribute("timestamp").takeIf { it.isNotBlank() }
        val hostname = element.getAttribute("hostname").takeIf { it.isNotBlank() }
        
        val properties = parseProperties(element)
        val testCases = parseTestCases(element)
        val systemOut = getTextContent(element, "system-out")
        val systemErr = getTextContent(element, "system-err")
        
        return JunitTestSuite(
            name = name,
            tests = tests,
            failures = failures,
            errors = errors,
            skipped = skipped,
            time = time,
            timestamp = timestamp,
            hostname = hostname,
            testCases = testCases,
            properties = properties,
            systemOut = systemOut,
            systemErr = systemErr
        )
    }
    
    private fun parseTestCases(testSuiteElement: Element): List<JunitTestCase> {
        val testCaseNodes = testSuiteElement.getElementsByTagName("testcase")
        val testCases = mutableListOf<JunitTestCase>()
        
        for (i in 0 until testCaseNodes.length) {
            val node = testCaseNodes.item(i)
            if (node.nodeType == Node.ELEMENT_NODE) {
                testCases.add(parseTestCase(node as Element))
            }
        }
        
        return testCases
    }
    
    private fun parseTestCase(element: Element): JunitTestCase {
        val name = element.getAttribute("name") ?: ""
        val classname = element.getAttribute("classname") ?: ""
        val time = parseDuration(element.getAttribute("time"))
        
        val result = parseTestResult(element)
        val systemOut = getTextContent(element, "system-out")
        val systemErr = getTextContent(element, "system-err")
        
        return JunitTestCase(
            name = name,
            classname = classname,
            time = time,
            result = result,
            systemOut = systemOut,
            systemErr = systemErr
        )
    }
    
    private fun parseTestResult(testCaseElement: Element): JunitTestResult {
        val failureNode = testCaseElement.getElementsByTagName("failure").item(0)
        if (failureNode != null && failureNode.nodeType == Node.ELEMENT_NODE) {
            val failureElement = failureNode as Element
            return JunitTestResult.Failure(
                message = failureElement.getAttribute("message").takeIf { it.isNotBlank() },
                type = failureElement.getAttribute("type").takeIf { it.isNotBlank() },
                content = failureElement.textContent?.trim()?.takeIf { it.isNotBlank() }
            )
        }
        
        val errorNode = testCaseElement.getElementsByTagName("error").item(0)
        if (errorNode != null && errorNode.nodeType == Node.ELEMENT_NODE) {
            val errorElement = errorNode as Element
            return JunitTestResult.Error(
                message = errorElement.getAttribute("message").takeIf { it.isNotBlank() },
                type = errorElement.getAttribute("type").takeIf { it.isNotBlank() },
                content = errorElement.textContent?.trim()?.takeIf { it.isNotBlank() }
            )
        }
        
        val skippedNode = testCaseElement.getElementsByTagName("skipped").item(0)
        if (skippedNode != null && skippedNode.nodeType == Node.ELEMENT_NODE) {
            val skippedElement = skippedNode as Element
            return JunitTestResult.Skipped(
                message = skippedElement.getAttribute("message").takeIf { it.isNotBlank() }
                    ?: skippedElement.textContent?.trim()?.takeIf { it.isNotBlank() }
            )
        }
        
        return JunitTestResult.Success
    }
    
    private fun parseProperties(testSuiteElement: Element): Map<String, String> {
        val propertiesNode = testSuiteElement.getElementsByTagName("properties").item(0)
        if (propertiesNode == null || propertiesNode.nodeType != Node.ELEMENT_NODE) {
            return emptyMap()
        }
        
        val propertiesElement = propertiesNode as Element
        val propertyNodes = propertiesElement.getElementsByTagName("property")
        val properties = mutableMapOf<String, String>()
        
        for (i in 0 until propertyNodes.length) {
            val node = propertyNodes.item(i)
            if (node.nodeType == Node.ELEMENT_NODE) {
                val propertyElement = node as Element
                val name = propertyElement.getAttribute("name")
                val value = propertyElement.getAttribute("value")
                if (name.isNotBlank()) {
                    properties[name] = value
                }
            }
        }
        
        return properties
    }
    
    private fun getTextContent(parent: Element, tagName: String): String? {
        val node = parent.getElementsByTagName(tagName).item(0)
        return node?.textContent?.trim()?.takeIf { it.isNotBlank() }
    }
    
    private fun parseDuration(timeString: String): Duration {
        if (timeString.isBlank()) return Duration.ZERO
        
        return try {
            val seconds = timeString.toDouble()
            Duration.ofMillis((seconds * 1000).toLong())
        } catch (e: NumberFormatException) {
            Duration.ZERO
        }
    }
}


data class JunitTestSuite(
        val name: String,
        val tests: Int,
        val failures: Int,
        val errors: Int,
        val skipped: Int,
        val time: Duration,
        val timestamp: String?,
        val hostname: String?,
        val testCases: List<JunitTestCase>,
        val properties: Map<String, String> = emptyMap(),
        val systemOut: String? = null,
        val systemErr: String? = null
)

data class JunitTestCase(
        val name: String,
        val classname: String,
        val time: Duration,
        val result: JunitTestResult,
        val systemOut: String? = null,
        val systemErr: String? = null
)

sealed class JunitTestResult {
    object Success : JunitTestResult()

    data class Failure(
            val message: String?,
            val type: String?,
            val content: String?
    ) : JunitTestResult()

    data class Error(
            val message: String?,
            val type: String?,
            val content: String?
    ) : JunitTestResult()

    data class Skipped(
            val message: String?
    ) : JunitTestResult()
}

data class JunitTestReports(
        val testSuites: List<JunitTestSuite>
)