package net.postchain.rellide.jetbrains.lsp4ij

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class RellSocketLanguageServer(private val project: Project) : StreamConnectionProvider {
    private var socket: Socket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    
    // These could come from settings/configuration
    private val host = "localhost" 
    private val port = 5008

    override fun start() {
        try {
            socket = Socket(host, port)
            inputStream = socket?.getInputStream()
            outputStream = socket?.getOutputStream()
        } catch (e: IOException) {
            throw RuntimeException("Failed to connect to Rell language server at $host:$port", e)
        }
    }

    override fun getInputStream(): InputStream? = inputStream

    override fun getOutputStream(): OutputStream? = outputStream

    override fun stop() {
        inputStream?.close()
        outputStream?.close()
        socket?.close()
    }

    override fun toString(): String = "Rell Language Server (Socket: $host:$port)"
} 