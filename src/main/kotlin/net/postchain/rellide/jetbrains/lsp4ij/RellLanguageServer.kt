package net.postchain.rellide.jetbrains.lsp4ij

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.server.JavaProcessCommandBuilder
import com.redhat.devtools.lsp4ij.server.ProcessStreamConnectionProvider

class RellLanguageServer(val project: Project) : ProcessStreamConnectionProvider() {
    private val extraOptions = listOf(
        "-Duser.language=en",
        "-Duser.region=US",
        "-DLspIncludeDefinition=false",
    )

    init {
        val pluginDescriptor = PluginManagerCore.getPlugin(PluginId.getId(PLUGIN_ID))
            ?: throw IllegalStateException("Cannot find plugin by ID: $PLUGIN_ID")
        val lspJarPath = pluginDescriptor.pluginPath.toAbsolutePath()
            .resolve("language-server/rell-language-server-0.4.16.jar")

        val commands = JavaProcessCommandBuilder(project, "Rell")
            .setJar(lspJarPath.toString())
            .create()

        val launchCommands = listOf(commands.first()) + extraOptions + commands.drop(1)
        super.setCommands(launchCommands)
    }

    companion object {
        private const val PLUGIN_ID = "net.postchain.rellide.jetbrains"
    }
}
