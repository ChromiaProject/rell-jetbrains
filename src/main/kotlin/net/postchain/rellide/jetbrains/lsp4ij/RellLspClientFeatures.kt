package net.postchain.rellide.jetbrains.lsp4ij

import com.intellij.openapi.vfs.VirtualFile
import com.redhat.devtools.lsp4ij.client.features.LSPClientFeatures
import java.io.File
import java.net.URI

class RellLspClientFeatures : LSPClientFeatures() {
    override fun keepServerAlive(): Boolean {
        return true
    }

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
