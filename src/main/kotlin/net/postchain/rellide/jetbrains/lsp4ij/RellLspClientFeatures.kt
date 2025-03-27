package net.postchain.rellide.jetbrains.lsp4ij

import com.redhat.devtools.lsp4ij.client.features.LSPClientFeatures

class RellLspClientFeatures : LSPClientFeatures() {
    override fun keepServerAlive(): Boolean {
        return true
    }
}
