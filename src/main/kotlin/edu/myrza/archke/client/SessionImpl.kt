package edu.myrza.archke.client

import edu.myrza.archke.util.findEnd
import java.net.Socket

class SessionImpl internal constructor(private val socket: Socket) : Session {

    private val inputStream = socket.getInputStream()
    private val outputStream = socket.getOutputStream()
    private val command = "^".toByteArray(Charsets.US_ASCII)
    private val divider = "\r\n".toByteArray(Charsets.US_ASCII)

//      TODO: Test send and flush the divider control symbols separately
//    private val divider1 = "\r".toByteArray(Charsets.US_ASCII)
//    private val divider2 = "\n".toByteArray(Charsets.US_ASCII)

    override fun send(msg: String) {
        // write
        val payload = msg.toByteArray(Charsets.UTF_8)
        val length = payload.size.toString().toByteArray(Charsets.US_ASCII)

        outputStream.write(command)
        outputStream.write(length)
        outputStream.write(divider)
        outputStream.write(payload)
        outputStream.flush()

        // read
        val response = ByteArray(RESPONSE_MAX_SIZE)
        var prevBytesRead = 0
        var bytesRead = 0
        var end = 0

        while (bytesRead < RESPONSE_MAX_SIZE) {
            prevBytesRead = bytesRead
            bytesRead += inputStream.read(response)

            end = response.findEnd(maxOf(prevBytesRead - 1, 0) , bytesRead) ?: continue
            break
        }
        val responseMsg = String(response, 1, end - 1, Charsets.US_ASCII)

        println("SERVER RESPONSE [ msg : $responseMsg ]")
    }

    override fun close() {
        socket.close()
    }

    companion object {
        private const val RESPONSE_MAX_SIZE = 256
    }

}
