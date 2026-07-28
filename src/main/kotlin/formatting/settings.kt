package net.postchain.rellide.jetbrains.formatting

import com.intellij.application.options.CodeStyleAbstractConfigurable
import com.intellij.application.options.IndentOptionsEditor
import com.intellij.application.options.SmartIndentOptionsEditor
import com.intellij.application.options.TabbedLanguageCodeStylePanel
import com.intellij.lang.Language
import com.intellij.psi.codeStyle.*
import net.postchain.rellide.jetbrains.language.RellLanguage

private val INDENT_SAMPLE: String = """
    operation initialize() {
        require(op_context.is_signer(chain_context.args.admin_pubkey));
    	initialize_system();
    }

    operation process(account_id: byte_array, auth_descriptor_id: byte_array, id: byte_array) {
    	val account = account @ {.id == account_id};
    	require_auth(account, auth_descriptor_id, list<text>());

    	activate(account, id);
    }
  """.trimIndent()

class RellCodeStyleSettingsProvider : CodeStyleSettingsProvider() {
    override fun createCustomSettings(settings: CodeStyleSettings) = RellCodeStyleSettings(settings)

    override fun getConfigurableDisplayName() = RellLanguage.INSTANCE.displayName

    override fun getLanguage(): Language = RellLanguage.INSTANCE

    override fun createConfigurable(settings: CodeStyleSettings, originalSettings: CodeStyleSettings) =
        object : CodeStyleAbstractConfigurable(settings, originalSettings, configurableDisplayName) {
            override fun createPanel(settings: CodeStyleSettings) = RellCodeStyleMainPanel(currentSettings, settings)
            override fun getHelpTopic() = null
        }

    private class RellCodeStyleMainPanel(currentSettings: CodeStyleSettings, settings: CodeStyleSettings) :
        TabbedLanguageCodeStylePanel(RellLanguage.INSTANCE, currentSettings, settings) {

        override fun initTabs(settings: CodeStyleSettings?) {
            addIndentOptionsTab(settings)
        }
    }
}

class RellCodeStyleSettings(container: CodeStyleSettings) : CustomCodeStyleSettings("RellCodeStyleSettings", container)

class RellLanguageCodeStyleSettingsProvider : LanguageCodeStyleSettingsProvider() {
    override fun getLanguage(): Language = RellLanguage.INSTANCE

    override fun getCodeSample(settingsType: SettingsType): String =
        when (settingsType) {
            SettingsType.INDENT_SETTINGS -> INDENT_SAMPLE
            else -> ""
        }

    override fun customizeDefaults(
        commonSettings: CommonCodeStyleSettings,
        indentOptions: CommonCodeStyleSettings.IndentOptions,
    ) {
    }

    override fun getIndentOptionsEditor(): IndentOptionsEditor = SmartIndentOptionsEditor()
}
