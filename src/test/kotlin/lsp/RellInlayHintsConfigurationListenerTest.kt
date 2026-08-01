package net.postchain.rellide.jetbrains.lsp

import com.intellij.codeInsight.hints.InlayHintsSettings
import com.intellij.codeInsight.hints.NoSettings
import com.intellij.codeInsight.hints.SettingsKey
import com.intellij.openapi.components.service
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import net.postchain.rellide.jetbrains.language.RellLanguage

/**
 * What the plugin tells the language server about inlay hints must track what the user actually
 * switched. The gate used to test `disabledHintProviderIds` for `"Rell.LSP.hints"`, an id no
 * provider registers under — ids are `"<languageId>.<keyId>"` and the platform's LSP provider uses
 * `SettingsKey("lsp.inlay.hints")` — so it answered `true` no matter what these tests do.
 */
@Suppress("UnstableApiUsage")
class RellInlayHintsConfigurationListenerTest : BasePlatformTestCase() {

    private val lspHintsKey = SettingsKey<NoSettings>("lsp.inlay.hints")

    private val sentFlags: Map<String, Boolean>
        get() = project.service<RellInlayHintsConfigurationListener>().getInlayHintsSettings()

    override fun tearDown() {
        try {
            val settings = InlayHintsSettings.instance()
            settings.setEnabledGlobally(true)
            settings.setHintsEnabledForLanguage(RellLanguage.INSTANCE, true)
            settings.changeHintTypeStatus(lspHintsKey, RellLanguage.INSTANCE, true)
        } finally {
            super.tearDown()
        }
    }

    fun testAllThreeFlagsMoveTogetherAndAreOnByDefault() {
        assertEquals(
            setOf("parameterHints", "variableTypeHints", "returnTypeHints"),
            sentFlags.keys,
        )
        assertTrue("Hints are on out of the box", sentFlags.values.all { it })
    }

    fun testGlobalToggleReachesTheServer() {
        InlayHintsSettings.instance().setEnabledGlobally(false)
        assertTrue("Disabling hints globally must be sent on", sentFlags.values.none { it })

        InlayHintsSettings.instance().setEnabledGlobally(true)
        assertTrue(sentFlags.values.all { it })
    }

    fun testPerLanguageToggleReachesTheServer() {
        InlayHintsSettings.instance().setHintsEnabledForLanguage(RellLanguage.INSTANCE, false)
        assertTrue("Disabling hints for Rell must be sent on", sentFlags.values.none { it })

        InlayHintsSettings.instance().setHintsEnabledForLanguage(RellLanguage.INSTANCE, true)
        assertTrue(sentFlags.values.all { it })
    }

    fun testProviderCheckboxReachesTheServer() {
        InlayHintsSettings.instance().changeHintTypeStatus(lspHintsKey, RellLanguage.INSTANCE, false)
        assertTrue("Unchecking the LSP hints provider must be sent on", sentFlags.values.none { it })

        InlayHintsSettings.instance().changeHintTypeStatus(lspHintsKey, RellLanguage.INSTANCE, true)
        assertTrue(sentFlags.values.all { it })
    }
}
