package net.postchain.rellide.jetbrains.language

import com.intellij.lang.Commenter

class RellCommenter : Commenter {
    override fun getLineCommentPrefix() = "//"

    override fun getCommentedBlockCommentPrefix() = null
    override fun getCommentedBlockCommentSuffix() = null
    override fun getBlockCommentPrefix() = "/*"
    override fun getBlockCommentSuffix() = "*/"
}
