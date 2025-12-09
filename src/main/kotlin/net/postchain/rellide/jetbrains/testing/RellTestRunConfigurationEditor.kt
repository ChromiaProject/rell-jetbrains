package net.postchain.rellide.jetbrains.testing

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * Settings editor for Rell test run configuration.
 * Provides UI for configuring test execution parameters.
 */
class RellTestRunConfigurationEditor(private val project: Project) : SettingsEditor<RellTestRunConfiguration>() {

    private val testScopeComboBox = ComboBox(TestScope.values())
    private val testModuleField = JBTextField()
    private val testBlockchainField = JBTextField()
    private val testPatternField = JBTextField()
    private val workingDirectoryField = TextFieldWithBrowseButton()
    private val chrExecutableField = JBTextField()
    private val additionalArgumentsField = JBTextField()

    private val testModuleLabel = JBLabel("Test module:")
    private val testBlockchainLabel = JBLabel("Test blockchain:")
    private val testPatternLabel = JBLabel("Test pattern:")

    init {
        setupUI()
    }

    private fun setupUI() {
        workingDirectoryField.addBrowseFolderListener(
                project,
                FileChooserDescriptorFactory.createSingleFolderDescriptor()
                        .withTitle("Select Working Directory")
                        .withDescription("Choose the working directory for test execution")
        )

        testScopeComboBox.addActionListener {
            updateFieldVisibility()
        }

        updateFieldVisibility()
    }

    private fun updateFieldVisibility() {
        val selectedScope = testScopeComboBox.selectedItem as TestScope

        testModuleLabel.isVisible = selectedScope == TestScope.MODULE
        testModuleField.isVisible = selectedScope == TestScope.MODULE

        testBlockchainLabel.isVisible = selectedScope == TestScope.BLOCKCHAIN
        testBlockchainField.isVisible = selectedScope == TestScope.BLOCKCHAIN

        testPatternLabel.isVisible = selectedScope == TestScope.TEST_PATTERN
        testPatternField.isVisible = selectedScope == TestScope.TEST_PATTERN
    }

    override fun createEditor(): JComponent {
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Test scope:", testScopeComboBox)
                .addLabeledComponent(testModuleLabel, testModuleField)
                .addLabeledComponent(testBlockchainLabel, testBlockchainField)
                .addLabeledComponent(testPatternLabel, testPatternField)
                .addSeparator()
                .addLabeledComponent("Working directory:", workingDirectoryField)
                .addLabeledComponent("Chromia CLI executable:", chrExecutableField)
                .addLabeledComponent("Additional arguments:", additionalArgumentsField)
                .addComponentFillVertically(JPanel(), 0)
                .panel
    }

    override fun resetEditorFrom(configuration: RellTestRunConfiguration) {
        val options = configuration.options

        testScopeComboBox.selectedItem = options.getTestScope()
        testModuleField.text = options.getTestModule() ?: ""
        testBlockchainField.text = options.getTestBlockchain() ?: ""
        testPatternField.text = options.getTestPattern() ?: ""
        workingDirectoryField.text = options.getWorkingDirectory() ?: ""
        chrExecutableField.text = options.getChrExecutable() ?: ""
        additionalArgumentsField.text = options.getAdditionalArguments() ?: ""

        updateFieldVisibility()
    }

    override fun applyEditorTo(configuration: RellTestRunConfiguration) {
        val options = configuration.options

        options.setTestScope(testScopeComboBox.selectedItem as TestScope)
        options.setTestModule(testModuleField.text.takeIf { it.isNotBlank() })
        options.setTestBlockchain(testBlockchainField.text.takeIf { it.isNotBlank() })
        options.setTestPattern(testPatternField.text.takeIf { it.isNotBlank() })
        options.setWorkingDirectory(workingDirectoryField.text.takeIf { it.isNotBlank() })
        options.setChrExecutable(chrExecutableField.text.takeIf { it.isNotBlank() })
        options.setAdditionalArguments(additionalArgumentsField.text.takeIf { it.isNotBlank() })
    }
} 