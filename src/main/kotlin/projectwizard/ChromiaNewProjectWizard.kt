@file:Suppress("UnstableApiUsage")

package net.postchain.rellide.jetbrains.projectwizard

import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.ide.wizard.*
import com.intellij.ide.wizard.NewProjectWizardBaseData.Companion.baseData
import com.intellij.ide.wizard.NewProjectWizardChainStep.Companion.nextStep
import com.intellij.openapi.project.Project
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.bindSelected
import net.postchain.rellide.jetbrains.language.RellIcons
import net.postchain.rellide.jetbrains.settings.RellPluginSettingsState
import java.nio.file.Path
import javax.swing.Icon

/**
 * The "Chromia" generator in File | New | Project. Collects the standard name/location/git
 * fields plus the `chr create-rell-dapp` options, then delegates generation to
 * [ChromiaDappGenerator].
 */
class ChromiaNewProjectWizard : GeneratorNewProjectWizard {
    override val id: String = "chromia"
    override val name: String = "Chromia"
    override val icon: Icon = RellIcons.CHROMIA_ICON_FILE
    override val description: String = "Create a Chromia dapp written in Rell from a Chromia CLI template."

    override fun createStep(context: WizardContext): NewProjectWizardStep =
        RootNewProjectWizardStep(context)
            .nextStep(::NewProjectWizardBaseStep)
            .nextStep(::GitNewProjectWizardStep)
            .nextStep(::ChromiaTemplateStep)
}

private class ChromiaTemplateStep(parent: NewProjectWizardStep) : AbstractNewProjectWizardStep(parent) {
    private val templateProperty = propertyGraph.property(ChromiaProjectTemplate.MINIMAL)
    private val devcontainerProperty = propertyGraph.property(false)

    override fun setupUI(builder: Panel) {
        builder.row("Template:") {
            val templateCell = comboBox(
                ChromiaProjectTemplate.entries,
                SimpleListCellRenderer.create("") { it.displayName },
            )
                .bindItem(templateProperty)
                .comment(templateProperty.get().description)
                .validationOnApply {
                    when {
                        baseData?.name.orEmpty().contains(' ') ->
                            error("Chromia project names cannot contain spaces")

                        RellPluginSettingsState.instance.buildChromiaCliCommandLine(emptyList()) == null ->
                            error(
                                "Chromia CLI is not configured and none of the standard locations " +
                                        "were found. Set it in Settings | Tools | Rell."
                            )

                        else -> null
                    }
                }
            templateProperty.afterChange { templateCell.comment?.text = it.description }
        }
        builder.row("") {
            checkBox("Set up dev container")
                .bindSelected(devcontainerProperty)
        }
    }

    override fun setupProject(project: Project) {
        val base = baseData ?: return
        ChromiaDappGenerator.generate(
            project = project,
            projectRoot = Path.of(base.path, base.name),
            template = templateProperty.get(),
            devcontainer = devcontainerProperty.get(),
        )
    }
}
