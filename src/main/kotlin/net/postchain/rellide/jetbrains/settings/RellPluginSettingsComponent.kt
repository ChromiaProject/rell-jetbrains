package net.postchain.rellide.jetbrains.settings

import com.intellij.ui.components.JBCheckBox
import com.intellij.util.ui.FormBuilder
import javax.swing.JPanel


/**
 * Supports creating and managing a [JPanel] for the Settings Dialog.
 */
class RellPluginSettingsComponent {
    private var settingsPanel: JPanel? = null
    private val indexCaching = JBCheckBox("Enable/disable caching of Rell project index. (Restart required)")

    init {
        settingsPanel = FormBuilder.createFormBuilder()
            .addComponent(indexCaching, 1)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    fun getPanel() = settingsPanel

    fun getPreferredFocusedComponent() = indexCaching

    var indexCachingState: Boolean
        get() = indexCaching.isSelected
        set(newValue) {
            indexCaching.isSelected = newValue
        }
}
