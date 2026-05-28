package net.postchain.rellide.jetbrains.lsp4ij

import com.sun.management.OperatingSystemMXBean
import java.lang.management.ManagementFactory

internal object JvmHeapSizeManager {
    fun getTotalSystemMemoryGB(): Long? {
        return try {
            val os = ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean
            os.totalMemorySize / (1024 * 1024 * 1024)
        } catch (_: Throwable) {
            null
        }
    }

    fun determineMaxHeapSizeMB(): Int? {
        return getTotalSystemMemoryGB()?.let { ramInGB ->
            when (ramInGB) {
                in 0..4 -> 1024
                in 4..8 -> 2048
                else -> 4096
            }
        }
    }
}
