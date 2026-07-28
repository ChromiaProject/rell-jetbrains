package net.postchain.rellide.jetbrains.util

import com.intellij.openapi.vfs.VirtualFile

fun VirtualFile.normalizedUri(): String = this.url.replace("file:///", "file:/")
