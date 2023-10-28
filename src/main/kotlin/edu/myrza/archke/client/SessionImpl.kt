package edu.myrza.archke.client

import edu.myrza.archke.util.getBytes
import edu.myrza.archke.util.getPositiveInt
import java.io.BufferedOutputStream
import java.net.Socket
import java.nio.ByteBuffer

class SessionImpl internal constructor(host: String, port: Int) : Session {

    private val socket: Socket

    init {
        socket = Socket(host, port)
    }

    override fun send(msg: String) {
        val payload = msg.toByteArray(Charsets.UTF_8)
        val command = 1.getBytes()
        val length = payload.size.getBytes()
        val response = ByteArray(8)

        val outputStream = BufferedOutputStream(socket.getOutputStream(), HEADER_SIZE + payload.size)
        outputStream.write(command)
        outputStream.flush()
        outputStream.write(length)
        outputStream.flush()
        outputStream.write(payload)
        outputStream.flush()

        val inputStream = socket.getInputStream()
        var bytesRead = 0
        while (bytesRead < 8) {
            bytesRead += inputStream.read(response)
        }

        val buffer = ByteBuffer.wrap(response)
        val code = buffer.getPositiveInt(0)
        val responseLength = buffer.getPositiveInt(4)

        println("SERVER RESPONSE [ code : $code, length : $responseLength ]")
    }

    override fun close() {
        socket.close()
    }

    companion object {

        private const val HEADER_SIZE = 8

    }

}
