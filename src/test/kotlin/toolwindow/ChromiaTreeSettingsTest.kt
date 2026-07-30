package net.postchain.rellide.jetbrains.toolwindow

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import net.postchain.rellide.jetbrains.toolwindow.tree.ChromiaNodeType
import net.postchain.rellide.jetbrains.toolwindow.tree.ChromiaTreeNode

class ChromiaTreeSettingsTest : BasePlatformTestCase() {
    fun testSettingsFileIsInsertedBeforeParameters() {
        val node = commandNode("chr build").apply {
            settingsFile = "atbash_dev.yml"
            parameters = "--blockchain my_chain"
        }
        assertEquals("chr build --settings atbash_dev.yml --blockchain my_chain", node.getFullCommand())
    }

    fun testCommandWithoutSettingsFileIsUnchanged() {
        assertEquals("chr build", commandNode("chr build").getFullCommand())
        assertEquals(
            "chr keygen --foo",
            commandNode("chr keygen").apply { parameters = "--foo" }.getFullCommand(),
        )
    }

    // The parameters store is keyed by the bare command, so stamping --settings must not leak into
    // the key or every project would share another project's saved parameters.
    fun testSettingsFileDoesNotChangeTheCommandIdentity() {
        val node = commandNode("chr test").apply { settingsFile = "atbash.yml" }
        assertEquals("chr test", node.command)
    }

    fun testSettingsFileNodeRendersActiveMarker() {
        val active = ChromiaTreeNode(displayName = "atbash.yml", nodeType = ChromiaNodeType.SETTINGS_FILE).apply {
            settingsFile = "atbash.yml"
            isActiveSettingsFile = true
        }
        assertTrue(active.isActiveSettingsFile)
        assertEquals("atbash.yml", active.getDisplayText())
    }

    private fun commandNode(command: String) =
        ChromiaTreeNode(displayName = command, nodeType = ChromiaNodeType.COMMAND, command = command)
}
