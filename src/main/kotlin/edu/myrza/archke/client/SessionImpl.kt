package edu.myrza.archke.client

import edu.myrza.archke.server.Response
import edu.myrza.archke.util.getBytes
import edu.myrza.archke.util.getPositiveInt
import java.net.Socket
import java.nio.ByteBuffer

class SessionImpl internal constructor(private val socket: Socket) : Session {

    private val inputStream = socket.getInputStream()
    private val outputStream = socket.getOutputStream()

    override fun send(msg: String) {
        val payload = msg.toByteArray(Charsets.UTF_8)
        val command = Command.PROCESS.code.getBytes()
        val length = payload.size.getBytes()
        val response = ByteArray(HEADER_SIZE)

        outputStream.write(command)
        outputStream.write(length)
        outputStream.write(payload)
        outputStream.flush()

        var bytesRead = 0
        while (bytesRead < HEADER_SIZE) {
            bytesRead += inputStream.read(response)
        }

        val buffer = ByteBuffer.wrap(response)
        val code = buffer.getPositiveInt(0)
        val responseLength = buffer.getPositiveInt(4)

        println("SERVER RESPONSE [ code : ${Response.byCode(code)}, length : $responseLength ]")
    }

    override fun close() {
        socket.close()
    }

    companion object {
        private const val HEADER_SIZE = 8
    }

}
