@file:Suppress("UnstableApiUsage")

package net.postchain.rellide.jetbrains.projectwizard

import com.intellij.ide.wizard.GeneratorNewProjectWizardBuilderAdapter

/** Exposes [ChromiaNewProjectWizard] in the File | New | Project dialog. */
class ChromiaModuleBuilder : GeneratorNewProjectWizardBuilderAdapter(ChromiaNewProjectWizard())
