package net.postchain.rellide.jetbrains.lsp4ij

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.client.IndexAwareLanguageClient

class RellLanguageClient(project: Project) : IndexAwareLanguageClient(project)