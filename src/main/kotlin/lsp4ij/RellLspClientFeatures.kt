package net.postchain.rellide.jetbrains.lsp4ij

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.redhat.devtools.lsp4ij.client.features.LSPClientFeatures
import com.redhat.devtools.lsp4ij.client.features.LSPDocumentColorFeature
import java.io.File
import java.net.URI

class RellLspClientFeatures : LSPClientFeatures() {
    init {
        // The Rell language server never provides document colors, so keep the feature
        // permanently disabled: this stops lsp4ij from queuing (and logging warnings for)
        // a textDocument/documentColor request on every highlighting pass.
        setDocumentColorFeature(object : LSPDocumentColorFeature() {
            override fun isEnabled(file: PsiFile): Boolean = false
        })
    }

    override fun keepServerAlive(): Boolean = true

    // On renaming of file, default implementation returned old uri as the new uri
    override fun getFileUri(file: VirtualFile): URI? {
        val diskFile = File(file.path)
        return if (diskFile.exists()) {
            diskFile.toURI()
        } else {
            super.getFileUri(file)
        }
    }
}
