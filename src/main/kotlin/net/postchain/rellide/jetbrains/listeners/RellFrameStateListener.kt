package net.postchain.rellide.jetbrains.listeners

import com.intellij.ide.FrameStateListener
import com.intellij.openapi.diagnostic.thisLogger

internal class RellFrameStateListener : FrameStateListener {

    override fun onFrameActivated() {
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }
}
